const { publishSensorData } = require("../services/ingestion.service");
const { validateSensorData } = require("../utils/validateSensorData");
const Sensor = require("../sensor/Sensor");

//  (SRP)

const receiveSensorData = async (req, res) => {
  try {
    const sensorData = req.body;

    //  validation (KISS + DRY)
    if (!validateSensorData(sensorData)) {
      return res.status(400).json({
        message: "Invalid sensor data",
      });
    }

    // LOGIQUE MÉTIER POO + STATE PATTERN
    const sensor = new Sensor(
      sensorData.sensorId,
      "STATION-1",
      "ZONE-1"
    );

    // exemple de règle métier
    if (!sensor.canSendMeasurement()) {
      return res.status(400).json({
        message: `Sensor is ${sensor.getStatus()}`
      });
    }

    //  Kafka (infra)
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
      message: "Internal server error. Please try again later.",
    });
  }
};

module.exports = { receiveSensorData };