'use strict';

angular.module('datawiki', ['datasetService']).
  config(['$routeProvider', function($routeProvider) {
        $routeProvider.
          when('/', {templateUrl: 'welcome.jsp', controller: WikiCtrl}).
          when('/wiki/:datasetName', {templateUrl: 'dataset.jsp', controller: DatasetCtrl}).
          when('/wiki/:datasetName/:docId', {templateUrl: 'document.jsp', controller: DocumentCtrl}).
          when('/format/:datasetName', {templateUrl: 'format.jsp', controller: DatasetCtrl}).
          otherwise({redirectTo: '/'});
      }]);

angular.module('datasetService', ['ngResource']).
  factory('Format', function($resource){
      return $resource('/wiki/schema(:name)', {}, {});
    }).
  factory('Dataset', function($resource){
      return $resource('/wiki/:name/', {}, {});
    }).
  factory('Document', function($resource){
      return $resource('/wiki/:name/:id', {}, {
          update: {method: 'PUT'}
        });
    });

function WikiCtrl($scope, $location) {
  $scope.search = {query: 'Dataset name here'};
  $scope.doSearch = function() {
    console.log('search: ');
    console.log($scope.search.query);
    $location.path('/wiki/' + $scope.search.query);
  }
}

function WelcomeCtrl($scope, $http) {
  $http.get('/wiki/').success(function(data) {
      $scope.datasetList = data;
    });
}

function DatasetCtrl($scope, $http, $routeParams, $resource, Dataset, Format, Document) {
  $scope.datasetName = $routeParams.datasetName;
  $scope.dataset = Dataset.get({name: $routeParams.datasetName}, function(d) {});
  $scope.format = Format.get({name: $routeParams.datasetName}, function(d) {});
  $scope.queryForm = {};
  $scope.create = function() {
    console.log('create');
    new Document($scope.queryForm).$save({name: $routeParams.datasetName});
  };
  $scope.find = function() {
    var query = '', form = $scope.queryForm;
    for (var prop in form) {
      var val = form[prop];
      if (val) {
        var expr = prop + '=' + val;
        if (query == '') {
          query += expr;
        } else {
          query += ' AND ' + expr;
        }
      }
    }
    if (query == '') {
      return;
    }
    console.log(query);
    $http({method: 'GET',
          url: '/wiki/' + $scope.datasetName,
          params: {q: query}}).
       success(function (data) {
           console.log(data);
           $scope.dataset = data;
         });
  };
}

function DocumentCtrl($scope, $routeParams, Document) {
  $scope.datasetName = $routeParams.datasetName;
  $scope.docId = $routeParams.docId;
  $scope.document = Document.get({name: $routeParams.datasetName,
                                  id: $routeParams.docId},
    function(d) {
      console.log('document:');
      console.log(d);
    });
  $scope.update = function() {
    console.log('update');
    console.log($scope.document);
    new Document($scope.document).$update({name: $routeParams.datasetName, id: $scope.docId});
  };
}

WikiCtrl.$inject = ['$scope', '$location'];
WelcomeCtrl.$inject = ['$scope', '$http'];
DatasetCtrl.$inject = ['$scope', '$http', '$routeParams', '$resource', 'Dataset', 'Format', 'Document'];
DocumentCtrl.$inject = ['$scope', '$routeParams', 'Document'];
