package com.example.finalproject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Contact {
    private String sender_id;
    private String receiver_id;
    private String sender_name;
    private String receiver_name;
    private Map<String, String> messages = new HashMap<String, String>();

    public Contact() { }

    public String getSender_id() {
        return sender_id;
    }

    public String getReceiver_id() {
        return receiver_id;
    }

    public String getSender_name() {
        return sender_name;
    }

    public String getReceiver_name() {
        return receiver_name;
    }

    public Map<String, String> getMessages() {
        return messages;
    }

    public void setSender_id(String sender_id) {
        this.sender_id = sender_id;
    }

    public void setReceiver_id(String receiver_id) {
        this.receiver_id = receiver_id;
    }

    public void setSender_name(String sender_name) {
        this.sender_name = sender_name;
    }

    public void setReceiver_name(String receiver_name) {
        this.receiver_name = receiver_name;
    }

    public void setMessages(Map<String, String> messages) {
        this.messages = messages;
    }
}
