const express = require("express");
const { receiveSensorData } = require("../controllers/ingestion.controller");

// ROUTE = mapping only
// 👉 aucune logique métier ici

const router = express.Router();

/**
 * @swagger
 * /ingest:
 *   post:
 *     summary: Receive sensor data
 *     tags:
 *       - Ingestion
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             properties:
 *               sensorId:
 *                 type: string
 *                 example: S1
 *               value:
 *                 type: number
 *                 example: 42
 *     responses:
 *       200:
 *         description: Data received successfully
 */
router.post("/ingest", receiveSensorData);

module.exports = router;