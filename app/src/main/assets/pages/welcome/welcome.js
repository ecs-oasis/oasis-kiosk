app
  .controller('WelcomeController', function WelcomeController($scope, $state, apiService) {
    console.log('WelcomeController');
    $scope.state = $state.current.name;
    $scope.$parent.$parent.startTimer();
    $scope.$parent.$parent.showGoHome(true);
    $scope.$parent.$parent.showGoBack(false);
    $scope.buttonLock = false;
    $scope.goToPrivacy = function (){
      $state.go('agree');
    }
    $scope.goNext = function () {
      if($scope.buttonLock){
        return;
      }
      $scope.buttonLock = true;
      if($scope.$parent.$parent.bAgree) {
        apiService.updateTerms({hasAgreedTerms:true, hasAgreedMarketing:false}, ()=>{
          $state.go('course');
        }, (res)=>{
          console.error(res);
          showSystemErrorDialog();
        });
      }
    }
  });