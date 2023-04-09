app
  .controller('InputCardPwController', function InputCardPwController($scope, $state, apiService) {
    console.log('InputCardPwController')
    $scope.state = $state.current.name;
    $scope.cardInfo = $scope.$parent.$parent.cardInfo;
    $scope.cardInfo.cardPwd = '';
    $scope.buttonLock = false;
    $scope.$parent.$parent.startTimer();
    $scope.$parent.$parent.showGoHome(true);
    $scope.$parent.$parent.showGoBack(true);
    $scope.$parent.$parent.inputScope.init();
    $scope.$parent.$parent.inputScope.target = 'cardPw';
    $scope.isValidCardPw = function (cardPw) {
      if(cardPw.length === 2) {
        return true;
      }
      return false;
    }
    $scope.registerCard = function () {
      if($scope.buttonLock){
        return;
      }
      $scope.buttonLock = true;
      if($scope.isValidCardPw($scope.cardInfo.cardPwd)) {
        apiService.registerCard($scope.cardInfo, (res)=>{
          if(res.data.outStatCd === "0021") {
            if($scope.$parent.$parent.user.subscriptionStatus && $scope.$parent.$parent.user.subscriptionStatus.includes('MEMBERSHIP')) {
              $scope.$parent.$parent.showDialogTxt('noti', "카드 등록이 완료되었습니다.\n" +
                "정기 결제 회원이므로 지금 즉시\n" +
                "세차 코스를 이용하실 수 있습니다.", function () {
                $state.go('welcomehome');
              })
            } else {
              $scope.$parent.$parent.showDialogTxt('noti', "카드 등록이 완료되었습니다.\n" +
                "이제 세차 코스를 선택할 수 있어요!", function () {
                $state.go('course');
              })
            }
          } else {
            $scope.$parent.$parent.showDialogTxt('err', res.data.outRsltMsg, function (){
              $state.go('insertcard');
            })
            console.error(res.data);
          }
        }, (res)=>{
          console.error(res);
          showSystemErrorDialog();
        });
      }
    }
  });