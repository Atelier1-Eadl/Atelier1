
const express = require("express")
const cors = require("cors")
const cookieParser = require("cookie-parser");
const router = require("./routes/NotifRoutes");
const { connectKafka } = require("../config/kafka");
const { connectRedis } = require("../config/redis");

require("dotenv").config({ path: "../.env" })


const app = express()
app.use(express.json());

app.use(cors({
  origin: "http://localhost:5173",
  credentials: true
}))



app.use(cookieParser())



app.use("/", router)

const start = async () => {
  try {
    await connectKafka();
    await connectRedis();

    app.listen(process.env.PORT_NOTIFICATION, () => {
  console.log(`Notification service running on http://localhost:${process.env.PORT_NOTIFICATION}`)
})

  } catch (err) {
    console.error("❌ Failed to start server:", err);
    process.exit(1);
  }
};

start();

