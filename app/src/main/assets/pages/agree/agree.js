app
  .controller('AgreeController', function AgreeController($scope, $state) {
    console.log('AgreeController');
    $scope.state = $state.current.name;
    $scope.$parent.$parent.startTimer();
    $scope.$parent.$parent.showGoHome(true);
    $scope.$parent.$parent.showGoBack(true);
  });