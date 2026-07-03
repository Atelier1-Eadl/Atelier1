const { getKafkaProducer } = require("../../../config/kafka");

// SERVICE = BUSINESS LOGIC LAYER
// 👉 pas de HTTP ici
// 👉 encapsule la logique ingestion

const publishSensorData = async (sensorData) => {

  // 🔴 INFRA ACCESS (Kafka via shared config gateway)
  const producer = getKafkaProducer();

  // 🟢 EVENT BUILDING (DRY)
  const event = {
    ...sensorData,
    timestamp: new Date().toISOString(),
    source: "ingestion-service",
  };

  // 🔴 KAFKA PUBLISH
  await producer.send({
    topic: "sensor-data",
    messages: [
      {
        value: JSON.stringify(event),
      },
    ],
  });

  console.log("📡 Sensor data sent to Kafka");
};

module.exports = { publishSensorData };