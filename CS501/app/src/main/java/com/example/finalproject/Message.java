package com.example.finalproject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Message {
    private String seller_id = "null";
    private String renter_id = "null";
    private String product_id = "null";
    private String type = "null";

    public Message() { }

    public String getSeller_id() {
        return seller_id;
    }

    public String getRenter_id() {
        return renter_id;
    }

    public String getProduct_id() {
        return product_id;
    }

    public String getType() {
        return type;
    }

    public void setSeller_id(String seller_id) {
        this.seller_id = seller_id;
    }

    public void setRenter_id(String renter_id) {
        this.renter_id = renter_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public void setType(String type) {
        this.type = type;
    }
}
