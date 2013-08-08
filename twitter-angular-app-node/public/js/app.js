'use strict';

// Declare app level module which depends on filters, and services
angular.module('myApp', ['myApp.filters', 'myApp.services', 'myApp.directives']).
  config(['$routeProvider', '$locationProvider', function($routeProvider, $locationProvider) {
    $routeProvider.
		when('/', {templateUrl: 'partials/index.html', controller: IndexListCtrl }).
		when('/tweets/:byType', {templateUrl: 'partials/by_date.html', controller: ByDateCtrl }).
		when('/tweets/list/:type/:key', {templateUrl: 'partials/tweet_list.html', controller: TweetListCtrl }).
		when('/tweet/:id', {templateUrl: 'partials/tweet_view.html', controller: TweetViewCtrl }).
		otherwise({redirectTo: '/'});
      
  }]);


