'use strict';

angular.module('datawiki', ['datasetService']).
  config(['$routeProvider', function($routeProvider) {
        $routeProvider.
          when('/', {templateUrl: 'welcome.jsp', controller: WikiCtrl}).
          when('/wiki/:name', {templateUrl: 'dataset.jsp', controller: DatasetCtrl}).
          when('/wiki/:name/:id', {templateUrl: 'document.jsp', controller: DocumentCtrl}).
          otherwise({redirectTo: '/'});
      }]);

angular.module('datasetService', ['ngResource']).
  factory('Format', function($resource){
      return $resource('wiki/:name', {}, {});
    }).
  factory('Dataset', function($resource){
      return $resource('wiki/:name/', {}, {});
    }).
  factory('Document', function($resource){
      return $resource('wiki/:name/:id', {}, {});
    });

function WikiCtrl($scope, $routeParams) {
}

function WelcomeCtrl($scope, $http) {
  $http.get('/wiki/').success(function(data) {
      $scope.datasetList = data;
    });
}

function DatasetCtrl($scope, $routeParams, Format, Dataset) {
  // $routeParams.name matches "when('/wiki/:name'," above
  $scope.format = Format.get({name: $routeParams.name}, function(f) {
      console.log('format:');
      console.log(f);
    });
  $scope.dataset = Dataset.get({name: $routeParams.name}, function(d) {
      console.log('dataset:');
      console.log(d);
    });
}

function DocumentCtrl($scope, $routeParams, Document) {
  $scope.dataset = $routeParams.name;
  $scope.id = $routeParams.id;
  $scope.document = Document.get({name: $routeParams.name, id: $routeParams.id}, function(d) {
      console.log('document:');
      console.log(d);
    });
}

WikiCtrl.$inject = ['$scope', '$routeParams'];
WelcomeCtrl.$inject = ['$scope', '$http'];
DatasetCtrl.$inject = ['$scope', '$routeParams', 'Format', 'Dataset'];
DocumentCtrl.$inject = ['$scope', '$routeParams', 'Document'];
