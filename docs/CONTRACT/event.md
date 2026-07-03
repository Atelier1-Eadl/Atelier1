{
  "eventName": "SensorDataReceived",
  "eventType": "SensorDataReceived",
  "eventVersion": "1.0",
  "topic": "sensor-data",
  "producer": "ingestion-service",
  "consumers": [
    "storage-service",
    "analytics-service"
  ],
  "partitionKey": "sensorId",
  "payloadExample": {
    "sensorId": "S1",
    "value": 42,
    "timestamp": "2026-07-03T12:00:00Z",
    "source": "ingestion-service"
  }
}