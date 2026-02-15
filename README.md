# ✈️ Travel Agency Management

Sistema full‑stack para gestão de agência de viagens com foco em **backend profissional**, **modelagem de domínio**, **segurança com JWT** e **integração com frontend React**.

---

# 🚀 Tech Stack

## Backend
- Java 17
- Spring Boot
- Spring Security
- JWT Authentication
- JPA / Hibernate
- Maven

## Banco de Dados
- PostgreSQL
- Docker

## Documentos
- Apache PDFBox (geração de contratos em PDF)

## Frontend (integração)
- React
- Axios

---

# 🧠 Arquitetura

Arquitetura em camadas:

```
controller → service → repository → entity → dto → security
```

### Princípios aplicados

- Separação de responsabilidades
- API REST stateless
- Validação de regras de negócio no service
- Controller sem acesso direto ao banco
- DTO para entrada e saída de dados

---

# 🔐 Segurança

Autenticação stateless com JWT.

## 🔄 Fluxo

1. Login → `/auth/login`
2. Backend gera o token
3. Front envia no header:

```
Authorization: Bearer TOKEN
```

4. Filtro JWT valida as requisições

---

## 👤 Usuário padrão

Criado automaticamente ao iniciar a aplicação:

```
login: admin
senha: admin123
```

---

# 🧳 Funcionalidades

## ✈️ Gestão de Viagens

- Criar viagem
- Listar viagens
- Editar viagem
- Remover viagem
- Definir preço por tipo de quarto:
  - CASAL
  - TRIPLO
  - QUADRUPLO
- Estatísticas de passageiros

---

## 👤 Gestão de Passageiros

- Cadastro de passageiros
- Associação à viagem
- Remoção da viagem
- Evita duplicidade

---

## 📄 Gestão de Contratos

### Criação de contratos com:

- Múltiplos passageiros
- Tipo de cobrança:
  - PAYING
  - FREE
- Tipo de quarto
- Observações

### Funcionalidades:

- Adicionar passageiro ao contrato
- Remover passageiro do contrato
- Buscar contrato por ID
- Listar contratos da viagem
- Download do contrato em PDF

---

## 🧾 Geração de PDF

Contrato gerado dinamicamente contendo:

- Dados da viagem
- Dados dos passageiros
- Valores
- Template fixo

---

# 🧱 Modelagem de Domínio

## Customer

Tabela: `tb_customers`

- name
- documentNumber
- birthDate
- phoneNumber

---

## Trip

Tabela: `tb_trips`

- destination
- startDate
- endDate

Relacionamentos:

- TripPassenger
- roomPrices

---

## TripPassenger

Entidade intermediária:

- Trip
- Customer

---

## Contract

Tabela: `tb_contracts`

- Trip
- Valor total
- Lista de passageiros

---

## ContractPassenger

- Contract
- Customer
- ChargeType
- RoomType
- Observações

---

# 📦 DTOs

## Entrada

- TripCreateDto
- CustomerDto
- ContractCreateDto
- ContractCreateWithPassengersDto
- ContractPassengerCreateDto

## Saída

- TripResponseDto
- CustomerResponseDto
- ContractResponseDto
- ContractPassengerResponseDto
- TripPassengerStatsDto

---

# 🌐 Endpoints

## Auth

POST `/auth/login`

---

## Trips

- POST `/trips`
- GET `/trips`
- PUT `/trips/{id}`
- DELETE `/trips/{id}`

---

## Passageiros da viagem

- POST `/trips/{tripId}/passengers`
- GET `/trips/{tripId}/passengers`
- DELETE `/trips/{tripId}/passengers/{customerId}`

---

## Contratos

- POST `/contracts`
- POST `/contracts/with-passengers`
- GET `/contracts/{id}`
- GET `/contracts/trip/{tripId}`
- POST `/contracts/{contractId}/passengers`
- DELETE `/contracts/{contractId}/passengers/{passengerId}`
- GET `/contracts/{id}/pdf`

---

# 🗄️ Banco de Dados

Subido com Docker:

```bash
docker compose up -d
```

### Tabelas

- tb_users
- tb_customers
- tb_trips
- tb_trip_passengers
- tb_contracts
- tb_contract_passengers
- trip_room_prices

---

# ▶️ Como rodar o projeto

## 1️⃣ Subir o banco

```bash
docker compose up -d
```

## 2️⃣ Rodar a aplicação

```bash
./mvnw spring-boot:run
```

---

# 🧪 Testes

- Testes unitários das regras do ContractService

---

# 🌍 Integração com Frontend

CORS configurado para:

```
http://localhost:5173
```

Autenticação via JWT.

---

# 📈 Roadmap

- Pagamentos
- Parcelamento
- Relatórios financeiros
- Upload de comprovantes
- Multi‑usuário com permissões
- Deploy em cloud

---

# 👨‍💻 Autor

Daniel Duarte

Projeto desenvolvido para estudo avançado de:

- Backend com Spring Boot
- Modelagem de domínio
- Segurança com JWT
- Integração full‑stack
