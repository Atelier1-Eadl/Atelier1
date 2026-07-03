const sendAlertToGateway = async (httpClient, event) => {
  await httpClient.post("http://localhost:6000/api/alerts", event);
};

module.exports = { sendAlertToGateway };