class SensorState {
  canSendMeasurement() {
    throw new Error("Method must be implemented");
  }

  getName() {
    throw new Error("Method must be implemented");
  }
}

module.exports = SensorState;