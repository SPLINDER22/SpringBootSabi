package com.sabi.sabi.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

/**
 * Servicio para manejar la subida de archivos
 * Usa Cloudinary si está configurado, sino guarda localmente
 */
@Service
public class FileStorageService {

    @Autowired(required = false)
    private Cloudinary cloudinary;

    private static final String LOCAL_UPLOAD_DIR = "uploads";

    /**
     * Guarda un archivo (imagen o video)
     * @param file El archivo a subir
     * @param folder La carpeta donde guardar (perfiles, diagnosticos, ejercicios, etc.)
     * @return La URL pública del archivo
     */
    public String guardarArchivo(MultipartFile file, String folder) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        // Si Cloudinary está configurado, usarlo
        if (cloudinary != null) {
            return subirACloudinary(file, folder);
        } else {
            // Fallback: guardar localmente (temporal, se borra al redesplegar)
            return guardarLocalmente(file, folder);
        }
    }

    /**
     * Sube un archivo a Cloudinary
     */
    private String subirACloudinary(MultipartFile file, String folder) throws IOException {
        try {
            // Convertir MultipartFile a File temporal
            File tempFile = convertirMultipartFileAFile(file);

            // Subir a Cloudinary
            Map uploadResult = cloudinary.uploader().upload(tempFile, ObjectUtils.asMap(
                "folder", "sabi/" + folder,
                "resource_type", "auto", // Detecta automáticamente si es imagen o video
                "public_id", UUID.randomUUID().toString()
            ));

            // Eliminar archivo temporal
            tempFile.delete();

            // Retornar URL segura
            String url = (String) uploadResult.get("secure_url");
            System.out.println("✅ Archivo subido a Cloudinary: " + url);
            return url;

        } catch (Exception e) {
            System.err.println("❌ Error subiendo a Cloudinary: " + e.getMessage());
            e.printStackTrace();
            // Fallback a almacenamiento local
            return guardarLocalmente(file, folder);
        }
    }

    /**
     * Guarda un archivo localmente (temporal)
     */
    private String guardarLocalmente(MultipartFile file, String folder) throws IOException {
        // Crear directorio si no existe
        Path uploadPath = Paths.get(LOCAL_UPLOAD_DIR, folder);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generar nombre único
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".")
            ? originalFilename.substring(originalFilename.lastIndexOf("."))
            : "";
        String filename = UUID.randomUUID().toString() + extension;

        // Guardar archivo
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath);

        String url = "/" + LOCAL_UPLOAD_DIR + "/" + folder + "/" + filename;
        System.out.println("⚠️ Archivo guardado localmente (temporal): " + url);
        return url;
    }

    /**
     * Convierte MultipartFile a File temporal
     */
    private File convertirMultipartFileAFile(MultipartFile multipartFile) throws IOException {
        File file = File.createTempFile("temp", null);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(multipartFile.getBytes());
        }
        return file;
    }

    /**
     * Elimina un archivo de Cloudinary
     * @param url La URL del archivo a eliminar
     */
    public void eliminarArchivo(String url) {
        if (url == null || url.isEmpty()) {
            return;
        }

        // Solo intentar eliminar si es una URL de Cloudinary
        if (cloudinary != null && url.contains("cloudinary.com")) {
            try {
                // Extraer public_id de la URL
                // URL ejemplo: https://res.cloudinary.com/dxxxxx/image/upload/v123456/sabi/perfiles/uuid.jpg
                String[] parts = url.split("/");
                if (parts.length >= 3) {
                    // Buscar el índice donde empieza "sabi"
                    int sabiIndex = -1;
                    for (int i = 0; i < parts.length; i++) {
                        if (parts[i].equals("sabi")) {
                            sabiIndex = i;
                            break;
                        }
                    }

                    if (sabiIndex != -1 && sabiIndex + 2 < parts.length) {
                        // Construir public_id: sabi/folder/filename (sin extensión)
                        String filename = parts[parts.length - 1];
                        String filenameWithoutExt = filename.contains(".")
                            ? filename.substring(0, filename.lastIndexOf("."))
                            : filename;
                        String publicId = "sabi/" + parts[sabiIndex + 1] + "/" + filenameWithoutExt;

                        cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
                        System.out.println("✅ Archivo eliminado de Cloudinary: " + publicId);
                    }
                }
            } catch (Exception e) {
                System.err.println("⚠️ Error eliminando archivo de Cloudinary: " + e.getMessage());
                // No lanzar excepción, solo logear
            }
        } else {
            // Eliminar archivo local
            try {
                Path filePath = Paths.get(url.substring(1)); // Quitar el "/" inicial
                if (Files.exists(filePath)) {
                    Files.delete(filePath);
                    System.out.println("✅ Archivo local eliminado: " + url);
                }
            } catch (IOException e) {
                System.err.println("⚠️ Error eliminando archivo local: " + e.getMessage());
            }
        }
    }

    /**
     * Verifica si Cloudinary está configurado
     */
    public boolean isCloudinaryConfigured() {
        return cloudinary != null;
    }
}

