'use strict';

angular.module('datawiki', ['datasetService']).
  config(['$routeProvider', function($routeProvider) {
        $routeProvider.
          when('/', {templateUrl: 'welcome.html', controller: WikiCtrl}).
          when('/wiki/:datasetName', {templateUrl: 'dataset.html', controller: DatasetCtrl}).
          when('/wiki/:datasetName/:docId', {templateUrl: 'doc.html', controller: DocumentCtrl}).
          otherwise({redirectTo: '/'});
      }]);

angular.module('datasetService', ['ngResource']).
  factory('Format', function($resource){
      return $resource('/wiki/schema(:name)', {}, {});
    }).
  factory('Dataset', function($resource){
      return $resource('/wiki/:name/', {}, {query:{method: 'GET', params:{}, isArray:false}});
    }).
  factory('Document', function($resource){
      return $resource('/wiki/:name/:id', {}, {
          update: {method: 'PUT'}
        });
    });

function WikiCtrl($scope, $location) {
  $scope.search = {query: 'Search'};
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

var gScope;

function DatasetCtrl($scope, $http, $routeParams, $resource, $filter, Dataset, Format, Document) {
  $scope.datasetName = $routeParams.datasetName;
  $scope.dataset = Dataset.get({name: $routeParams.datasetName}, ok, err);
  $scope.create = function() {
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
    $http({method: 'GET',
          url: '/wiki/' + $scope.datasetName,
          params: {q: query}}).
       success(function (data) {
           console.log('find success:');
           console.log(data);
           //$scope.dataset.results.length = 0;
           //$scope.dataset.results.concat(data.results);
           //$scope.updateListing(data);
         });
  };
  $scope.format = Format.get({name: $routeParams.datasetName}, ok, err);
  $scope.noMetaFilter = function(fmt) {
    var clean = {};
    for (var key in fmt) {
      if (key == 'updated' || key == 'updater') {
        continue;
      }
      clean[key] = fmt[key];
    }
    return clean;
  };
  $scope.idDisplay = function(id) {
    var match = id.match(/__(\d+)__/);
    if (match) {
      return match[1];
    }
    return id;
  };
  $scope.dataDisplay = function(obj, key) {
    var val = obj[key];
    if (key == 'updated')
      return $filter('date')(val);
    return val;
  };
  $scope.queryForm = {};
  $scope.getKey = function(obj) {
    for (var key in obj)
      return key;
  };
  $scope.getVal = function(obj) {
    for (var key in obj)
      return obj[key];
  };
}

var gData;

function DocumentCtrl($scope, $routeParams, Document) {
  $scope.datasetName = $routeParams.datasetName;
  $scope.docId = $routeParams.docId;
  $scope.document = Document.get({name: $routeParams.datasetName,
                                  id: $routeParams.docId}, function() {
      new FormEditor('docForm', true);
    }, err);
  $scope.mods = {};
  $scope.update = function() {
    console.log('update');
    console.log($scope.document);
    console.log($scope.mos);
    new Document($scope.mods).$update({name: $routeParams.datasetName, id: $scope.docId});
  };
}

WikiCtrl.$inject = ['$scope', '$location'];
WelcomeCtrl.$inject = ['$scope', '$http'];
DatasetCtrl.$inject = ['$scope', '$http', '$routeParams', '$resource', '$filter',
                       'Dataset', 'Format', 'Document'];
DocumentCtrl.$inject = ['$scope', '$routeParams', 'Document'];

function ok(e) {
}

function err(e) {
}
