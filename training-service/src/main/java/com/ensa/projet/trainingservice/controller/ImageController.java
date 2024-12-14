package com.ensa.projet.trainingservice.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    @GetMapping("/{imageName}")
    public ResponseEntity<Resource> getImage(@PathVariable String imageName) {
        try {
            Resource resource = new ClassPathResource("static/images/" + imageName);

            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }
            String contentType = Files.probeContentType(resource.getFile().toPath());
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + imageName + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}