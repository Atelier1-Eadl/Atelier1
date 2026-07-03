const express = require("express");
const { receiveSensorData } = require("../controllers/ingestion.controller");

// ROUTE = mapping only
// 👉 aucune logique métier ici

const router = express.Router();

router.post("/ingest", receiveSensorData);

module.exports = router;