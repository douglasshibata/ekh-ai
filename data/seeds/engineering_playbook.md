# 🛠️ Playbook de Engenharia & Arquitetura de TI (EKH-AI Engineering Handbook)

## 1. Visão Geral da Arquitetura Técnica
O ecossistema **Enterprise Knowledge Hub (ekh-ai)** é estruturado segundo os princípios de arquitetura limpa e microsserviços orientados a eventos:
- **Backend Corporativo:** Java 21, Spring Boot 3.4+, Spring Data JPA, Spring AI.
- **Frontend SPA:** Angular 19+ com Angular Signals (`signal`, `computed`, `effect`) e Tailwind CSS.
- **Camada de Dados & Recuperação Híbrida (Hybrid Search RAG):**
  - **Qdrant Vector Database:** Busca vetorial semântica via embeddings (`cosine similarity` com HNSW index).
  - **OpenSearch:** Busca lexical full-text (BM25) para correspondência exata de nomes de classes, tabelas e termos técnicos.
  - **PostgreSQL 15:** Metadados estruturados, controle de acesso RBAC, auditoria e histórico de conversas.
  - **Redis 7:** Cache de tokens, sessões ativas e cache semântico de embeddings.
  - **RabbitMQ:** Fila assíncrona para ingestão pesada de documentos e desacoplamento do pipeline OCR.

---

## 2. Padrões de Código e Diretrizes de Desenvolvimento
1. **Convenções de Código (Clean Code):**
   - Utilização de DTOs e Records no Java para transporte de dados imutáveis.
   - Proibição de lógica de negócio dentro de Controllers (`RestController`). Toda regra pertence às camadas de `Service` ou `Agent`.
   - Logging estruturado via SLF4J com chaves contextuais (`trace_id`, `department`, `query_fingerprint`).
2. **Boas Práticas de Frontend Angular:**
   - Uso estrito de componentes `standalone: true`.
   - Gerenciamento reativo via Signals nativos, evitando vazamento de memória em subscrições RxJS manuais.
   - Adoção de Tailwind CSS para consistência visual responsiva e tema neutro/acessível.

---

## 3. Esteira de CI/CD e Políticas de Deploy
- **Branching Strategy:** Trunk-based development com feature branches curtas (`feature/`, `fix/`, `chore/`).
- **Pipeline de Validação:**
  1. Análise estática de vulnerabilidades e secrets scanning via Git hooks.
  2. Execução da suíte de testes unitários: `mvn clean test`.
  3. Build do pacote de distribuição: `mvn package -DskipTests`.
  4. Geração de imagens Docker multi-stage com base distroless/alpine.
- **Ambientes de Deploy:**
  - `dev`: Atualização contínua a cada merge na branch principal.
  - `staging`: Réplica exata do ambiente de produção com dados anonimizados.
  - `prod`: Deploy canary com aprovação de 4 olhos (Four-Eyes Principle) e verificação de métricas de telemetria.

---

## 4. Runbook de Incidentes & Troubleshooting Operacional
### Incidente A: Qdrant Indisponível / Timeout de Conexão gRPC
- **Sintoma:** Queries de RAG falham com status HTTP 500 ou mensagem de fallback no chat.
- **Diagnóstico:** Verificar conectividade de rede na porta 6334 (gRPC) ou 6333 (HTTP): `curl http://localhost:6333/healthz`.
- **Ação:** Se o container estiver com alta pressão de memória, verificar limites em `docker-compose.yml` e reiniciar o container: `docker compose restart qdrant`.

### Incidente B: Falha na Ingestão de Documentos Pesados (Apache Tika OOM)
- **Sintoma:** JVM lança `OutOfMemoryError` durante parse de arquivos PDF escaneados volumosos.
- **Ação:** Verificar se a flag de OCR `PDFParserConfig.OCR_STRATEGY.OCR_AND_TEXT_EXTRACTION` está configurada com limite de memória alocado para a Tika JVM (`-Xmx2g`).
