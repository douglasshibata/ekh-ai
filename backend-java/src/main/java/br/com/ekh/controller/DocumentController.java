package br.com.ekh.controller;

import br.com.ekh.model.Document;
import br.com.ekh.service.IngestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DocumentController {

    private final IngestionService ingestionService;

    @PostMapping("/upload")
    public ResponseEntity<Document> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("department") String department,
            @RequestParam("confidentiality") String confidentiality) throws IOException {

        Document doc = ingestionService.ingest(file, department, confidentiality);
        return ResponseEntity.ok(doc);
    }
}
