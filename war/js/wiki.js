'use strict';

var datawiki = angular.module('datawiki', ['ngRoute', 'datasetService']).
  config(['$routeProvider', function($routeProvider) {
        $routeProvider.
          when('/', {templateUrl: 'welcome.html',
                controller: DatasetsCtrl}).

          when('/wiki/:datasetName', {templateUrl: 'dataset.html',
                controller: DatasetCtrl}).

          when('/wiki/:datasetName/create', {templateUrl: 'createDataset.html',
                controller: DatasetsCtrl}).

          when('/wiki/:datasetName/:docId', {templateUrl: 'document.html',
                controller: DocumentCtrl}).

          otherwise({redirectTo: '/'});
      }]);

angular.module('datasetService', ['ngResource']).
  factory('Datasets', function($resource) {
      return $resource('/wiki/', {}, {},
                       {stripTrailingSlashes: false});
    }).
  factory('Dataset', function($resource) {
      return $resource('/wiki/:name/', {}, {
          put: {method: 'PUT'},
          query: {method: 'GET', params:{}, isArray:false}
        },
        {stripTrailingSlashes: false});
    }).
  factory('Format', function($resource) {
      return $resource('/wiki/schema(:name)', {}, {
          put:{method: 'PUT'},
        });
    }).
  factory('Document', function($resource) {
      return $resource('/wiki/:name/:id', {}, {
          update: {method: 'PUT'}
        });
    });


function DatasetsCtrl($scope, $location, $http, Datasets, Dataset) {

  $scope.datasets = Datasets.get();
  $scope.query = {val: 'Search'};
  $scope.queryClass = 'inputInactive';

  $scope.queryActive = function() {
    $scope.query.val = '';
    $scope.queryClass = 'inputActive';
  };

  // Collection methods.
  $scope.checkQuery = function() {
    var q = $scope.query.val;
    if (q == '' || q == 'Search') {
      return false;
    }
    return true;
  };

  $scope.find = function() {
    $http({method: 'GET',
           url: '/wiki/' + $scope.query.val}).
    success(function (data) {
        $scope.go();
      }).
    error(function(data) {
        $http({method: 'PUT',
               url: '/wiki/' + $scope.query.val,
               data: {}}).
        success(function (data) {
            $location.path('/wiki/' + $scope.query.val);
          })
      });
  };
  $scope.go = function() {
    $location.path('/wiki/' + $scope.query.val);
  };
}
datawiki.controller('DatasetsCtrl',
    ['$scope', '$location', '$http',
     'Datasets', 'Dataset', DatasetsCtrl]);


function DatasetCtrl($scope, $http, $routeParams, $filter,
                     Dataset, Format, Document) {
  $scope.saveSchema = function() {
    console.log('setSchema');
    // TODO(pmy): have FormEditor operate directly on angular model.
    var jsonSchema = $scope.editor.toJson();
    console.log(jsonSchema);
    new Format(jsonSchema).$put({name: $routeParams.datasetName});
  };

  $scope.editForm = function(formId) {
    console.log('edit');
    if ($scope.editing) {
      $scope.editing = false;
      $scope.editor.saveForm();
    } else {
      $scope.editing = true;
      $scope.editor = new FormEditor(formId, true,
                                     function() {
                                       $scope.saveSchema();
                                     });
      console.log($scope);
      $scope.editor.editForm();
    }
  };

  $scope.handleFormSubmit = function(action) {
    if (action === 'add') {
      $scope.add();
    } else if (action === 'find') {
      $scope.find();
    } else {
      console.log('unknown form action: ' + action);
    }
  };

  // Collection methods.
  $scope.add = function() {
    console.log('add');
    new Document($scope.queryForm).$save({name: $routeParams.datasetName});
  };

  $scope.find = function() {
    console.log('find');
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

  $scope.datasetName = $routeParams.datasetName;
  $scope.dataset = Dataset.get({name: $routeParams.datasetName}, ok, err);
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
datawiki.controller('DatasetCtrl',
    ['$scope', '$http', '$routeParams', '$filter',
     'Dataset', 'Format', 'Document', DatasetCtrl]);


function DocumentCtrl($scope, $routeParams, Document, Format) {

  $scope.datasetName = $routeParams.datasetName;
  $scope.docId = $routeParams.docId;

  $scope.document = Document.get({name: $routeParams.datasetName,
                                  id: $routeParams.docId}, function() {
      // TODO(pmy):
      // new FormEditor('docForm', true);
    }, err);

  $scope.noMetaFilter = function(doc) {
    var clean = {};
    for (var key in doc) {
      if (key == 'updated' || key == 'updater') {
        continue;
      }
      clean[key] = doc[key];
    }
    return clean;
  };

  $scope.mods = {};

  $scope.update = function() {
    new Document($scope.mods).$update({name: $routeParams.datasetName, id: $scope.docId});
  };

  // TODO(pmy): following copied from Dataset.
  // Form edit should directly modify ng model; currently edits don't take effect.
  $scope.editForm = function(formId) {
    console.log('edit');
    if ($scope.editing) {
      $scope.editing = false;
      $scope.editor.saveForm();
    } else {
      $scope.editing = true;
      $scope.editor = new FormEditor(formId, true, null);
      console.log($scope);
      $scope.editor.editForm();
    }
  };
}
datawiki.controller('DocumentCtrl',
    ['$scope', '$routeParams',
     'Document', 'Format', DocumentCtrl]);


//DatasetsCtrl.$inject = ['$scope', '$location', '$http', 'Datasets'];
//DatasetCtrl.$inject = ['$scope', '$http', '$routeParams', '$filter',
//                       'Dataset', 'Format', 'Document'];
//DocumentCtrl.$inject = ['$scope', '$routeParams', 'Document'];

function ok(e) {
}

function err(e) {
}
