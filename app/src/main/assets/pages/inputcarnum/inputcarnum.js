app
  .controller('InputCarNumController', function InputCarNumController($scope, $state, apiService) {
    console.log('InputCarNumController')
    $scope.state = $state.current.name;
    $scope.buttonLock = false;
    $scope.$parent.$parent.startTimer();
    $scope.$parent.$parent.showGoHome(true);
    $scope.$parent.$parent.inputScope.init();
    $scope.$parent.$parent.inputScope.target = 'carNum';

    $scope.login = function () {
      if(isValidCarNum($scope.$parent.$parent.carNum)) {
        $scope.$parent.$parent.showDialogYn('차량번호 확인', '입력하신 차량번호\n' +
          '<span>'+$scope.$parent.$parent.carNum + '</span>가\n' +
          '확실히 맞으신가요?', '취소', '확인', function (){
          if($scope.buttonLock){
            return;
          }
          $scope.buttonLock = true;
          apiService.login({carNo: $scope.$parent.$parent.carNum});
        });
      } else {
        getAppScope().showDialogTxt('err', "올바른 차량번호를 입력해 주세요.", function (){
        })
      }
    }

    $scope.isValidCarNum = function (str) {
      return isValidCarNum(str);
    }

  });