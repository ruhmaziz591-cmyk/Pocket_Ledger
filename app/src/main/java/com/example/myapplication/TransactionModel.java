// In app/src/main/java/com/example/myapplication/TransactionModel.java

package com.example.myapplication;

import com.google.firebase.firestore.Exclude;

public class TransactionModel {

    // IMPORTANT: Field names must EXACTLY match the keys in your Firestore documents
    // for automatic mapping with toObject() to work.
    private String id;
    private String type;
    private double amount;
    private String category;
    private String date;
    private String note;
    private String userId;

    // IMPORTANT: A public no-argument constructor is required for Firestore's toObject() method.
    public TransactionModel() {
    }

    public TransactionModel(String type, double amount, String category, String date, String note, String userId) {
        this.type = type;
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.note = note;
        this.userId = userId;
    }

    // Use @Exclude on the getter to prevent Firestore from trying to save the ID as a separate field
    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
