const express = require("express");
const { alert } = require("../controllers/Alert");
const router = express.Router();


router.post("/alerts", alert );

module.exports = router;