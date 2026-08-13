package com.nabgha.book.book.domain.repository;


public interface FileStoragePort {
    String save(byte[] fileContent, String originalFilename, Integer ownerId);
}
