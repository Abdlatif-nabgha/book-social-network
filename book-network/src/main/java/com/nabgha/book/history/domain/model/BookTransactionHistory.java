package com.nabgha.book.history.domain.model;


import java.time.LocalDateTime;

public class BookTransactionHistory {

    private final Integer id;
    private final Integer bookId;
    private final Integer userId;
    private boolean returned;
    private boolean returnedApproved;
    private final LocalDateTime createdAt;

    private BookTransactionHistory(Integer id, Integer bookId, Integer userId,
                                   boolean returned, boolean returnedApproved, LocalDateTime createdAt){
        this.id = id;
        this.bookId = bookId;
        this.userId = userId;
        this.returned = returned;
        this.returnedApproved = returnedApproved;
        this.createdAt = createdAt;
    }

    public static BookTransactionHistory create(Integer bookId, Integer userId) {
        return new BookTransactionHistory(null, bookId, userId, false, false, LocalDateTime.now());
    }

    public static BookTransactionHistory reconstitute(Integer id, Integer bookId, Integer userId,
                                                      boolean returned, boolean returnedApproved,
                                                      LocalDateTime createdAt) {
        return new BookTransactionHistory(id, bookId, userId, returned, returnedApproved, createdAt);
    }

    public boolean isBorrowedBy(Integer userId) {
        return this.userId.equals(userId);
    }

    public void markReturned() {
        this.returned = true;
    }

    public void approveReturned() {
        this.returnedApproved = true;
    }

    public Integer getId() { return id; }
    public Integer getBookId() { return bookId; }
    public Integer getUserId() { return userId; }
    public boolean isReturned() { return returned; }
    public boolean isReturnedApproved() { return returnedApproved; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
