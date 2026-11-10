package br.com.ekh.config;

import br.com.ekh.model.Document;
import br.com.ekh.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * DataInitializer responsável por carregar os datasets semente
 * de Engenharia/TI (Fase 1) e Onboarding de RH (Fase 2) na inicialização.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final DocumentRepository documentRepository;

    @Autowired(required = false)
    private VectorStore vectorStore;

    @Override
    public void run(String... args) {
        log.info("Iniciando verificação e carga de dados semente corporativos (EKH-AI)...");
        try {
            initSeedDocument("TI", "engineering_playbook.md", "Playbook de Engenharia & Arquitetura de TI", "ekh-ai/data/seeds/engineering_playbook.md");
            initSeedDocument("RH", "hr_onboarding_guide.md", "Manual de Acolhimento e Integração de Novos Colaboradores", "ekh-ai/data/seeds/hr_onboarding_guide.md");
        } catch (Exception e) {
            log.warn("Carga de sementes finalizada com aviso: {}", e.getMessage());
        }
    }

    private void initSeedDocument(String department, String fileName, String title, String relativePath) {
        if (documentRepository.findAll().stream().anyMatch(d -> fileName.equals(d.getFileName()))) {
            log.info("Documento semente {} já existente no repositório. Pulando.", fileName);
            return;
        }

        Path path = Paths.get(relativePath);
        if (!Files.exists(path)) {
            // Tentativa de busca a partir da raiz do módulo backend ou do repositório
            path = Paths.get("../data/seeds/" + fileName);
        }

        if (!Files.exists(path)) {
            log.info("Arquivo semente não localizado em {}, ignorando seed automático.", relativePath);
            return;
        }

        try {
            String content = Files.readString(path);
            Document doc = Document.builder()
                    .title(title)
                    .fileName(fileName)
                    .content(content)
                    .contentType("text/markdown")
                    .size((long) content.length())
                    .department(department)
                    .confidentialityLevel("INTERNAL")
                    .author("EKH-AI Core")
                    .build();

            doc = documentRepository.save(doc);
            log.info("Documento semente [{}] ({}) salvo com sucesso com ID: {}", title, department, doc.getId());

            if (vectorStore != null) {
                List<String> chunks = chunkText(content, 300, 30);
                Document finalDoc = doc;
                List<org.springframework.ai.document.Document> aiDocuments = chunks.stream()
                        .map(chunk -> new org.springframework.ai.document.Document(chunk, Map.of(
                                "document_id", finalDoc.getId(),
                                "title", finalDoc.getTitle(),
                                "department", department,
                                "confidentiality", "INTERNAL"
                        )))
                        .collect(Collectors.toList());

                vectorStore.add(aiDocuments);
                log.info("Indexados {} chunks vetoriais para o documento semente [{}]", aiDocuments.size(), title);
            }
        } catch (Exception e) {
            log.error("Erro ao processar arquivo semente {}: {}", fileName, e.getMessage(), e);
        }
    }

    private List<String> chunkText(String text, int maxWords, int overlap) {
        if (text == null || text.isBlank()) return List.of();
        String[] words = text.split("\\s+");
        List<String> chunks = new ArrayList<>();
        for (int i = 0; i < words.length; i += (maxWords - overlap)) {
            int end = Math.min(i + maxWords, words.length);
            StringBuilder sb = new StringBuilder();
            for (int j = i; j < end; j++) {
                sb.append(words[j]).append(" ");
            }
            chunks.add(sb.toString().trim());
            if (end == words.length) break;
        }
        return chunks;
    }
}
