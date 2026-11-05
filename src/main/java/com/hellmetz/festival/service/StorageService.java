package com.hellmetz.festival.service;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    // Stocke un fichier et retourne le nom unique généré
    String store(MultipartFile file, String subDirectory);

    // Supprime un fichier physique s'il existe
    void delete(String filename, String subDirectory);
}