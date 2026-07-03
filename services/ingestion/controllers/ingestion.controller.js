const { publishSensorData } = require("../services/ingestion.service");

// CONTROLLER = HTTP ONLY
// 👉 SRP: pas de Kafka ici, juste orchestration

const receiveSensorData = async (req, res) => {
  try {

    const sensorData = req.body;

    // 🟡 SIMPLE VALIDATION (KISS)
    if (!sensorData.sensorId || sensorData.value === undefined) {
      return res.status(400).json({
        message: "Invalid sensor data",
      });
    }

    // 🔴 DELEGATION TO SERVICE
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