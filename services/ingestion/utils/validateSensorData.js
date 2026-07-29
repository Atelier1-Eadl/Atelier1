

const validateSensorData = (data) => {
  if (!data.sensorId || data.value === undefined) {
    return false;
  }
  return true;
};

module.exports = { validateSensorData };