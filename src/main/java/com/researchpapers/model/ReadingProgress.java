package com.researchpapers.model;

import java.time.LocalDateTime;

public class ReadingProgress {
    private int id;
    private int paperId;
    private int userId;
    private String status;
    private int currentPage;
    private int totalPages;
    private LocalDateTime lastReadAt;

    // Constructor
    public ReadingProgress(int paperId, int userId, String status) {
        this.paperId = paperId;
        this.userId = userId;
        this.status = status;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPaperId() { return paperId; }
    public void setPaperId(int paperId) { this.paperId = paperId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getCurrentPage() { return currentPage; }
    public void setCurrentPage(int currentPage) { this.currentPage = currentPage; }

    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }

    public LocalDateTime getLastReadAt() { return lastReadAt; }
    public void setLastReadAt(LocalDateTime lastReadAt) { this.lastReadAt = lastReadAt; }
}
