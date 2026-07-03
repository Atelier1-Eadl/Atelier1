const axios = require("axios");

const { getKafka } = require("../config/kafka");
const { createAlertConsumer } = require("./consumers/alert.consumer");

require("dotenv").config();

const startApp = async () => {
  try {
    const kafka = await getKafka();

    const alertConsumer = createAlertConsumer({
      kafka,
      httpClient: axios,
    });

    await alertConsumer.start();

  } catch (err) {
    console.error(" App failed:", err);
    process.exit(1);
  }
};

startApp();