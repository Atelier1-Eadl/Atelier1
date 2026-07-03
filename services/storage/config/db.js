const mysql = require("mysql2/promise");

let connection;

const connectDB = async () => {
  connection = await mysql.createConnection({
    host: process.env.DB_HOST,
    user: process.env.DB_USER,
    password: process.env.DB_PASSWORD,
    database: process.env.DB_NAME,
  });

  console.log("MySQL connected");
};

const getDB = () => {
  if (!connection) throw new Error("DB not initialized");
  return connection;
};

module.exports = { connectDB, getDB };