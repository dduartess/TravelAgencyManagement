# ✈️ Travel Agency Management --- MVP

Sistema de gestão para agência de viagens desenvolvido com **Spring
Boot + JPA + PostgreSQL**, focado no aprendizado prático de **engenharia
de software**, modelagem de domínio e boas práticas de arquitetura.

------------------------------------------------------------------------

## 🎯 Objetivo do sistema

O sistema permite:

-   Cadastro de passageiros
-   Criação de viagens
-   Definição de preços por tipo de quarto
-   Associação de passageiros às viagens
-   Consulta de passageiros por viagem

Arquitetura separada em **Entity**, **DTO**, **Service** e
**Controller**.

------------------------------------------------------------------------

## 🧱 Entidades do domínio

### 👤 Customer (Passageiro)

**Tabela:** `tb_customers`

  Campo            Tipo        Regra
  ---------------- ----------- -------------------------------------
  id               Long        PK, auto gerado
  name             String      Obrigatório, apenas letras
  documentNumber   String      Obrigatório, apenas números (7--20)
  birthDate        LocalDate   Obrigatório, passado ou presente
  phoneNumber      String      Obrigatório, 11 dígitos

Relacionamento: ManyToMany com Trip.

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

Mapeamento:

``` java
Map<RoomType, BigDecimal> roomPrices
```

**Tabela:** `trip_room_prices`

  trip_id   room_type   price
  --------- ----------- -------

Enum RoomType: - CASAL - TRIPLO - QUADRUPLO

------------------------------------------------------------------------

### 👥 Relação Trip ↔ Customer

**Tabela:** `trip_passengers`

  trip_id   customer_id
  --------- -------------

Implementado com `Set<Customer>` para evitar duplicidade.

------------------------------------------------------------------------

## 📦 DTOs

### Entrada

-   TripCreateDto
-   CustomerDto

### Saída

-   TripResponseDto
-   CustomerResponseDto
-   TripPassengerStatsDto

Motivo: não expor Entities na API.

------------------------------------------------------------------------

## ⚙️ Regras de negócio

### Criar viagem

-   Data início não pode ser no passado
-   Data fim ≥ data início
-   Deve existir ao menos um preço de quarto \> 0

### Adicionar passageiro

-   Se não existir → cria
-   Se existir → reutiliza
-   Não permite duplicidade na mesma viagem

### Consultar passageiros

-   Retorna lista e total

------------------------------------------------------------------------

## 🌐 Endpoints

### POST /trips

Cria uma viagem

``` json
{
  "destination": "Porto Seguro",
  "startDate": "2026-03-04",
  "endDate": "2026-03-08",
  "roomPrices": {
    "CASAL": 850.00,
    "QUADRUPLO": 750.00
  }
}
```

------------------------------------------------------------------------

### POST /trips/{tripId}/passengers

Adiciona passageiro

``` json
{
  "name": "Daniel Duarte",
  "documentNumber": "12345678901",
  "birthDate": "2005-01-10",
  "phoneNumber": "38991555907"
}
```

------------------------------------------------------------------------

### GET /trips/{tripId}/passengers

Lista passageiros e quantidade.

------------------------------------------------------------------------

## 🗄️ Banco de dados

PostgreSQL via Docker.

Tabelas criadas automaticamente pelo Hibernate: - tb_customers -
tb_trips - trip_passengers - trip_room_prices

------------------------------------------------------------------------

## 🧠 Decisões técnicas aplicadas

-   Uso de Set para evitar duplicidade
-   Map\<Enum, BigDecimal\> para preços por quarto
-   Bean Validation nas entidades
-   DTO de resposta
-   Service com regras de negócio
-   ManyToMany correto
-   Enum persistido como STRING

------------------------------------------------------------------------

## ✅ Estado atual do MVP

O sistema já permite:

-   Criar viagens
-   Definir preços por quarto
-   Cadastrar passageiros automaticamente
-   Vincular passageiros às viagens
-   Consultar passageiros
-   Persistência relacional correta
