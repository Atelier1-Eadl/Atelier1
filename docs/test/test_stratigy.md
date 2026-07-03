# Testing Strategy — Smart City / UrbanHub (Node.js Microservices)
1. Objectif

Ce document décrit la stratégie de tests appliquée dans le projet Smart City / UrbanHub.

L’objectif est de garantir que les microservices restent :

testables ;
maintenables ;
découplés de l’infrastructure (Kafka, Redis, DB) ;
fiables dans une architecture event-driven ;
compatibles avec une approche TDD / XP.

Les tests couvrent principalement :

tests unitaires (Jest) ;
mocks (Kafka, Redis) ;
stubs (valeurs contrôlées) ;
tests API (Postman / curl) ;
tests d’intégration Kafka (manuel / Docker) ;
validation CI/CD.
2. Services concernés

Les microservices testés sont :

ingestion-service
storage-service
analytics-service
alerting-service
gateway (infra shared Kafka)

Chaque service possède sa propre logique métier et ses tests indépendants.

3. Types de tests utilisés
3.1 Tests unitaires

Les tests unitaires vérifient la logique métier sans dépendre de Kafka, Redis ou base de données.

Exemple (ingestion-service)
const { publishSensorData } = require("../services/ingestion.service");

Objectifs :

tester la logique d’ingestion ;
éviter Kafka réel ;
garantir des tests rapides ;
assurer la reproductibilité.
3.2 Tests avec mocks (Kafka)

Kafka est mocké pour isoler la logique métier.

const mockSend = jest.fn();

jest.mock("../../../config/kafka", () => ({
  getKafkaProducer: () => ({
    send: mockSend,
  }),
}));
Vérification :
expect(mockSend).toHaveBeenCalled();

👉 Cela permet de tester sans infrastructure externe.

3.3 Tests avec stubs

Les stubs permettent de fixer des valeurs déterministes.

Exemple (timestamp contrôlé)
jest.spyOn(Date.prototype, "toISOString").mockReturnValue("2026-01-01");
Exemple (données capteur)
const data = {
  sensorId: "S1",
  value: 42
};
3.4 Tests API (manuel)

Les endpoints REST sont testés via Postman ou curl.

Exemple ingestion-service
POST /ingestion/ingest

Body :

{
  "sensorId": "S1",
  "value": 42
}

Réponse attendue :

{
  "message": "Data received successfully"
}
3.5 Tests Kafka (manuel)

Kafka est testé via consumer CLI ou logs Docker.

kafka-console-consumer --bootstrap-server localhost:9092 --topic sensor-data --from-beginning
Topics testés :
sensor-data
analytics-events
alerts
dead-letter-queue (DLQ)
4. Exemple de test unitaire — ingestion-service

Ce test vérifie que les données capteurs sont publiées dans Kafka.

const { publishSensorData } = require("../services/ingestion.service");

const mockSend = jest.fn();

jest.mock("../../../config/kafka", () => ({
  getKafkaProducer: () => ({
    send: mockSend,
  }),
}));

test("should publish sensor data", async () => {

  const data = {
    sensorId: "S1",
    value: 42
  };

  await publishSensorData(data);

  expect(mockSend).toHaveBeenCalledTimes(1);

  const callArgs = mockSend.mock.calls[0][0];

  expect(callArgs.topic).toBe("sensor-data");

  const message = JSON.parse(callArgs.messages[0].value);

  expect(message.sensorId).toBe("S1");
  expect(message.value).toBe(42);
  expect(message.timestamp).toBeDefined();
});
Ce que ce test valide
la donnée est bien publiée dans Kafka ;
le topic est correct ;
le message est correctement construit ;
Kafka est mocké (pas d’infra réelle).
5. Exemple de mock (Kafka)

Kafka est remplacé par un mock Jest :

getKafkaProducer: () => ({
  send: jest.fn(),
});
Avantages :
pas de dépendance externe ;
tests rapides ;
isolation complète du service.
6. Exemple de stub
Exemple de valeur stable :
const data = {
  sensorId: "S1",
  value: 42
};
Exemple d’effet :
test déterministe ;
résultats reproductibles ;
pas de random data.
7. Tests unitaires — autres services
analytics-service
test("should detect anomaly when value is high", () => {

  const result = analyze({ value: 250 });

  expect(result.level).toBe("CRITICAL");
});
alerting-service
test("should send alert when event is critical", async () => {

  await handleAlert({ level: "CRITICAL" });

  expect(notificationService.send).toHaveBeenCalled();
});
8. Tests d’intégration Kafka (manuel)
docker logs kafka

ou

kafka-console-consumer --topic sensor-data
Vérifie :
publication réelle des messages ;
consommation correcte ;
format JSON valide.
9. Tests end-to-end (flux complet)
sensor → ingestion-service → Kafka → analytics-service → alerting-service
Test manuel :
curl -X POST http://localhost:3000/ingestion/ingest \
-H "Content-Type: application/json" \
-d '{
  "sensorId": "S1",
  "value": 42
}'
10. TDD (Red → Green → Refactor)
Red

Écriture du test :

test("should publish sensor data", ...)
Green

Implémentation minimale du service.

Refactor
séparation controller/service ;
extraction Kafka config ;
amélioration lisibilité.
11. CI/CD (GitHub Actions)

Chaque service possède sa pipeline :

test → build → deploy
Étapes :
npm install
npm test
build Docker image
deploy
12. Limites actuelles
Kafka encore majoritairement testé manuellement ;
pas encore Testcontainers ;
pas de contract testing automatisé ;
peu de tests d’intégration full flow automatisés.
13. Synthèse

La stratégie de tests repose sur :

tests unitaires Jest ;
mocks Kafka (isolation complète) ;
stubs pour données déterministes ;
tests API manuels ;
tests Kafka CLI ;
approche TDD ;
CI/CD par microservice.
🔥 Conclusion

Cette stratégie garantit :

une architecture testable ;
une séparation claire métier / infrastructure ;
une fiabilité en environnement distribué ;
une bonne base pour évolution future (contract testing + integration tests).