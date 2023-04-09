app
  .controller('WelcomeHomeController', function WelcomeHomeController($scope, $state) {
    console.log('WelcomeHomeController');
    $scope.state = $state.current.name;
    $scope.$parent.$parent.startTimer();
    $scope.course = $scope.$parent.$parent.course;
    $scope.$parent.$parent.showGoHome(true);
    $scope.$parent.$parent.showGoBack(false);
    $scope.startCourse = function (){
      $scope.$parent.$parent.justPaid = false;
      $scope.$parent.$parent.isSubscription = true;
      $state.go('entercar');
    }
    $scope.goToMembership = function (){
      $state.go('membership');
    }
  });