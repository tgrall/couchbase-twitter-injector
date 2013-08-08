var express = require('express'),
	driver = require('couchbase'),
	routes = require('./routes'),
	appVersion = "1.0"
	;



dbConfiguration = {
	"hosts": ["127.0.0.1:8091"],
	"bucket": "default"
};


driver.connect(dbConfiguration, function(err, cb) {
	if (err) {
		throw (err)
	}


	var app = module.exports = express();
	// Configuration
	app.configure(function() {
		app.set('views', __dirname + '/views');
		app.engine('.html', require('ejs').renderFile);
		app.set('view engine', 'html');
		app.set('view options', {
			layout: false
		});
		app.use(express.bodyParser());
		app.use(express.methodOverride());
		app.use(express.cookieParser());
		app.use(express.session({
			secret: 'demo-cb'
		}));
		app.use(app.router);
		app.use(express.static(__dirname + '/public'));
	});


	
	// *** routes
	app.get('/', routes.index);
	app.get('/partials/:name', routes.partials);



	// *** API and Couchbase access ****
	app.get('/api/tweet/list/:type/:key?', function(req, res) {
		
		var params = {
			stale: false,
			reduce: false,
			limit: 100,
		};
		
		if (req.params.key) {
			if (req.params.key.indexOf("[") != -1 ) {
				params.startkey = eval(req.params.key);
				var ek = eval(req.params.key);
				ek.push({});
				params.endkey = ek;
			}
			else {
				params.key = req.params.key;
			}
		}
		
		
		cb.view("twitter", req.params.type, 
		 params,
		 function(err, view) {
			var keys = new Array();
			for (var i = 0; i < view.length; i++) {
				keys.push(view[i].id);
			}
			
			cb.get(keys, null, function(errs, docs, metas) {
				res.send(docs);
			});
		});		
	});


	app.get('/api/tweet/stats/:type', function(req, res) {
		
		var level = req.query.level;
		if (!req.query.level) {
			level = 1;
		}
		
		var params = 	{
				stale: false,
				group_level: level,
				limit: 1000
				}
		
		cb.view("twitter", req.params.type, 
		 params ,
		 function(err, view) {
			var keys = new Array();
			for (var i = 0; i < view.length; i++) {
				keys.push(view[i]);
			}
			res.send(keys);
		});		
	});
	



	app.get('/api/tweet/:id', function(req, res) {
		cb.get(req.params.id, function(err, doc, meta) {
			if (doc != null) {
				res.send(doc);
			} else {
				res.send(404);
			}
		});
	});
	
	appServer = app.listen(3000, function() {
		console.log("Express server listening on port %d in %s mode", appServer.address().port, app.settings.env);
	});

});
