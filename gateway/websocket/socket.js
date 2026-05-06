const WebSocket = require("ws");

let wss;

const initWebSocket = (server) => {
  wss = new WebSocket.Server({ server });

  wss.on("connection", (ws) => {
    console.log("🟢 Client connected");

    ws.on("close", () => {
      console.log("🔴 Client disconnected");
    });
  });

  return wss;
};

// 🔥 broadcast à tous les clients
const broadcast = (data) => {
  if (!wss) return;

  const message = JSON.stringify(data);

  wss.clients.forEach((client) => {
    if (client.readyState === WebSocket.OPEN) {
      client.send(message);
    }
  });
};

module.exports = { initWebSocket, broadcast };