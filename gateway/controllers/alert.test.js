// 🔥 1. MOCK AVANT IMPORT
// On remplace le vrai module WebSocket par un fake (mock)
// → broadcast devient une fonction espion (jest.fn)
jest.mock("../websocket/socket", () => ({
  broadcast: jest.fn(),
}));

//  IMPORT DES LIBS
const request = require("supertest"); // pour tester HTTP
const express = require("express");   // mini serveur pour test

//  IMPORT APRÈS MOCK (TRÈS IMPORTANT)
// Ici broadcast est déjà mocké
const { broadcast } = require("../websocket/socket");

// On importe le controller réel à tester
const { alert } = require("./Alert");


// CRÉATION D’UNE APP EXPRESS DE TEST
// → pas ton vrai serveur, juste un fake isolé
const app = express();
app.use(express.json());

// On branche la route vers ton controller
app.post("/api/alerts", alert);


// 🔥 5. SUITE DE TEST
describe("ALERT CONTROLLER", () => {

  // 🔥 6. TEST UNITAIRE
  it("should send alert and call broadcast", async () => {

    // 👉 données simulées (comme Kafka en vrai)
    const payload = {
      id: 1,
      title: "Fire detected",
      level: "HIGH",
    };

    // 👉 appel HTTP simulé
    const res = await request(app)
      .post("/api/alerts")
      .send(payload);

    // ✅ 7. TEST HTTP RESPONSE
    // → le controller répond bien
    expect(res.statusCode).toBe(200);
    expect(res.body.success).toBe(true);

    // ✅ 8. TEST WEBSOCKET (MOCK)
    // → on vérifie que broadcast a été appelé
    expect(broadcast).toHaveBeenCalledTimes(1);

    // ✅ 9. TEST DATA ENVOYÉE
    // → on vérifie que le bon message est envoyé au frontend
    expect(broadcast).toHaveBeenCalledWith({
      type: "ALERT_CREATED",
      data: payload,
    });
  });

});