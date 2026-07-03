# Clean Code — Exemples SOLID, DRY, KISS et Refactoring
1. Objectif

Ce document présente plusieurs exemples de Clean Code appliqués dans le projet Smart City / UrbanHub.

Les exemples couvrent :

SOLID
DRY
KISS
séparation des responsabilités
architecture microservices Node.js
usage de Kafka partagé via gateway
refactoring propre et testable
2. Exemple SOLID — Single Responsibility Principle
Contexte

Dans le service ingestion-service, chaque couche a une responsabilité unique :

Controller → HTTP
Service → logique métier
Kafka (config gateway) → infrastructure
Controller (HTTP layer uniquement)
const { publishSensorData } = require("../services/ingestion.service");

// CONTROLLER = SRP (HTTP uniquement)
const receiveSensorData = async (req, res) => {
  try {

    const sensorData = req.body;

    if (!sensorData.sensorId || sensorData.value === undefined) {
      return res.status(400).json({
        message: "Invalid sensor data",
      });
    }

    await publishSensorData(sensorData);

    return res.status(200).json({
      message: "Data received successfully",
    });

  } catch (err) {
    return res.status(500).json({
      message: "Internal server error",
    });
  }
};

module.exports = { receiveSensorData };
Service (logique métier + orchestration Kafka)
const { getKafkaProducer } = require("../../../config/kafka");

// SERVICE = SRP (business logic)
const publishSensorData = async (sensorData) => {

  const producer = getKafkaProducer();

  const event = {
    ...sensorData,
    timestamp: new Date().toISOString(),
    source: "ingestion-service"
  };

  await producer.send({
    topic: "sensor-data",
    messages: [
      {
        value: JSON.stringify(event),
      },
    ],
  });

  console.log("Sensor data published to Kafka");
};

module.exports = { publishSensorData };
Route (mapping uniquement)
const express = require("express");
const { receiveSensorData } = require("../controllers/ingestion.controller");

// ROUTE = SRP (mapping uniquement)
const router = express.Router();

router.post("/ingest", receiveSensorData);

module.exports = router;
3. SOLID — Dependency Inversion Principle (adapté Node + Kafka gateway)
Contexte

Le service dépend de Kafka via un module partagé dans le gateway :

const { getKafkaProducer } = require("../../../config/kafka");

👉 Cela montre une dépendance directe à l’infrastructure.

Version conceptuelle (amélioration SOLID)
// PORT (abstraction)
const SensorPublisher = {
  publish: async (event) => {}
};
Implémentation Kafka (infrastructure)
const { getKafkaProducer } = require("../../../config/kafka");

// ADAPTER KAFKA
const KafkaSensorPublisher = {
  publish: async (event) => {

    const producer = getKafkaProducer();

    await producer.send({
      topic: "sensor-data",
      messages: [
        {
          value: JSON.stringify(event),
        },
      ],
    });
  }
};

module.exports = KafkaSensorPublisher;
Use (dans service)
const KafkaSensorPublisher = require("../infra/KafkaSensorPublisher");

// DIP (version améliorée)
await KafkaSensorPublisher.publish(event);
4. DRY — Don’t Repeat Yourself
Contexte

Les métadonnées Kafka sont centralisées dans un seul endroit :

const event = {
  ...sensorData,
  timestamp: new Date().toISOString(),
  source: "ingestion-service"
};

👉 évite duplication dans les controllers

5. KISS — Keep It Simple
Validation simple et lisible
if (!sensorData.sensorId || sensorData.value === undefined) {
  return res.status(400).json({
    message: "Invalid sensor data",
  });
}

👉 pas de framework complexe
👉 logique directe
👉 facile à maintenir

6. Séparation des responsabilités
Architecture réelle du projet
Route → Controller → Service → Kafka (gateway config)
Responsabilités
Controller → HTTP + validation simple
Service → business logic + event building
Kafka config → infrastructure (shared gateway)
7. Encapsulation de l’infrastructure Kafka
const { Kafka } = require("kafkajs");

let producer;

const connectKafka = async () => {
  const kafka = new Kafka({
    clientId: "gateway",
    brokers: [process.env.KAFKA_BROKER],
  });

  producer = kafka.producer();
  await producer.connect();

  console.log("Kafka connected");
};

const getKafkaProducer = () => {
  if (!producer) throw new Error("Kafka not initialized");
  return producer;
};

module.exports = { connectKafka, getKafkaProducer };
8. Refactoring — extraction de logique métier
Avant

Logique mélangée controller + Kafka

Après
// SERVICE ISOLÉ
await publishSensorData(sensorData);

👉 améliore :

testabilité
lisibilité
maintenance
9. Exemple de test (mock Kafka)
// MOCK Kafka producer
const mockProducer = {
  send: jest.fn(),
};

👉 permet de tester service sans Kafka réel

10. Exemple de stub (timestamp)
jest.spyOn(Date.prototype, "toISOString").mockReturnValue("2026-01-01");

👉 test déterministe

11. Test unitaire ingestion
test("should publish sensor data", async () => {

  const data = {
    sensorId: "S1",
    value: 42
  };

  await publishSensorData(data);

  expect(getKafkaProducer().send).toHaveBeenCalled();
});
12. Séparation métier / infrastructure
Métier
ingestion.service.js
validation data
event building
Infrastructure
config/kafka.js
Redis
Kafka producer