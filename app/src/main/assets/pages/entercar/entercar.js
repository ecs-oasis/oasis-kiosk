app
  .controller('EnterCarController', function EnterCarController($scope, $state) {
    console.log('EnterCarController');
    $scope.state = $state.current.name;
    $scope.$parent.$parent.startTimer(10);
    $scope.$parent.$parent.showGoHome(false);
    $scope.$parent.$parent.showGoBack(false);
    $scope.course = $scope.$parent.$parent.course;
    $scope.justPaid = $scope.$parent.$parent.justPaid;
    $scope.isSubscription = $scope.$parent.$parent.isSubscription;

    if($scope.course.name === '프리미엄 코스') {
        startCourse('premium');
    } else {
        startCourse('standard');
    }

    $scope.goNext = function () {
        $state.go('nocarnum');
    }
  });