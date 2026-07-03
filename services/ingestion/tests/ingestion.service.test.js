const { publishSensorData } = require("../services/ingestion.service");

// 🔴 MOCK Kafka (OBLIGATOIRE)
const mockSend = jest.fn();

jest.mock("../../../config/kafka", () => ({
  getKafkaProducer: () => ({
    send: mockSend,
  }),
}));

describe("Ingestion Service", () => {

  beforeEach(() => {
    mockSend.mockClear();
  });

  test("should publish sensor data to Kafka", async () => {

    const data = {
      sensorId: "S1",
      value: 42
    };

    await publishSensorData(data);

    // ✔ Kafka called
    expect(mockSend).toHaveBeenCalledTimes(1);

    // ✔ topic correct
    const callArgs = mockSend.mock.calls[0][0];
    expect(callArgs.topic).toBe("sensor-data");

    // ✔ message content
    const message = JSON.parse(callArgs.messages[0].value);

    expect(message.sensorId).toBe("S1");
    expect(message.value).toBe(42);
    expect(message.timestamp).toBeDefined();
    expect(message.source).toBe("ingestion-service");
  });

});