module.exports = {
  server: {
    middleware: {
      2: require('http-proxy-middleware')((process.env.SCORE_REST_API_URL || 'http://localhost:8080') + '/api')
    }
  }
};