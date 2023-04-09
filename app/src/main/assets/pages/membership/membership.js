app
  .controller('MembershipController', function MembershipController($scope, $state, apiService) {
    console.log('MembershipController');
    $scope.state = $state.current.name;
    $scope.$parent.$parent.startTimer();
    $scope.$parent.$parent.showGoHome(true);
    $scope.$parent.$parent.showGoBack(true);
    $scope.changeCard = function () {
        $scope.$parent.$parent.showDialogYn('결제카드를 변경하시겠어요?', '결제카드를 변경하시면 현재\n' +
          '등록되어 있는 카드 정보가 삭제됩니다.', '취소', '카드 변경하기', function (){
            $state.go('insertcard', { mode : 'change'});
        });
    }
    $scope.changeCourse = function () {
        if(getAppScope().user.isSubscriptionCanceled && getAppScope().user.subscriptionStatus === 'MEMBERSHIP_PREMIUM'){
            return;
        }
        $state.go('course');
    }
    $scope.cancelMembership = function () {
        if(getAppScope().user.isSubscriptionCanceled){
            return;
        }
        $scope.$parent.$parent.showDialogYn('정말로 멤버십을 취소하시겠어요?', '지금 멤버십 취소 시 다음 달부터\n' +
          '결제되지 않습니다. 남은 기간 동안\n' +
          '더 이용하고 결정해보세요!', '결제 후 취소', '멤버십 유지하기', function(){

        },function (){
            apiService.cancelCard(function (){
                $scope.$parent.$parent.showDialogYn('멤버십 취소 완료', '멤버십이 취소되었습니다.\n' +
                  '잔여기간 동안 기존 멤버십 코스를\n' +
                  '정상적으로 사용 가능합니다.', null, '확인', function (){
                });
                getAppScope().user.isSubscriptionCanceled = true;
            }, function (res){
                if(res.data.statusCode === 400) {//이미 취소
                    getAppScope().showDialogTxt('err', res.data.message, function (){
                    })
                } else {
                    showSystemErrorDialog();
                }
            });
        });
    }
  });