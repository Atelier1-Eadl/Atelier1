const { broadcast } = require("../websocket/socket");



const alert = async (req, res) => {
  const event = req.body;

  console.log("Alert received in gateway:", event);

  broadcast({
    type: "ALERT_CREATED",
    data: event,
  });

  return res.status(200).json({ success: true });
};

module.exports = { alert };