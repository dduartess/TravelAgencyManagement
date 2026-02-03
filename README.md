# ✈️ Travel Agency Management --- MVP

Sistema de gestão para agência de viagens desenvolvido com **Spring
Boot + JPA (Hibernate) + PostgreSQL**, com **Spring Security + JWT** e
integração real com **frontend React**.

Foco em **modelagem de domínio**, **boas práticas de arquitetura**,
**segurança** e **integração full-stack**.

------------------------------------------------------------------------

## 🎯 Objetivo do sistema

O sistema permite:

-   Cadastro de passageiros
-   Criação de viagens
-   Definição de preços por tipo de quarto
-   Associação de passageiros às viagens
-   Edição e remoção de passageiros da viagem
-   Consulta de passageiros por viagem (com estatísticas)
-   Autenticação segura com JWT
-   Arquitetura organizada em **Entity → DTO → Service → Controller →
    Security**

------------------------------------------------------------------------

## 🧱 Entidades do Domínio

### 👤 Customer (Passageiro)

**Tabela:** `tb_customers`

  ------------------------------------------------------------------------
  Campo            Tipo        Regra
  ---------------- ----------- -------------------------------------------
  id               Long        PK, auto gerado

  name             String      Obrigatório, apenas letras

  documentNumber   String      Obrigatório, apenas números (7--20)

  birthDate        LocalDate   Obrigatório, passado ou presente

  phoneNumber      String      Obrigatório, 11 dígitos
  ------------------------------------------------------------------------

Relacionamento: `ManyToMany` com Trip.

------------------------------------------------------------------------

### 🧳 Trip (Viagem)

**Tabela:** `tb_trips`

  Campo         Tipo        Regra
  ------------- ----------- -----------------
  id            Long        PK, auto gerado
  destination   String      Obrigatório
  startDate     LocalDate   Hoje ou futuro
  endDate       LocalDate   ≥ startDate

------------------------------------------------------------------------

### 🛏️ Preços por tipo de quarto

``` java
Map<RoomType, BigDecimal> roomPrices;
```

**Tabela:** `trip_room_prices`

| trip_id \| room_type \| price \|

Enum `RoomType`: CASAL, TRIPLO, QUADRUPLO

------------------------------------------------------------------------

### 👥 Relação Trip ↔ Customer

**Tabela:** `trip_passengers`

| trip_id \| customer_id \|

Implementado com `Set<Customer>` para evitar duplicidade.

------------------------------------------------------------------------

## 📦 DTOs

### Entrada

-   `TripCreateDto`
-   `CustomerDto`

### Saída

-   `TripResponseDto`
-   `CustomerResponseDto`
-   `TripPassengerStatsDto`

------------------------------------------------------------------------

## ⚙️ Regras de Negócio

-   Datas da viagem validadas
-   Preços obrigatórios por quarto
-   Passageiros não duplicam na viagem
-   Passageiros podem ser editados e removidos da viagem

------------------------------------------------------------------------

## 🔐 Segurança --- Spring Security + JWT

O sistema utiliza autenticação **stateless** com JWT.

Fluxo:

1.  Login com username e senha
2.  Backend gera token JWT
3.  Frontend envia token no header Authorization
4.  Filtro JWT autentica as requisições protegidas

Endpoint de login:

**POST `/auth/login`**

``` json
{
  "username": "admin",
  "password": "123456"
}
```

Resposta:

``` json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

------------------------------------------------------------------------

## 🌐 Endpoints

-   POST `/trips`
-   POST `/trips/{tripId}/passengers`
-   GET `/trips/{tripId}/passengers`
-   PUT `/trips/{tripId}/passengers/{customerId}`
-   DELETE `/trips/{tripId}/passengers/{customerId}`

Todos protegidos por JWT.

------------------------------------------------------------------------

## 🗄️ Banco de Dados

PostgreSQL via Docker.

Tabelas:

-   tb_customers
-   tb_trips
-   trip_passengers
-   trip_room_prices

------------------------------------------------------------------------

## ✅ Estado Atual do MVP

-   CRUD completo de viagens
-   Gestão completa de passageiros por viagem
-   Persistência relacional correta
-   Autenticação JWT funcional
-   Integração completa com frontend React
