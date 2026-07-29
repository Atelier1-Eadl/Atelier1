# Refactoring — UrbanHub Ingestion Service

## 1. Objectif

Ce document décrit le refactoring du service d’ingestion IoT du projet UrbanHub.

L’objectif est de :

- appliquer les principes SOLID, DRY, KISS
- introduire une architecture propre en couches
- séparer la logique métier de l’infrastructure
- intégrer un modèle métier Sensor avec State Pattern
- améliorer la maintenabilité et la testabilité

---

## 2. État initial (avant refactor)

### Problèmes

- validation dans le controller
- logique métier dans le controller
- appel direct à Kafka
- absence de modèle métier (Sensor)
- code difficile à maintenir et tester

### Exemple ancien code

```js
const receiveSensorData = async (req, res) => {
  const data = req.body;

  if (!data.sensorId || data.value === undefined) {
    return res.status(400).json({ message: "Invalid data" });
  }

  await kafka.send(data);

  return res.json({ message: "OK" });
};

## After

const { publishSensorData } = require("../services/ingestion.service");
const { validateSensorData } = require("../utils/validateSensorData");
const Sensor = require("../sensor/Sensor");

const receiveSensorData = async (req, res) => {
  try {
    const sensorData = req.body;

    // validation séparée (DRY)
    if (!validateSensorData(sensorData)) {
      return res.status(400).json({
        message: "Invalid sensor data",
      });
    }

    // logique métier (POO + State Pattern)
    const sensor = new Sensor(
      sensorData.sensorId,
      "STATION-1",
      "ZONE-1"
    );

    if (!sensor.canSendMeasurement()) {
      return res.status(400).json({
        message: `Sensor is ${sensor.getStatus()}`
      });
    }

    // infrastructure isolée (Kafka)
    await publishSensorData({
      ...sensorData,
      status: sensor.getStatus()
    });

    return res.status(200).json({
      message: "Data received successfully",
      sensorStatus: sensor.getStatus()
    });

  } catch (err) {
    return res.status(500).json({
      message: "Internal server error",
    });
  }
};

module.exports = { receiveSensorData };