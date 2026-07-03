openapi: 3.1.0
info:
  title: UrbanHub - Ingestion Service API
  version: 1.0.0
  description: API du service d'ingestion des capteurs IoT

servers:
  - url: http://localhost:3000

paths:
  /ingestion/ingest:
    post:
      summary: Recevoir une mesure capteur IoT
      description: Reçoit une mesure et la publie dans Kafka
      operationId: ingestSensorData

      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: "#/components/schemas/SensorDataRequest"

      responses:
        "200":
          description: OK
          content:
            application/json:
              schema:
                $ref: "#/components/schemas/SensorDataResponse"

components:
  schemas:

    SensorDataRequest:
      type: object
      properties:
        sensorId:
          type: string
          example: "S1"
        value:
          type: number
          example: 42

    SensorDataResponse:
      type: object
      properties:
        message:
          type: string
          example: "Data received successfully"