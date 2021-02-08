package com.jrivas.FileCreatorApplication.controller;

import com.jrivas.FileCreatorApplication.data.Article;
import com.jrivas.FileCreatorApplication.data.MarshallingWrapper;
import com.jrivas.FileCreatorApplication.data.Product;
import com.jrivas.FileCreatorApplication.service.Marshalling;
import com.jrivas.FileCreatorApplication.service.ParserEngine;
import com.jrivas.FileCreatorApplication.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class MyRestController {
    @Autowired
    private ProductService service;

    /**
     * Retrieve a JSON list of the products contained in an HTML file.
     *
     * @return Product List.
     */
    @GetMapping("/getProducts")
    public List<Product> getProducts() {
        ParserEngine parser = new ParserEngine();
        return parser.extractProducts(parser.extractHTML(ParserEngine.CORTE_INGLES_URI));
    }

    /**
     * Filter the list results by brand and/or if it is on discount or not.
     *
     * @param brand    product's brand.
     * @param discount it is true if it is on discount and false if it is not.
     * @return Product List.
     */
    @GetMapping("/getRequestedProducts")
    public List<Product> getProducts(@RequestParam(value = "brand", required = false) String brand,
                                     @RequestParam(value = "onDiscount", required = false) Boolean discount) {
        ParserEngine parser = new ParserEngine();
        return parser.returnRequestedProducts(brand, discount);
    }

    /**
     * Retrieve a list with the articles in the web page.
     *
     * @return Article class List.
     */
    @GetMapping("/getArticles")
    public List<Article> getArticles() {
        ParserEngine parser = new ParserEngine();
        MarshallingWrapper wrapper = new MarshallingWrapper();
        List<Article> articleList = parser.extractArticles(parser.extractHTML(ParserEngine.ULTIMA_HORA_URI));
        wrapper.setArticleList(articleList);
        String string = Marshalling.marshall2JSON(wrapper);
        String fileName = String.format("%s\\%s\\%s", Marshalling.USER_PATH, Marshalling.DIRECTORY_NAME,
                Marshalling.FILE_NAME);
        Marshalling.generateFile(fileName, string);
        return articleList;
    }

    /**
     * Retrieve a refreshed list of the articles.
     *
     * @return Article class List.
     */
    @GetMapping("/getNewArticles")
    public List<Article> getNewArticles() {
        String fileName = String.format("%s\\%s\\%s", Marshalling.USER_PATH, Marshalling.DIRECTORY_NAME,
                Marshalling.FILE_NAME);
        ParserEngine parser = new ParserEngine();
        List<Article> newArticleList = parser.extractArticles(parser.extractHTML(ParserEngine.ULTIMA_HORA_URI));
        MarshallingWrapper wrapper = Marshalling.unmarshallJSON(Marshalling.readFile(fileName), MarshallingWrapper.class);
        List<Article> oldArticleList = wrapper.getArticleList();
        if (Marshalling.checkNewArticles(newArticleList, oldArticleList)) {
            wrapper.setArticleList(newArticleList);
            Marshalling.generateFile(fileName, Marshalling.marshall2JSON(wrapper));
        }
        return newArticleList;
    }

    @PostMapping("/addProduct")
    public Product addProduct(@RequestBody Product product) {
        return service.saveProduct(product);
    }

    @GetMapping("/addProducts")
    public List<Product> addProducts() {
        return service.saveProducts(this.getProducts());
    }

    @GetMapping("/products")
    public List<Product> findAllProducts() {
        return service.getProducts();
    }

    @GetMapping("/productById/{id}")
    public Product findProductById(@PathVariable int id) {
        return service.getProductById(id);
    }

    @GetMapping("/productByBrand/{brand}")
    public Product findProductByBrand(@PathVariable String brand) {
        return service.getProductByBrand(brand);
    }

    @PutMapping("/update")
    public Product updateProduct(@RequestBody Product product) {
        return service.updateProduct(product);
    }

    @DeleteMapping("/delete/{id}")
    public String deleteProduct(@PathVariable int id) {
        return service.deleteProduct(id);
    }
}
