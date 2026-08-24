package com.nabgha.book.feedback.domain.model;


import java.time.LocalDateTime;

public class Feedback {

    private final Integer id;
    private Double note;
    private String comment;
    private final Integer bookId;
    private final Integer createdBy;
    private final LocalDateTime createdAt;

    private Feedback(Integer id, Double note, String comment, Integer bookId,
                     Integer createdBy, LocalDateTime createdAt) {
        this.id = id;
        this.note = note;
        this.comment = comment;
        this.bookId = bookId;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

    public static Feedback create(Double note, String comment, Integer bookId, Integer createdBy) {
        return new Feedback(null, note, comment, bookId, createdBy, LocalDateTime.now());
    }

    public static Feedback reconstitute(Integer id, Double note, String comment,
                                 Integer bookId,Integer createdBy,
                                 LocalDateTime createdAt
    ){
        return new Feedback(id, note, comment, bookId, createdBy, createdAt);
    }

    public boolean isOwnedBy(Integer userId) {
        return createdBy.equals(userId);
    }

    public void updateNote(Double note) {
        this.note = note;
    }

    public void updateComment(String comment) {
        this.comment = comment;
    }

    public Integer getId() { return id; }
    public Double getNote() { return note; }
    public String getComment() { return comment; }
    public Integer getBookId() { return bookId; }
    public Integer getCreatedBy() { return createdBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }

}
