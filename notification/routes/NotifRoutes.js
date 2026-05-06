const express = require("express")
const { notifAlert } = require("../controllers/NotifController")
const router = express.Router()




router.post("/notifyAlert", notifAlert )

module.exports = router