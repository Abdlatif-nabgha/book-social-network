package com.nabgha.book.book.infrastructure.file;


public interface FileStoragePort {
    String save(byte[] fileContent, String originalFilename, Integer ownerId);
    byte[] read(String location);
}
