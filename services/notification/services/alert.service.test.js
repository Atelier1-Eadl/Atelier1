const { sendAlertToGateway } = require("./alert.service");


describe("Alert Service", () => {
  it("should send alert to gateway", async () => {

    const httpClient = {
      post: jest.fn(),
    };

    const event = {
      id: 1,
      title: "Fire",
    };

    await sendAlertToGateway(httpClient, event);

    expect(httpClient.post).toHaveBeenCalledTimes(1);

    expect(httpClient.post).toHaveBeenCalledWith(
      "http://localhost:6000/api/alerts",
      event
    );
  });
});