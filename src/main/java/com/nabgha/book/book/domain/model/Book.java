package com.nabgha.book.book.domain.model;


import java.time.LocalDateTime;

public class Book {

    private final Integer id;
    private String title;
    private String author;
    private String isbn;
    private String synopsis;
    private String bookCover;
    private boolean archived;
    private boolean shareable;
    private final Integer ownerId;
    private final String ownerName;
    private final LocalDateTime createdAt;

    private Book(Builder builder) {
        this.id = builder.id;
        this.title = builder.title;
        this.author = builder.author;
        this.isbn = builder.isbn;
        this.synopsis = builder.synopsis;
        this.bookCover = builder.bookCover;
        this.archived = builder.archived;
        this.shareable = builder.shareable;
        this.ownerId = builder.ownerId;
        this.ownerName = builder.ownerName;
        this.createdAt = builder.createdAt;
    }

    // ---- Factory methods: the only public entry points for construction ----
    public static Book create(String title, String author, String isbn,
            String synopsis, Integer ownerId, String ownerName) {
        return new Builder()
                .title(title)
                .author(author)
                .isbn(isbn)
                .synopsis(synopsis)
                .archived(false)
                .shareable(true)
                .ownerId(ownerId)
                .ownerName(ownerName)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static Book reconstitute(Integer id, String title, String author, String isbn,
                                    String synopsis, String bookCover, boolean archived,
                                    boolean shareable, Integer ownerId, String ownerName,
                                    LocalDateTime createdAt) {
        return new Builder()
                .id(id)
                .title(title)
                .author(author)
                .isbn(isbn)
                .synopsis(synopsis)
                .bookCover(bookCover)
                .archived(archived)
                .shareable(shareable)
                .ownerId(ownerId)
                .ownerName(ownerName)
                .createdAt(createdAt)
                .build();
    }

        // --- domain behavior ---

    public boolean isAvailableForBorrowing() {
        return archived || !shareable;
    }

    public boolean isOwnedBy(Integer userId) {
        return ownerId.equals(userId);
    }

    public void toggleShareable() {
        this.shareable = !this.shareable;
    }

    public void toggleArchived() {
        this.archived = !this.archived;
    }

    public void updateCover(String coverPath) {
        this.bookCover = coverPath;
    }

    // --- getters only, no setters for id/owner (immutable identity) ---
    public Integer getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getIsbn() { return isbn; }
    public String getSynopsis() { return synopsis; }
    public String getBookCover() { return bookCover; }
    public boolean isArchived() { return archived; }
    public boolean isShareable() { return shareable; }
    public Integer getOwnerId() { return ownerId; }
    public String getOwnerName() { return ownerName; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // ---- Builder: private, only used internally by the factory methods ----

    private static class Builder {
        private Integer id;
        private String title;
        private String author;
        private String isbn;
        private String synopsis;
        private String bookCover;
        private boolean archived;
        private boolean shareable;
        private Integer ownerId;
        private String ownerName;
        private LocalDateTime createdAt;

        Builder id(Integer id) { this.id = id; return this; }
        Builder title(String title) { this.title = title; return this; }
        Builder author(String author) { this.author = author; return this; }
        Builder isbn(String isbn) { this.isbn = isbn; return this; }
        Builder synopsis(String synopsis) { this.synopsis = synopsis; return this; }
        Builder bookCover(String bookCover) { this.bookCover = bookCover; return this; }
        Builder archived(boolean archived) { this.archived = archived; return this; }
        Builder shareable(boolean shareable) { this.shareable = shareable; return this; }
        Builder ownerId(Integer ownerId) { this.ownerId = ownerId; return this; }
        Builder ownerName(String ownerName) { this.ownerName = ownerName; return this; }
        Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        Book build() {
            if (title == null || title.isBlank()) {
                throw new IllegalStateException("Book must have a title");
            }
            if (ownerId == null) {
                throw new IllegalStateException("Book must have an owner");
            }
            return new Book(this);
        }
    }
}
