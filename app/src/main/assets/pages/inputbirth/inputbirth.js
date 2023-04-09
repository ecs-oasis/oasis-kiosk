app
  .controller('InputBirthController', function InputBirthController($scope, $state, apiService) {
    console.log('InputBirthController')
    $scope.state = $state.current.name;
    $scope.cardInfo = $scope.$parent.$parent.cardInfo;
    $scope.cardInfo.idntNo = '';
    $scope.$parent.$parent.startTimer();
    $scope.$parent.$parent.showGoHome(true);
    $scope.$parent.$parent.showGoBack(true);
    $scope.$parent.$parent.inputScope.init();
    $scope.$parent.$parent.inputScope.target = 'birth';
    $scope.isValidBirth = function (birth) {
      return isValidBirth(birth);
    }

    $scope.goToNext = function () {
      if(isValidBirth($scope.cardInfo.idntNo)){
        $state.go('inputcardpw');
      } else {
        getAppScope().showDialogTxt('err', "올바른 생년월일를 입력해 주세요.", function (){
        })
      }
    }
  });