# Enterprise Knowledge Hub (EKH AI)

O **Enterprise Knowledge Hub** é uma plataforma de Inteligência Artificial Corporativa que centraliza todo o conhecimento de uma organização.

## 📋 Tabela de Conteúdos
- [Visão Geral](#-visão-geral)
- [Funcionalidades](#-principais-funcionalidades)
- [Stack Tecnológica](#-stack-tecnológica)
- [Estrutura do Projeto](#-estrutura-do-projeto)
- [Como Iniciar](#-como-iniciar-desenvolvimento)
- [Segurança](#segurança)
- [Licença](#licença)

## 🚀 Visão Geral
Em vez de pesquisar manualmente em sistemas isolados, o usuário faz uma pergunta ao sistema, que utiliza busca híbrida e RAG (Retrieval-Augmented Generation) para gerar uma resposta fundamentada em dados corporativos.

## ✨ Principais Funcionalidades
- **Busca Híbrida Inteligente:** Combinação de busca vetorial (Qdrant) e textual (OpenSearch).
- **Agentes Especializados:** Contextos específicos para Engenharia/TI (`TiAgent`), RH/Onboarding (`HrAgent`), Jurídico, Financeiro e Comercial.
- **Estratégia de Adoção em Duas Fases:**
  - **Fase 1 (Engenharia & TI):** Ingestão do *Playbook de Engenharia & Arquitetura* (`engineering_playbook.md`) para reduzir MTTR, acelerar troubleshooting e documentar runbooks e APIs.
  - **Fase 2 (Onboarding & RH):** Ingestão do *Manual de Acolhimento e Integração* (`hr_onboarding_guide.md`) para reduzir time-to-productivity de novos talentos e democratizar regras de benefícios, férias e conduta.
- **Conectores de Dados:** Integração com SharePoint, SAP e bancos SQL.
- **OCR Inteligente:** Extração de texto via Apache Tika.

## 🛠️ Stack Tecnológica
- **Backend:** Java 21, Spring Boot 3.3.0, Spring AI.
- **Frontend:** Angular 19+, Signals, Tailwind CSS.
- **Dados:** PostgreSQL 15, Qdrant 1.9.0, OpenSearch 2.8.0, Redis, RabbitMQ.

## 📁 Estrutura do Projeto
- `backend-java/`: API Spring Boot e orquestração de IA.
- `frontend-angular/`: Interface de chat e painel administrativo.
- `infra/docker/`: Orquestração de serviços de dados.

## 🚥 Como Iniciar (Desenvolvimento)
1. **Infra**: `cd infra/docker && docker-compose up -d`
2. **Backend**: `cd backend-java && ./mvnw spring-boot:run`
3. **Frontend**: `cd frontend-angular && npm install && npm start`

## Segurança
- **RBAC**: Controle de acesso baseado em funções via Spring Security.
- **Auditoria**: Logs de acesso e de queries de IA sem dados sensíveis.
- **Isolamento**: Filtros de metadados garantem que usuários vejam apenas documentos autorizados.

## Licença
Proprietário / Enterprise

---

## 🛠️ Project Deep Dive & Technical Guide

### 🎯 Main Purpose
Corporate Knowledge Hub using dynamic Multi-Agent RAG architectures.

### 🧰 Tecnologias Utilizadas (Tech Stack)
- **Primary Technologies:** Angular 19.2.2, Docker / Docker Compose, Java 21, Maven, Node.js / npm, Spring Boot 3.4.3
- **Deployment Strategy:** Container-ready with standard Docker/Docker Compose integrations where applicable.

### 🚀 Como Implantar e Executar (How to Deploy & Run)
#### Backend (Java / Spring Boot)
1. Navigate to the backend directory.
2. Compile and run: `./mvnw spring-boot:run` or `mvn spring-boot:run`.

#### Frontend (Angular / Node.js)
1. Navigate to the frontend directory.
2. Install dependencies: `npm install`.
3. Run development server: `npm start` or `npm run dev`.

---
