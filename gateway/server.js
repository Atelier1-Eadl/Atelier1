const express = require("express");
const cors = require("cors");
const cookieParser = require("cookie-parser");

const notificationProxy = require("./proxi/notificationproxi");
const { connectRedis } = require("../config/redis");
const { connectKafka } = require("../config/kafka");

require("dotenv").config({ path: "../.env" });


const http = require("http");
  
const { initWebSocket, broadcast } = require("./websocket/socket");
const router = require("./routes/alert.route");
const app = express();


const server = http.createServer(app);
initWebSocket(server);


app.use(cors({
  origin: "http://localhost:5173",
  credentials: true,
}));

app.use(express.json());
app.use(express.urlencoded({ extended: true }));
app.use(cookieParser());

app.use((req, res, next) => {
  console.log("🚨 GATEWAY HIT:", req.method, req.originalUrl);
  next();
});


app.use("/api", router);
app.use("/api/alerts", notificationProxy);


const start = async () => {
  try {
    await connectRedis();
    await connectKafka();

    server.listen(process.env.PORT_GATEWAY, () => {
      console.log("🚪 GATEWAY RUNNING ON " + process.env.PORT_GATEWAY);
    });

  } catch (err) {
    console.error("❌ Failed to start server:", err);
    process.exit(1);
  }
};

start();

module.exports = { app, broadcast };