package com.usuario.models.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
public class S3Service {

    private final S3Client s3Client;

    @Value("${app.aws.s3.bucket}")
    private String bucketName;

    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public String subirArchivo(MultipartFile archivo, String carpeta, String tipo) throws IOException {
        
        if (!archivo.getContentType().equals(tipo)) {
            throw new IllegalArgumentException("El archivo debe ser de tipo: " + tipo);
        }

        // Nombre único para evitar colisiones
        String nombreArchivo = carpeta + "/" + UUID.randomUUID() + "_" + archivo.getOriginalFilename();

        // Crear archivo temporal para subir
        Path tempFile = Files.createTempFile("upload-", archivo.getOriginalFilename());
        archivo.transferTo(tempFile.toFile());

        // Construir request de subida
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(nombreArchivo)
                .contentType(archivo.getContentType())
                .build();

        // Subir a S3
        s3Client.putObject(putObjectRequest, tempFile);

        // Borrar archivo temporal
        Files.delete(tempFile);

        // Retorna la URL pública (ajusta si tu bucket no es público)
        return "https://" + bucketName + ".s3.amazonaws.com/" + nombreArchivo;
    }
    
    public void verificarBucket() {
        s3Client.listObjectsV2(b -> b.bucket(bucketName).maxKeys(1));
    }

    public void eliminarArchivo(String key) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        s3Client.deleteObject(deleteObjectRequest);
    }
}