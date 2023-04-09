app
  .controller('MainController', function MainController($scope, $state) {
    console.log('MainController');
    $scope.state = $state.current.name;
  });