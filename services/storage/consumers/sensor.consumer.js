const { getKafka } = require("../../config/kafka");
const { saveSensorData } = require("../services/storage.service");

const createSensorConsumer = async () => {
  const kafka = await getKafka();
  const consumer = kafka.consumer({ groupId: "storage-service" });

  await consumer.connect();
  await consumer.subscribe({ topic: "sensor-data", fromBeginning: false });

  await consumer.run({
    eachMessage: async ({ message }) => {
      const event = JSON.parse(message.value.toString());

      await saveSensorData(event);
    },
  });
};

module.exports = { createSensorConsumer };