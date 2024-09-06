package com.unach.api_pp_sc_rp.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public interface FileService {

    void save(MultipartFile file, String filename);
    Resource load (String filename);
    void save (List<MultipartFile> files);
    Stream<Path> loadAll();
    void delete(String filename);
    String getRutaArchivo(String filename);

    String getUniqueFilename(String filename);

    void updateFile(String oldFileName, String newFileName);
}
