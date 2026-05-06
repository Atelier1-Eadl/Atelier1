


const { sendAlertToGateway } = require("../services/alert.service");
const { createAlertConsumer } = require("./alert.consumer");


jest.mock("../services/alert.service", () => ({
  sendAlertToGateway: jest.fn(),
}));



describe("Alert Consumer", () => {
  it("should consume kafka message and call service", async () => {

    const fakeMessage = {
      value: Buffer.from(
        JSON.stringify({
          id: 1,
          title: "Fire",
        })
      ),
    };

    const runMock = jest.fn(({ eachMessage }) => {
      return eachMessage({ message: fakeMessage });
    });

    const kafka = {
      consumer: () => ({
        connect: jest.fn(),
        subscribe: jest.fn(),
        run: runMock,
      }),
    };

    const consumer = createAlertConsumer({
      kafka,
      httpClient: {},
    });

    await consumer.start();

    expect(sendAlertToGateway).toHaveBeenCalledTimes(1);

    expect(sendAlertToGateway).toHaveBeenCalledWith(
      {},
      {
        id: 1,
        title: "Fire",
      }
    );
  });
});