package br.com.ekh.service;

import br.com.ekh.model.Document;
import br.com.ekh.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.ocr.TesseractOCRConfig;
import org.apache.tika.parser.pdf.PDFParserConfig;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class IngestionService {

    private final DocumentRepository documentRepository;
    private final VectorStore vectorStore;

    public Document ingest(MultipartFile file, String department, String confidentialityLevel) throws IOException {
        log.info("Starting professional ingestion for: {}", file.getOriginalFilename());

        String content = extractTextWithOcr(file);

        Document doc = Document.builder()
                .title(file.getOriginalFilename())
                .fileName(file.getOriginalFilename())
                .content(content)
                .contentType(file.getContentType())
                .size(file.getSize())
                .department(department)
                .confidentialityLevel(confidentialityLevel)
                .build();

        doc = documentRepository.save(doc);

        // Improved Chunking: Token-aware simulated approach
        List<String> chunks = semanticChunking(content, 500, 50);

        Document finalDoc = doc;
        List<org.springframework.ai.document.Document> aiDocuments = chunks.stream()
                .map(chunk -> new org.springframework.ai.document.Document(chunk, Map.of(
                        "document_id", finalDoc.getId(),
                        "title", finalDoc.getTitle(),
                        "department", department,
                        "confidentiality", confidentialityLevel
                )))
                .collect(Collectors.toList());

        vectorStore.add(aiDocuments);

        return doc;
    }

    private String extractTextWithOcr(MultipartFile file) throws IOException {
        Parser parser = new AutoDetectParser();
        BodyContentHandler handler = new BodyContentHandler(-1);
        Metadata metadata = new Metadata();
        ParseContext context = new ParseContext();

        TesseractOCRConfig config = new TesseractOCRConfig();
        context.set(TesseractOCRConfig.class, config);

        PDFParserConfig pdfConfig = new PDFParserConfig();
        pdfConfig.setExtractInlineImages(true);
        pdfConfig.setOcrStrategy(PDFParserConfig.OCR_STRATEGY.OCR_AND_TEXT_EXTRACTION);
        context.set(PDFParserConfig.class, pdfConfig);

        try (InputStream stream = file.getInputStream()) {
            parser.parse(stream, handler, metadata, context);
            return handler.toString();
        } catch (Exception e) {
            throw new IOException("OCR/Parsing failed", e);
        }
    }

    private List<String> semanticChunking(String text, int maxWords, int overlap) {
        if (text == null || text.isBlank()) return List.of();

        String[] words = text.split("\\s+");
        List<String> chunks = new ArrayList<>();

        for (int i = 0; i < words.length; i += (maxWords - overlap)) {
            int end = Math.min(i + maxWords, words.length);
            StringBuilder chunk = new StringBuilder();
            for (int j = i; j < end; j++) {
                chunk.append(words[j]).append(" ");
            }
            chunks.add(chunk.toString().trim());
            if (end == words.length) break;
        }

        return chunks;
    }
}
