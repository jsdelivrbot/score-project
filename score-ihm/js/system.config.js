System.config({
        packages: {        
          app: {
            format: 'register',
            defaultExtension: 'js'
          }
        }
      });
      System.import('app//app/main')
            .then(null, console.error.bind(console));
