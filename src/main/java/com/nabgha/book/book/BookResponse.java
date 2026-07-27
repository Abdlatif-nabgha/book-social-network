package com.nabgha.book.book;

import com.nabgha.book.user.User;
import lombok.Builder;

@Builder
public record BookResponse(
        Integer id,
        String title,
        String author,
        String isbn,
        String synopsis,
        boolean shareable,
        boolean archived,
        Integer ownerId,
        String owner,
        String bookCover,
        double rate
) {}