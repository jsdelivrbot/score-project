module.exports = {
  server: {
    middleware: {
      2: require('http-proxy-middleware')('http://dvdsi320w.creteil.francetelecom.fr:7000/api')
    }
  }
};