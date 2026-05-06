const Redis = require("ioredis")
require("dotenv").config({ path: "../.env" })
let redis

const connectRedis = async () => {
  try {
    redis = new Redis({
      host: process.env.REDIS_HOST ,
      port: process.env.REDIS_PORT ,
      password: process.env.REDIS_PASSWORD,
      retryStrategy: (times) => Math.min(times * 50, 2000)
    })

    redis.on("connect", () => {
      console.log("⚡ Redis connected")
    })

    redis.on("error", (err) => {
      console.error("❌ Redis error:", err.message)
    })

    await redis.ping()
    console.log("✅ Redis ready")
  } catch (err) {
    console.error("❌ Redis connection failed:", err.message)
  }
}

const getRedis = () => {
  if (!redis) throw new Error("Redis not initialized")
  return redis
}

module.exports = { connectRedis, getRedis }