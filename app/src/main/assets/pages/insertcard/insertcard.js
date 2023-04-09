app
  .controller('InsertCardNumController', function InsertCardNumController($scope, $state, $stateParams, $timeout, apiService) {
    console.log('InsertCardNumController')
    $scope.state = $state.current.name;
    $scope.buttonLock = false;
    $scope.$parent.$parent.startTimer();
    $scope.$parent.$parent.showGoHome(true);
    if($scope.$parent.$parent.course) {
      $scope.$parent.$parent.showGoBack(true);
    } else {
      $scope.$parent.$parent.showGoBack(false);
    }

    $scope.goToNext = function () {
      try {
        $scope.$parent.$parent.cardInfo = readMagnetCard();
      }catch (e){}
      if($scope.$parent.$parent.cardInfo) {
        if($stateParams.mode && $stateParams.mode === 'change') {
          $scope.registerCard();
        } else {
          $scope.payCard();
        }
        // $state.go('inputbirth');
      } else {
        $scope.$parent.$parent.showDialogTxt('err', "IC카드 삽입 회수 후\n다음 버튼을 눌러주세요.", function (){
        })
      }
    }

    $scope.registerCard = function () {
      if($scope.buttonLock){
        return;
      }
      $scope.buttonLock = true;
      apiService.registerCard({
        ...$scope.$parent.$parent.cardInfo,
      }, (res)=>{
        if(res.data.outStatCd === "0021") {
          $state.go('welcomehome');
        } else {
          $scope.$parent.$parent.showDialogTxt('err', res.data.outRsltMsg, function (){
            $state.go('insertcard');
          })
          console.error(res.data);
          $scope.$parent.$parent.cardInfo = null;
          $scope.buttonLock = false;
        }
      }, (res)=>{
        $scope.$parent.$parent.cardInfo = null;
        $scope.buttonLock = false;
        console.error(res);
        showSystemErrorDialog();
      });
    }

    $scope.payCard = function () {
      if($scope.buttonLock){
        return;
      }
      $scope.buttonLock = true;
      apiService.payCardNew({
        ...$scope.$parent.$parent.cardInfo,
        isSubscribe : $scope.$parent.$parent.isSubscription,
        productId : $scope.$parent.$parent.course.id,
      }, (res)=>{
        if(res.data.outStatCd === "0021") {
          $state.go('entercar');
        } else {
          $scope.$parent.$parent.showDialogTxt('err', res.data.outRsltMsg, function (){
            $state.go('insertcard');
          })
          console.error(res.data);
          $scope.$parent.$parent.cardInfo = null;
          $scope.buttonLock = false;
        }
      }, (res)=>{
        $scope.$parent.$parent.cardInfo = null;
        $scope.buttonLock = false;
        console.error(res);
        showSystemErrorDialog();
      });
    }

    // $scope.listenMagnetCard = function () {
    //   $scope.isListenCard = true;
    //   $timeout(()=>{
    //     console.log("readMagnet!");
    //     $scope.cardInfo = readMagnetCard();
    //     if($scope.cardInfo) {
    //       $scope.isListenCard = false;
    //       $scope.cardInserted($scope.cardInfo);
    //     } else if(getCurrentPage() === 'insertcard' && $scope.isListenCard) {
    //       $scope.listenMagnetCard();
    //     }
    //   }, 2000);
    // }

    // $scope.listenMagnetCard();
  });