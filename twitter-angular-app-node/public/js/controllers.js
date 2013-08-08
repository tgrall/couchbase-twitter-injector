'use strict';

/* Controllers */


function AppCtrl($scope, $http) {
	$scope.user = null;
}
AppCtrl.$inject = ['$scope', '$http'];


function NavBarController($scope) {
}
NavBarController.$inject = ['$scope'];



function ByDateCtrl($rootScope, $scope, $routeParams, $http, $location) {	
	
 $scope.type = $routeParams.byType;
	

 	if ($scope.type == "by_date") {
 		$scope.statusList = [
    		{label:"Year", value:1},
    		{label:"Month", value:2},
    		{label:"Day", value:3},
    		{label:"Hour", value:4},
			{label:"Mn", value:5},
			{label:"Sec", value:6}
  			];
  		$scope.status =  {label:"Year", value:1} ;
	} else {
		$http({method: 'GET', url: '/api/tweet/stats/'+ $scope.type })
			.success(function(data, status, headers, config) {
				$scope.entries = data;
				console.log(data);
			})
			.error(function(data, status, headers, config) {
	    		$scope.name = 'Error!'
	  		});
	}


  $scope.onStatusChange = function(){
	var entryStatus = "";
	  entryStatus = ($scope.status);
	$http({method: 'GET', url: '/api/tweet/stats/by_date?level='+ $scope.status.value })
		.success(function(data, status, headers, config) {
			$scope.entries = data;
			console.log(data);
		})
		.error(function(data, status, headers, config) {
    		$scope.name = 'Error!'
  		});
  };
}
ByDateCtrl.$inject = ['$rootScope', '$scope', '$routeParams','$http', '$location'];



function TweetListCtrl($rootScope, $scope, $routeParams, $http, $location) {	
	$http({method: 'GET', url: '/api/tweet/list/'+ $routeParams.type +"/"+ escape($routeParams.key)} )
		.success(function(data, status, headers, config) {
			$scope.entries = data;
			console.log(data);
		})
		.error(function(data, status, headers, config) {
    		$scope.name = 'Error!'
  		});
	

}
TweetListCtrl.$inject = ['$rootScope', '$scope', '$routeParams','$http', '$location'];


function TweetViewCtrl($rootScope, $scope, $routeParams, $http, $location) {	
	
	$http({method: 'GET', url: "/api/tweet/"+ $routeParams.id.toString()  } )
		.success(function(data, status, headers, config) {
			$scope.entry = data;
		})
		.error(function(data, status, headers, config) {
    		$scope.name = 'Error!'
  		});
	

}
TweetViewCtrl.$inject = ['$rootScope', '$scope', '$routeParams','$http', '$location'];

function IndexListCtrl($rootScope, $scope, $routeParams, $http, $location) {	
}
IndexListCtrl.$inject = ['$rootScope', '$scope', '$routeParams','$http', '$location'];
