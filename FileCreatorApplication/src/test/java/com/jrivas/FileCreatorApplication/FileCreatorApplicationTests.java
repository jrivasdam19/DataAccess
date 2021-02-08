package com.jrivas.FileCreatorApplication;

import com.jrivas.FileCreatorApplication.data.Article;
import com.jrivas.FileCreatorApplication.data.MarshallingWrapper;
import com.jrivas.FileCreatorApplication.service.Marshalling;
import com.jrivas.FileCreatorApplication.service.ParserEngine;
import com.jrivas.FileCreatorApplication.service.XMLBuilder;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;
import org.w3c.dom.Document;

import java.io.File;
import java.io.IOException;
import java.util.List;

@SpringBootTest
class FileCreatorApplicationTests {
    private static final Logger log = LoggerFactory.getLogger(FileCreatorApplicationTests.class);
    private static final String FILE_NAME = String.format("%s\\%s\\%s", Marshalling.USER_PATH, Marshalling.DIRECTORY_NAME,
            Marshalling.FILE_NAME);

    @Test
    void tryBuildingXML() {
        XMLBuilder xmlBuilder = new XMLBuilder();
        try {
            xmlBuilder.readFile(String.format("%s\\%s\\%s", System.getProperty(XMLBuilder.HOME)
                    , XMLBuilder.DESKTOP, XMLBuilder.CSV_DEFAULT_FILE_PATH));
            Document document = xmlBuilder.generateRandomProductXML();
            File file = xmlBuilder.saveDocumentAsFile(String.format("%s\\%s\\%s", System.getProperty(XMLBuilder.HOME)
                    , XMLBuilder.DESKTOP, XMLBuilder.XML_DEFAULT_FILE_PATH), document, true);
            Assert.notNull(file, XMLBuilder.errorMessages[3]);
        } catch (IOException e) {
            log.error(XMLBuilder.errorMessages[0], e);
        } catch (javax.xml.parsers.ParserConfigurationException e) {
            log.error(XMLBuilder.errorMessages[1], e);
        } catch (javax.xml.transform.TransformerException e) {
            log.error(XMLBuilder.errorMessages[2], e);
        }
    }

    @Test
    void tryGettingHtml() {
        ParserEngine parser = new ParserEngine();
        Assert.notNull(parser.extractHTML(ParserEngine.CORTE_INGLES_URI), "null Document");
    }

    @Test
    void tryCreateFile() {
        String fileName = String.format("%s\\.fbmoll\\test.txt", System.getProperty("user.home"));
        File file = Marshalling.generateFile(fileName, "Hello World!");
        Assert.notNull(file, "Null file!");
    }

    @Test
    void tryJSON() {
        ParserEngine parser = new ParserEngine();
        MarshallingWrapper marshallingWrapper = new MarshallingWrapper();
        marshallingWrapper.setArticleList(parser.extractArticles(parser.extractHTML(ParserEngine.ULTIMA_HORA_URI)));
        String content = Marshalling.marshall2JSON(marshallingWrapper);
        Assert.notNull(content, "Example rules");
        Marshalling.generateFile(FILE_NAME, content);
    }

    @Test
    void tryUnmarshalling() {
        ParserEngine parser = new ParserEngine();
        List<Article> articleList = parser.extractArticles(parser.extractHTML(ParserEngine.ULTIMA_HORA_URI));
        String firstArticleTitle = articleList.get(0).getTitle();
        String content = Marshalling.readFile(FILE_NAME);
        MarshallingWrapper wrapper = Marshalling.unmarshallJSON(content, MarshallingWrapper.class);
        boolean isSameFirstTitle = StringUtils.equals(firstArticleTitle,wrapper.getArticleList().get(0).getTitle());
        Assert.isTrue(isSameFirstTitle,"Not the same title.");
    }
}
