package com.researchpapers.model;

public class Collection {
    private int id;
    private String name;
    private String description;
    private int userId;

    // Constructor
    public Collection(String name, int userId) {
        this.name = name;
        this.userId = userId;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
}
