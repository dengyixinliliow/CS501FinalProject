package com.example.finalproject;

public class Product {
    private String product_name;
    private String product_size;
    private String product_price;
    private String product_id;
    private String product_img_url;

    public Product(String name, String size, String price, String id, String img_url) {
        this.product_name = name;
        this.product_size = size;
        this.product_price = price;
        this.product_id = id;
        this.product_img_url = img_url;
    }

    public String getProductName() {
        return product_name;
    }

    public String getProductSize() {
        return product_size;
    }

    public String getProductPrice() {
        return product_price;
    }

    public String getProductId() { return product_id; }

    public String getProductImgURL() {
        return product_img_url;
    }
}
