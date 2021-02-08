package com.jrivas.FileCreatorApplication.data;

import javax.persistence.*;

@Entity
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name="title")
    private String title;
    @Column(name="price")
    private String price;
    @Column(name="brand")
    private String brand;
    @Column(name="url")
    private String url;
    @Column(name="onDiscount")
    private boolean onDiscount;

    public Product() {

    }

    public Product(String title, String price, String brand, String url, boolean onDiscount) {
        this.title = title;
        this.price = price;
        this.brand = brand;
        this.url = url;
        this.onDiscount = onDiscount;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isOnDiscount() {
        return onDiscount;
    }

    public void setOnDiscount(boolean onDiscount) {
        this.onDiscount = onDiscount;
    }
}
