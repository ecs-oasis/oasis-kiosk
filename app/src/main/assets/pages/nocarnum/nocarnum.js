app
  .controller('NoCarNumController', function NoCarNumController($scope, $state, apiService) {
    console.log('NoCarNumController');
    $scope.state = $state.current.name;
    $scope.$parent.$parent.initData();
    $scope.goNext = function () {
      $state.go('inputcarnum');
    }

    $scope.login = function (carNum) {
      apiService.login(carNum);
    }
  });