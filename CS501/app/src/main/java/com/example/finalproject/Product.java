package com.example.finalproject;

public class Product {
    private String product_name;
    private String size;
    private String price;
    private String id;
//    private String product_img_url;

    public Product(String product_name, String size, String price, String id) {
        this.product_name = product_name;
        this.size = size;
        this.price = price;
        this.id = id;
//        this.product_img_url = product_img_url;
    }

    public String getProductName() {
        return product_name;
    }

    public String getProductSize() {
        return size;
    }

    public String getProductPrice() {
        return price;
    }

    public String getProductId() { return id; }
//
//    public String getProductImgURL() {
//        return product_img_url;
//    }
}