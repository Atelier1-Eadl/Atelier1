const { sendAlertToGateway } = require("../services/alert.service");


const createAlertConsumer = ({ kafka, httpClient }) => {
  const start = async () => {
    const consumer = kafka.consumer({ groupId: "notification-service" });

    await consumer.connect();
    console.log("Kafka connected");

    await consumer.subscribe({
      topic: "Alert_created",
      fromBeginning: false,
    });

    await consumer.run({
      eachMessage: async ({ message }) => {
        try {
          const event = JSON.parse(message.value.toString());

          await sendAlertToGateway(httpClient, event);

          console.log(" Alert sent");
        } catch (err) {
          console.error(" Error:", err.message);
        }
      },
    });
  };

  return { start };
};

module.exports = { createAlertConsumer };