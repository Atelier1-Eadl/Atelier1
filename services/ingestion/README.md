# Ingestion Service

## Description

Le **Ingestion Service** est responsable de recevoir les données envoyées par les capteurs IoT via une API REST. Après une première validation, il publie les mesures sur Kafka afin qu'elles puissent être traitées par les autres microservices.

---

## Responsabilités

- Recevoir les mesures des capteurs via HTTP.
- Vérifier les données obligatoires.
- Ajouter les métadonnées (timestamp).
- Publier les événements sur Kafka.
- Retourner une réponse HTTP au client.

---

## Technologies utilisées

- Node.js
- Express.js
- KafkaJS
- Redis
- Docker
- Docker Compose

---

## Architecture

```
Capteur IoT
     │
     ▼
POST /ingestion/ingest
     │
     ▼
Ingestion Service
     │
     ▼
Kafka (topic : sensor-data)
```

---

## API REST

### POST /ingestion/ingest

Reçoit une mesure provenant d'un capteur.

### Exemple de requête

```json
{
  "sensorId": "S1",
  "value": 42
}
```

### Réponse

```json
{
  "message": "Data received successfully"
}
```

---

## Kafka

### Producer

Le service publie les événements sur le topic :

```
sensor-data
```

Exemple d'événement :

```json
{
  "sensorId": "S1",
  "value": 42,
  "timestamp": "2026-07-03T15:30:00Z",
  "source": "ingestion-service"
}
```

---

## Variables d'environnement

```env
PORT_INGESTION=3001

KAFKA_BROKER=kafka:9092

REDIS_HOST=redis

REDIS_PORT=6379
```

---

## Installation

Installer les dépendances :

```bash
npm install
```

Lancer le service :

```bash
npm start
```

---

## Tests

Lancer les tests unitaires :

```bash
npm test
```

---

## Dépendances

- Gateway
- Kafka
- Redis

---

## Services consommateurs

Les événements produits sont consommés par :

- Storage Service
- Analytics Service

---

## Auteur

Projet UrbanHub - Smart City