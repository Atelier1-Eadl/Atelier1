const swaggerJsdoc = require("swagger-jsdoc");
const swaggerUi = require("swagger-ui-express");

const options = {
  definition: {
    openapi: "3.0.0",
    info: {
      title: "UrbanHub Ingestion Service",
      version: "1.0.0",
      description: "API documentation for IoT ingestion service"
    },
    servers: [
      {
        url: "http://localhost:5001"
      }
    ]
  },
  apis: ["./routes/*.js"] 
};

const swaggerSpec = swaggerJsdoc(options);

const setupSwagger = (app) => {
  app.use("/api-docs", swaggerUi.serve, swaggerUi.setup(swaggerSpec));
};

module.exports = setupSwagger;