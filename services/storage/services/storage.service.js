const { getDB } = require("../config/db");

const saveSensorData = async (event) => {
  const db = getDB();

  await db.execute(
    `INSERT INTO sensor_data (sensor_id, value, timestamp, source)
     VALUES (?, ?, ?, ?)`,
    [
      event.sensorId,
      event.value,
      event.timestamp,
      event.source || "ingestion-service",
    ]
  );

  console.log("Sensor data saved in MySQL");
};

module.exports = { saveSensorData };