const { Kafka } = require("kafkajs");

let kafka;
let producer;

const connectKafka = async () => {
  try {
    require("dotenv").config({ path: "../.env" });

    const broker = process.env.KAFKA_BROKER;

    if (!broker) {
      throw new Error(" KAFKA_BROKER is not defined");
    }

    kafka = new Kafka({
      clientId: "gateway",
      brokers: [broker],
    });

    producer = kafka.producer(); // ✅ CLEAN VERSION

    await producer.connect();

    console.log("🧵 Kafka producer connected");
  } catch (err) {
    console.error("Kafka connection error:", err);
  }
};
// ✅ SAFE getter Kafka (IMPORTANT POUR CONSUMER)
const getKafka = async() => {
  try {
    require("dotenv").config({ path: "../.env" });
  if (!kafka) {
    const broker = process.env.KAFKA_BROKER;

    if (!broker) {
      throw new Error("❌ KAFKA_BROKER is not defined");
    }

    kafka = new Kafka({
      clientId: "gateway",
      brokers: [broker],
    });
  }
  console.log("🧵 Kafka  connected");
  } catch (error) {
    console.error("❌ Kafka connection error:", error);
  }

  return kafka;
};

const getKafkaProducer = () => {
  if (!producer) throw new Error("Kafka not initialized");
  return producer;
};

module.exports = { connectKafka, getKafkaProducer,getKafka };