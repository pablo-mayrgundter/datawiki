'use strict';

angular.module('datawiki', ['datasetService']).
  config(['$routeProvider', function($routeProvider) {
        $routeProvider.
          when('/', {templateUrl: 'welcome.jsp', controller: WikiCtrl}).
          when('/wiki/:datasetName', {templateUrl: 'dataset.jsp', controller: DatasetCtrl}).
          when('/wiki/:datasetName/:docId', {templateUrl: 'document.jsp', controller: DocumentCtrl}).
          when('/format/:formatName', {templateUrl: 'format.jsp', controller: FormatCtrl}).
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

function DatasetCtrl($scope, $http, $routeParams, Format, Dataset) {
  $scope.datasetName = $routeParams.datasetName;
  $scope.formatName = $routeParams.datasetName;
  $scope.format = Format.get({name: $routeParams.datasetName}, function(f) {});
  $scope.dataset = Dataset.get({name: $routeParams.datasetName}, function(d) {});
  $http({method: 'GET',
        url: '/wiki/schema(' + $routeParams.datasetName + ')'})
    .success(function(f) {
        $scope.format.schema = f;
      })
    .error(function(msg) {
        console.log('format err:');
        console.log(msg);
      });
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
}

function FormatCtrl($scope, $http, $routeParams, Format) {
  $scope.formatName = $routeParams.formatName;
  $scope.format = Format.get({name: $routeParams.formatName}, function(f) {});
  $http({method: 'GET',
        headers: {'X-Datahub-blob': true},
        url: '/wiki/' + $routeParams.formatName})
    .success(function(f) {
        $scope.format.schema = f;
      })
    .error(function(msg) {
        console.log('format err:');
        console.log(msg);
      });
}

WikiCtrl.$inject = ['$scope', '$routeParams'];
WelcomeCtrl.$inject = ['$scope', '$http'];
DatasetCtrl.$inject = ['$scope', '$http', '$routeParams', 'Format', 'Dataset'];
DocumentCtrl.$inject = ['$scope', '$routeParams', 'Document'];
FormatCtrl.$inject = ['$scope', '$http', '$routeParams', 'Format'];
