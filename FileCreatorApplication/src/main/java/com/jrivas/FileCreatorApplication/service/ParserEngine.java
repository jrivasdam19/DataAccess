package com.jrivas.FileCreatorApplication.service;

import com.jrivas.FileCreatorApplication.data.Article;
import com.jrivas.FileCreatorApplication.data.Product;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ParserEngine {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public static final String CORTE_INGLES_URI = "https://www.elcorteingles.es/electronica/ordenadores/portatiles/?s=portatil&hierarchy=&deep_search=&stype=search_redirect";
    public static final String ULTIMA_HORA_URI = "https://www.ultimahora.es/noticias.html";

    private static final String PRODUCT_LIST = ".products_list-item";
    private static final String PRODUCT_BRAND = ".product_preview-brand";
    private static final String PRODUCT_TITLE = ".product_preview-desc";
    private static final String PRODUCT_DISCOUNT_PRICE = ".price._big._sale";
    private static final String PRODUCT_PRICE = ".price._big";
    private static final String HYPERLINK_TAG = "a";
    private static final String ABS_HREF = "abs:href";

    private static final String ARTICLE_TAG = "article";
    private static final String ARTICLE_INFO_BOX = ".autor.autor_extras.pull-left";
    private static final String ARTICLE_URI = "header > h3 > a";

    public ParserEngine() {

    }

    /**
     * Retrieve the HTML from the given URI.
     *
     * @return parsed Document.
     */
    public Document extractHTML(String uri) {
        Document doc = null;
        try {
            doc = Jsoup.connect(uri).get();
        } catch (IOException e) {
            log.error("Error extracting Document", e);
        }
        return doc;
    }

    /**
     * Extract a list with requested information from the article HTML and save it in Article class (Java Bean).
     *
     * @param doc parsed Document (extracted HTML).
     */
    public List<Article> extractArticles(Document doc) {
        String articleUri;
        Elements articles = doc.select(ARTICLE_TAG);
        ArrayList<Article> articleList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            articleUri = articles.get(i).select(ARTICLE_URI).attr(ABS_HREF);
            if (!articleUri.equals(StringUtils.EMPTY)) {
                Article newArticle = new Article();
                newArticle.setTitle(articles.get(i).select(ARTICLE_URI).text());
                newArticle.setUri(articleUri);
                extractDate(articleUri, newArticle);
                articleList.add(newArticle);
            }
        }
        return articleList;
    }

    /**
     * Extract HTML from a particular article to retrieve its date.
     *
     * @param articleUri uri to an article.
     * @param newArticle new Article class instance.
     */
    public void extractDate(String articleUri, Article newArticle) {
        Document doc = extractHTML(articleUri);
        Elements article = doc.select(ARTICLE_INFO_BOX);
        for (Element element : article) {
            newArticle.setDate(article.select("time").attr("datetime"));
        }
    }

    /**
     * Extract a list with requested information from the product HTML and save it in Product class (Java Bean).
     *
     * @param doc parsed Document (extracted HTML).
     * @return product list.
     */
    public List<Product> extractProducts(Document doc) {
        Elements productList = doc.select(PRODUCT_LIST);
        ArrayList<Product> productsArray = new ArrayList<>();
        for (Element product : productList) {
            Product newProduct = new Product();
            newProduct.setBrand(StringUtils.lowerCase(product.select(PRODUCT_BRAND).text()));
            newProduct.setTitle(product.select(PRODUCT_TITLE).text());
            newProduct.setPrice(setPriceAndDiscount(newProduct, product));
            newProduct.setUrl(product.select(HYPERLINK_TAG).attr(ABS_HREF));
            productsArray.add(newProduct);
        }
        return productsArray;
    }

    /**
     * Filter the product list according to the requested parameters.
     *
     * @param brand    name of the product brand.
     * @param discount True if product is on discount.
     * @return the filtered product list.
     */
    public List<Product> returnRequestedProducts(String brand, Boolean discount) {
        ArrayList<Product> products = new ArrayList<>();
        if (brand.equals(StringUtils.EMPTY) && discount == null) {
            return extractProducts(extractHTML(CORTE_INGLES_URI));
        } else if (!brand.equals(StringUtils.EMPTY) && discount != null) {
            for (Product product : extractProducts(extractHTML(CORTE_INGLES_URI))) {
                if (product.getBrand().equals(StringUtils.lowerCase(brand)) &&
                        product.isOnDiscount() == discount) {
                    products.add(product);
                }
            }
            return products;
        } else if (brand.equals(StringUtils.EMPTY) && discount != null) {
            for (Product product : extractProducts(extractHTML(CORTE_INGLES_URI))) {
                if (product.isOnDiscount() == discount) {
                    products.add(product);
                }
            }
            return products;
        } else if (!brand.equals(StringUtils.EMPTY) && discount == null) {
            for (Product product : extractProducts(extractHTML(CORTE_INGLES_URI))) {
                if (product.getBrand().equals(StringUtils.lowerCase(brand))) {
                    products.add(product);
                }
            }
            return products;
        } else {
            return null;
        }
    }

    /**
     * Checks whether the product is on discount or not.
     *
     * @param newProduct new Product class instance
     * @param product    requested Element from HTML Document.
     */
    public String setPriceAndDiscount(Product newProduct, Element product) {
        String price;
        if (!product.select(PRODUCT_DISCOUNT_PRICE).isEmpty()) {
            newProduct.setOnDiscount(true);
            price = product.select(PRODUCT_DISCOUNT_PRICE).text();
        } else {
            newProduct.setOnDiscount(false);
            price = product.select(PRODUCT_PRICE).text();
        }
        return price;
    }
}
