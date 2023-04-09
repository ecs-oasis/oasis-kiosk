let app = angular.module('app', [
  'ui.router',
]);

app
  .controller('AppController', function AppController($scope, $interval, $state, apiService) {
    console.log('AppController')
    $scope.$state = $state;
    apiService.setAccessToken(null);
    $scope.initData = function () {
      $scope.carNum = "";
      $scope.user = null;
      $scope.course = null;
      $scope.isSubscription = null;
      $scope.justPaid = null;
      $scope.cardInfo = null;

      $scope.bShowDialogTxt = false;
      $scope.bShowDialogYn = false;

      /** privacy **/
      $scope.initAgree();

      /** navbar **/
      $scope.bShowGoBack = false;
      $scope.showGoBack = function (bool) {
        $scope.bShowGoBack = bool;
      }
      $scope.bShowGoHome = false;
      $scope.showGoHome = function (bool) {
        $scope.bShowGoHome = bool;
      }

      /** timer **/
      $scope.leftSec = 60;
      $scope.showTimer = false;
      // $scope.timerObj = null;

      if($scope.inputScope) {
        $scope.inputScope.init();
      }
    }

    $scope.startTimer = function(sec=60) {
      if(getCurrentPage() === 'nocarnum') {
        return;
      }
      if($scope.timerObj) {
        $interval.cancel($scope.timerObj);
      }
      $scope.showTimer = true;
      $scope.leftSec = sec;
      $scope.timerObj = $interval(()=>{
        if($scope.leftSec > 0){
          $scope.leftSec = $scope.leftSec -1;
        } else {
          $scope.showTimer = false;
          $interval.cancel($scope.timerObj);
          $scope.timerObj = null;
          $scope.goHome();
        }
      }, 1000);
    }

    $scope.goBack = function () {
      if($scope.inputScope) {
        $scope.inputScope.init();
      }
      window.history.back();
    }
    $scope.goHome = function () {
      $state.go('nocarnum');
    }

    $scope.initAgree = function () {
      $scope.bAgree = false;
      $scope.b1Checked = false;
      $scope.b2Checked = false;
      $scope.b3Checked = false;
      $scope.b4Checked = false;
      $scope.b5Checked = false;
    }

    $scope.updateAgree = function (num) {
      $scope['b'+num+'Checked'] = !$scope['b'+num+'Checked'];
      if($scope.b1Checked && $scope.b2Checked && $scope.b3Checked && $scope.b4Checked && $scope.b5Checked) {
        $scope.bAgree = true;
      } else {
        $scope.bAgree = false;
      }
    }

    $scope.toggleAgree = function () {
      $scope.bAgree = !$scope.bAgree;
      $scope.b1Checked = $scope.bAgree;
      $scope.b2Checked = $scope.bAgree;
      $scope.b3Checked = $scope.bAgree;
      $scope.b4Checked = $scope.bAgree;
      $scope.b5Checked = $scope.bAgree;
    }

    $scope.showInput = function (mode) {
      $scope['inputScope'].showInput(mode);
    }

    $scope.setInputMode = function (mode) {
      $scope['inputScope'].target = mode;
    }

    $scope.testCarIn = function () {
      onCarNumDetected ('12가1111');
    }

    $scope.testCardIn = function () {
      let cardInfo = {
          cardNo : "379183994242599",
          // idntNo : "",
          vldDtMon : "07",
          vldDtYear : "25",
          // cardPwd : ""
      };
      $scope.cardInfo = cardInfo;
    }

    $scope.initData();



    $scope.showDialogTxt = function (type, text, callback){
      $scope.dialogtxtType = type; //'noti' | 'err'
      $scope.dialogTxt = text;
      $scope.onClickDialogTxt = callback;
      $scope.bShowDialogTxt = true;
    }

    $scope.hideDialogTxt = function () {
      $scope.bShowDialogTxt = false;
    }

    $scope.showDialogYn = function (title, desc, cancel, confirm, callbackY, callbackN){
      $scope.dialogtxtTitle = title;
      $scope.dialogYnDesc = desc;
      $scope.dialogYnBtnCancel = cancel;
      $scope.dialogYnBtnConfirm = confirm;
      $scope.onClickDialogY = callbackY;
      $scope.onClickDialogN = callbackN;
      $scope.bShowDialogYn = true;
    }

    $scope.hideDialogYn = function () {
      $scope.bShowDialogYn = false;
    }

  });