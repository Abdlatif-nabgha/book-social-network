package com.nabgha.book.book.domain.usecase;


import com.nabgha.book.book.domain.exception.BookNotFoundException;
import com.nabgha.book.book.domain.exception.BookOperationNotPermittedException;
import com.nabgha.book.book.domain.model.Book;
import com.nabgha.book.book.domain.repository.BookRepository;
import com.nabgha.book.book.infrastructure.file.FileStoragePort;

public class UploadBookCoverUseCase {
    private final BookRepository bookRepository;
    private final FileStoragePort fileStoragePort;

    public UploadBookCoverUseCase(BookRepository bookRepository, FileStoragePort fileStoragePort) {
        this.bookRepository = bookRepository;
        this.fileStoragePort = fileStoragePort;
    }

    public Book execute(Integer bookId, byte[] fileContent, String originalFilename, Integer connectedUserId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));

        if (!book.isOwnedBy(connectedUserId)) {
            throw new BookOperationNotPermittedException(
                    "You cannot upload a cover for a book you don't own");
        }
        String storedPath = fileStoragePort.save(fileContent, originalFilename, connectedUserId);
        if (storedPath == null) {
            throw new IllegalStateException("Failed to store the book cover file");
        }
        book.updateCover(storedPath);
        return bookRepository.save(book);
    }
}
