module.exports = {
  server: {
    middleware: {
      2: require('http-proxy-middleware')('http://localhost:8080/api')
    }
  }
};