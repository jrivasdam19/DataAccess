package com.jrivas.FileCreatorApplication.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;
import com.jrivas.FileCreatorApplication.data.Article;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;


public class Marshalling {
    private static Logger log = LoggerFactory.getLogger(Marshalling.class);
    public static final String USER_PATH = System.getProperty("user.home");
    public static final String DIRECTORY_NAME = "Noticias";
    public static final String FILE_NAME = "Articulos.json";

    /**
     * Generate a plain text file.
     *
     * @param path    file location.
     * @param content file content.
     * @return plain text file.
     */
    public static File generateFile(String path, String content) {
        FileWriter fileWriter = null;
        PrintWriter printWriter = null;
        File file = null;
        try {
            file = new File(path);
            file.getParentFile().mkdirs();
            fileWriter = new FileWriter(path);
            printWriter = new PrintWriter(fileWriter);
            printWriter.println(content);
        } catch (IOException e) {
            log.error("i/o ERROR ", e);
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    log.error("i/o ERROR ", e);
                }
            }
            return file;
        }
    }

    /**
     * Generate an Objectmapper to provide functionality for reading and writing JSON files.
     *
     * @return Objectmapper object
     */
    private static ObjectMapper getMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(MapperFeature.USE_WRAPPER_NAME_AS_PROPERTY_NAME);
        AnnotationIntrospector introspector = new JaxbAnnotationIntrospector(mapper.getTypeFactory());
        mapper.setAnnotationIntrospector(introspector);
        mapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper;
    }

    /**
     * Marshall the Java Bean content.
     *
     * @param item an item to be marshalled.
     * @param <T>  generic method.
     * @return String content.
     */
    public static <T extends Serializable> String marshall2JSON(T item) {
        try {
            return getMapper().writeValueAsString(item);
        } catch (Exception e) {
            log.error("Marshalling to string failure", e);
            return StringUtils.EMPTY;
        }
    }

    /**
     * Unmarshall String content.
     *
     * @param content           String plain text content.
     * @param typeArgumentClass String content will be converted to this class type.
     * @param <T>               generic method.
     * @return
     */
    public static <T extends Serializable> T unmarshallJSON(String content, Class<T> typeArgumentClass) {
        try {
            return getMapper().readValue(content, typeArgumentClass);
        } catch (Exception e) {
            log.error("Unmarshalling string failure", e);
        }
        return null;
    }

    /**
     * Read plain text file content.
     *
     * @param filePath file path location.
     * @return String content.
     */
    public static String readFile(String filePath) {
        String content = StringUtils.EMPTY;
        BufferedReader buffer = null;
        try {
            File file = new File(filePath);
            FileReader fileReader = new FileReader(file);
            buffer = new BufferedReader(fileReader);
            String fileLine = null;
            while ((fileLine = buffer.readLine()) != null) {
                content += fileLine;
            }
        } catch (Exception e) {
            log.error("Can't access to file", e);
        } finally {
            if (buffer != null)
                try {
                    buffer.close();
                } catch (Exception e) {
                    log.error("Can't close file", e);
                }
        }
        return content;
    }

    /**
     * Check if there are new Articles in the web page.
     *
     * @param newArticleList current Article list.
     * @param oldArticleList persisted Article list.
     * @return true if there are new Articles and false instead.
     */
    public static boolean checkNewArticles(List<Article> newArticleList, List<Article> oldArticleList) {
        boolean newArticles;
        if (StringUtils.equals(newArticleList.get(0).getTitle(), oldArticleList.get(0).getTitle())) {
            newArticles = false;
        } else {
            newArticles = true;
        }
        return newArticles;
    }

    /**
     * Retrieve a list with the new articles.
     *
     * @param newArticleList current Article list.
     * @param oldArticleList persisted Article list.
     * @return Article List with the new Article.
     */
    public static List<Article> retrieveNewArticles(List<Article> newArticleList, List<Article> oldArticleList) {
        int counter = 0;
        boolean sameNew = false;
        while (!sameNew) {
            sameNew = StringUtils.equals(newArticleList.get(counter).getTitle(), oldArticleList.get(0).getTitle());
            counter++;
            if (counter >= newArticleList.size()) {
                sameNew = true;
            }
        }
        if (counter == 1) {
            newArticleList.clear();
            newArticleList.add(new Article("No new Articles", "No date", "No uri"));
        } else if (counter < newArticleList.size()) {
            for (int i = counter; i < newArticleList.size(); i++) {
                newArticleList.remove(i);
            }
        }
        return newArticleList;
    }
}
