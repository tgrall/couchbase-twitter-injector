var driver = require('couchbase');



dbConfiguration = {
	"hosts": ["127.0.0.1:8091"],
	"bucket": "default"
};


driver.connect(dbConfiguration, function(err, cb) {
	if (err) {
		throw (err)
	}




	var ddoc = {
		"views": {
			"by_date": {
				"map": 
					"function (doc, meta) { \n"+
				  	"  if (doc.created_at) { \n"+
				    "    emit( dateToArray(doc.created_at)); \n"+
				    "  } \n"+
				    "}",
				"reduce" : "_count"
			},
			"by_tags": {
				"map": 
				 "function (doc, meta) { \n"
				+"  if (doc.entities.hashtags.length > 0) { \n"
				+"    for (i in doc.entities.hashtags) { \n"
				+"     emit( doc.entities.hashtags[i].text ); \n"
				+"    } \n"
				+"  } \n"
				+"} \n"
				,"reduce" : "_count"
			},						
			"by_username" : {
				"map" : 
					"function (doc, meta) { \n"
				   +"  if ( doc.user.screen_name ) { \n"
				   +"    emit(doc.user.screen_name); \n"
				   +"   }\n"
				   +"} "
				,"reduce" : "_count"
			}											
		}
	};
	cb.setDesignDoc('twitter', ddoc, function(err, resp, data) { 
		if (err) { 
			console.log(err)
		} 
	});


	
	
});