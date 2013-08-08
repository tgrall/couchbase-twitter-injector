Couchbase Twitter Dashboard
===========================

Sample application that reads tweets from Couchbase and use views to navigate into them using User, Tags and Date.


## Installation

#####1- Prerequisites:

*Couchbase C Client Library:*
To be able to run the application you need to be sure you have installed the latest version of the Couchbase C Client library (aka libcouchbase) see [Couchnode installation](https://github.com/couchbase/couchnode).

*Node and Node_gyp*: 
Install [Node.js](http://nodejs.org/) and [node-gyp](https://github.com/TooTallNate/node-gyp)


#####2- Install the dependencies 

Install the dependencies using npm:

	cd twitter-angular-app-node
	
	npm install
	
	
#####3- Install the views

	node import.js
	
	ctlr+c

#####4- Run the application

	node app.js
	
Go to [http://127.0.0.1:3000](http://127.0.0.1:3000)




Note: the application use the bucket "default", if you want to change it edit the import.js and app.js.