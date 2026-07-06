package com.researchpapers.model;

public class Paper {
    private int id;
    private String title;
    private String authors;
    private String abstractText;
    private String publicationVenue;
    private int publicationYear;
    private String doi;
    private String filePath;
    private int userId;
    private String category;
    private int rating;

    public Paper(String title, String authors, int publicationYear, int userId) {
        this.title = title;
        this.authors = authors;
        this.publicationYear = publicationYear;
        this.userId = userId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthors() { return authors; }
    public void setAuthors(String authors) { this.authors = authors; }

    public String getAbstractText() { return abstractText; }
    public void setAbstractText(String abstractText) { this.abstractText = abstractText; }

    public String getPublicationVenue() { return publicationVenue; }
    public void setPublicationVenue(String publicationVenue) { this.publicationVenue = publicationVenue; }

    public int getPublicationYear() { return publicationYear; }
    public void setPublicationYear(int publicationYear) { this.publicationYear = publicationYear; }

    public String getDoi() { return doi; }
    public void setDoi(String doi) { this.doi = doi; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
}
