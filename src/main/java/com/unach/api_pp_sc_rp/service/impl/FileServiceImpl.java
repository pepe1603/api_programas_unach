package com.unach.api_pp_sc_rp.service.impl;

import com.unach.api_pp_sc_rp.exception.FileException;
import com.unach.api_pp_sc_rp.exception.FileNotFoundException;
import com.unach.api_pp_sc_rp.exception.FileStorageException;
import com.unach.api_pp_sc_rp.service.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Stream;

@Service
public class FileServiceImpl implements FileService {

    private static final Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);
    private final Path rootFolder = Paths.get("uploads");

    @PostConstruct
    public void init() {
        try {
            if (!Files.exists(rootFolder)) {
                Files.createDirectories(rootFolder);
                logger.info("Carpeta de directorio de subidas creada exitosamente.");
            } else {
                logger.info("Carpeta de directorio de subidas ya está disponible.");
            }
        } catch (IOException ex) {
            logger.error("No se pudo crear el directorio de archivos: ", ex);
            throw new FileStorageException("No se pudo crear el directorio de archivos: " + ex.getMessage() +" \n Erorr: " + ex.getMessage());
        }
    }

    @Override
    public void save(MultipartFile file, String filename) {
        validatePdfFile(file);

        try {
            if (file.isEmpty()) {
                String originalFilename = file.getOriginalFilename();
                logger.warn("El archivo [ {} ] está vacío.", originalFilename);
                throw new FileStorageException("El archivo [ " + originalFilename + " ] está vacío.");
            }
            Path destinationFile = rootFolder.resolve(filename);
            Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);
            logger.info("Archivo [ {} ] guardado exitosamente.", filename);
        } catch (IOException ex) {
            logger.error("No se pudo almacenar el archivo [ {} ]: ", file.getOriginalFilename(), ex);
            throw new FileStorageException("No se pudo almacenar el archivo [ " + file.getOriginalFilename() + " ]: " + ex.getMessage()+ " \nError: " +ex.getMessage());
        }
    }

    @Override
    public Resource load(String filename) {
        try {
            Path file = rootFolder.resolve(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() && resource.isReadable()) {
                logger.info("Archivo [ {} ] cargado exitosamente.", filename);
                return resource;
            } else {
                logger.warn("El archivo [ {} ] no existe o no se puede leer.", filename);
                throw new FileNotFoundException("No se pudo leer el archivo: " + filename);
            }
        } catch (MalformedURLException e) {
            logger.error("Error al cargar el archivo [ {} ]: ", filename, e);
            throw new FileNotFoundException("Error al cargar el archivo: " + filename + " ¸n Error: " + e.getMessage());
        }
    }

    @Override
    public void save(List<MultipartFile> files) {
        for (MultipartFile file : files) {
            String filename = getUniqueFilename(file.getOriginalFilename());
            save(file, filename);
        }
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(rootFolder, 1)
                    .filter(path -> !path.equals(rootFolder))
                    .map(rootFolder::relativize);
        } catch (IOException e) {
            logger.error("No se pudieron cargar los archivos: ", e);
            throw new FileStorageException("No se pudieron cargar los archivos: " + e.getMessage() +" : Error: "+e.getMessage());
        }
    }

    @Override
    public void delete(String filename) {
        try {
            Path file = rootFolder.resolve(filename);
            if (Files.deleteIfExists(file)) {
                logger.info("Archivo [ {} ] eliminado exitosamente.", filename);
            } else {
                logger.warn("El archivo [ {} ] no existe y no se pudo eliminar.", filename);
            }
        } catch (IOException ex) {
            logger.error("No se pudo eliminar el archivo [ {} ]: ", filename, ex);
            throw new FileStorageException("No se pudo eliminar el archivo [ " + filename + " ]: " + ex.getMessage());
        }
    }

    @Override
    public String getRutaArchivo(String filename) {
        Path archivoPath = rootFolder.resolve(filename);
        return archivoPath.toString();
    }

    public String getUniqueFilename(String filename) {
        String fileExtension = getFileExtension(filename);
        String baseName = filename.substring(0, filename.length() - fileExtension.length());

        String uniqueFilename = filename;
        int counter = 1;

        while (fileExists(uniqueFilename)) {
            uniqueFilename = baseName + "-" + counter + fileExtension;
            counter++;
        }

        return uniqueFilename;
    }

    private String getFileExtension(String filename) {
        int lastIndexOfDot = filename.lastIndexOf(".");
        if (lastIndexOfDot == -1) {
            return "";
        }
        return filename.substring(lastIndexOfDot);
    }

    private boolean fileExists(String filename) {
        return Files.exists(rootFolder.resolve(filename));
    }

    @Override
    public void updateFile(String oldFileName, String newFileName) {
        Path oldPath = rootFolder.resolve(oldFileName);
        Path newPath = rootFolder.resolve(newFileName);

        try {
            Files.move(oldPath, newPath, StandardCopyOption.REPLACE_EXISTING);
            logger.info("Archivo actualizado de [ {} ] a [ {} ]", oldFileName, newFileName);
        } catch (IOException e) {
            logger.error("Error al actualizar el archivo [ {} ] a [ {} ]: ", oldFileName, newFileName, e);
            throw new FileStorageException("Error al actualizar el archivo [ " + oldFileName + " ] a [ " + newFileName + " ]  \n:"+e.getMessage());
        }
    }

    private void validatePdfFile(MultipartFile file) {
        String filename = file.getOriginalFilename();
        if (filename == null || !filename.toLowerCase().endsWith(".pdf")) {
            logger.warn("El archivo [ {} ] no es un PDF.", filename);
            throw new FileException("Solo se permiten archivos PDF. El archivo [" + filename + "] no es un PDF.");
        }
    }
}
