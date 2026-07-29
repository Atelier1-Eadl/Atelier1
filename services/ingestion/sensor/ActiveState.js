const SensorState = require("./SensorState");

class ActiveState extends SensorState {
  canSendMeasurement() {
    return true;
  }

  getName() {
    return "ACTIVE";
  }
}

module.exports = ActiveState;