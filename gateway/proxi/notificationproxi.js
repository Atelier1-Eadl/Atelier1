const { createProxyMiddleware } = require("http-proxy-middleware");

const notificationProxy = createProxyMiddleware({
  target: "http://localhost:5002",
  changeOrigin: true,
  logLevel: "debug",

  onProxyReq(proxyReq, req) {
    if (req.body) {
      const bodyData = JSON.stringify(req.body);

      proxyReq.setHeader("Content-Type", "application/json");
      proxyReq.setHeader("Content-Length", Buffer.byteLength(bodyData));
      proxyReq.write(bodyData);
    }

    if (req.user) {
      proxyReq.setHeader("x-user-id", req.user.id_uuid);
      proxyReq.setHeader("x-user-email", req.user.email);
      proxyReq.setHeader("x-user-role", req.user.role);
    }
  }
});

module.exports = notificationProxy;