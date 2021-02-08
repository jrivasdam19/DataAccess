package com.jrivas.FileCreatorApplication.data;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.List;

@XmlRootElement(name = "wrapper")
@XmlAccessorType(XmlAccessType.FIELD)
public class MarshallingWrapper implements Serializable {
    @XmlElementWrapper(name = "articles")
    @XmlElement(name = "article")
    private List<Article> articleList;

    @XmlElement(name = "value")
    private Double value;

    private void setValue(Double value) {
        this.value = value;
    }

    public List<Article> getArticleList() {
        return articleList;
    }

    public void setArticleList(List<Article> articleList) {
        this.articleList = articleList;
    }
}
