app
  .controller('CourseController', function CourseController($scope, $state, apiService, $filter) {
    console.log('CourseController')
    $scope.state = $state.current.name;
    $scope.$parent.$parent.startTimer();
    $scope.buttonLock = false;
    $scope.$parent.$parent.showGoHome(true);
    if($scope.$parent.$parent.course) {
      $scope.$parent.$parent.showGoBack(true);
    } else {
      $scope.$parent.$parent.showGoBack(false);
    }
    $scope.curCourse = null;
    $scope.isSubscription = false;
    $scope.setSubscription = function (subscription) {
      $scope.isSubscription = subscription;
    }
    $scope.courses =  [
    ]


    $scope.bShowDialogSubscribe = false;
    $scope.bShowDialogPurchase = false;
    $scope.bShowDialogEvent = false;

    $scope.showDialogEvent = function (bool){
      $scope.bShowDialogEvent = bool;
    }

    $scope.showDialogSubscribe = function (bool){
      $scope.bShowDialogSubscribe = bool;
    }

    $scope.showDialogPurchase = function (bool){
      $scope.bShowDialogPurchase = bool;
    }

    $scope.courseSelected = function (course) {
      if($scope.$parent.$parent.course){
        $scope.isSubscription = true;
        if($scope.$parent.$parent.course.name !== course.name) {
          $scope.curCourse = course;
          if(course.name.includes('프리미엄')) {
            $scope.$parent.$parent.showDialogYn(course.name + '로 변경하시겠어요?', '변경 시 결제 금액은 <span>'+$filter('currency')(course.price, "", 0)+'원</span>이며,\n' +
              '기존 코스 결제 금액에서 나머지 금액\n' +
              '<span>15,000원이 추가 결제됩니다.</span>', '취소', '코스 변경하기', function () {
              $scope.changeMembership();
            }, function (){
              $scope.curCourse = null;
            });
          } else {
            $scope.$parent.$parent.showDialogYn(course.name + '로 변경하시겠어요?', '변경 시 결제 금액은 '+$filter('currency')(course.price, "", 0)+'원이며,\n' +
              course.name + ' 적용은\n' +
              '<span>다음달</span>부터 적용됩니다.\n' +
              '정말로 변경하시겠어요?', '취소', '코스 변경하기', function () {
              $scope.changeMembership();
            }, function (){
              $scope.curCourse = null;
            });
          }
        }
      } else {
        $scope.curCourse = course;
        $scope.showDialogEvent(true);
      }
    }

    $scope.changeMembership = function () {
      if($scope.buttonLock){
        return;
      }
      $scope.buttonLock = true;
      apiService.changeMembership(
        {
          productId : $scope.curCourse.id,
        }, (res) =>{
          if(res.data.success) {
            $scope.$parent.$parent.justPaid = false;
            if(res.data.subscriptionStatus === 'MEMBERSHIP_PREMIUM') {
              $scope.$parent.$parent.isSubscription = $scope.isSubscription;
              $scope.$parent.$parent.course = $scope.curCourse;
              $scope.$parent.$parent.showDialogTxt('noti', "코스 변경이 완료되었습니다.\n" +
                "이제 " + $scope.$parent.$parent.course.name + "로 세차할 수 있어요!", function () {
                getAppScope().user.subscriptionStatus = "MEMBERSHIP_PREMIUM";
                $state.go('welcomehome');
              })
            } else if(res.data.subscriptionStatus === 'MEMBERSHIP'){
              $scope.$parent.$parent.showDialogTxt('noti', "코스 변경이 완료되었습니다.\n" +
                "다음달부터 " + $scope.curCourse.name + "로\n" +
                "세차할 수 있어요!", function (){
                $state.go('welcomehome');
              })
            }
          } else {
            $scope.$parent.$parent.showDialogTxt('err', res.data.outRsltMsg, function (){
              $state.go('insertcard');
            })
            console.error(res.data);
          }
        }, (res) =>{
          if(res.data.statusCode === 400 && getAppScope().user.subscriptionStatus === 'MEMBERSHIP_PREMIUM'){
            $scope.$parent.$parent.showDialogTxt('err', "이미 해당 코스를 구독중 입니다.\n" +
              "다음달 부터 해당 코스로 변경 됩니다.", function () {
              $state.go('welcomehome');
            })
          } else {
            showSystemErrorDialog();
          }
          console.error(res);
        })
    }

    $scope.payCard = function () {
      $scope.$parent.$parent.isSubscription = $scope.isSubscription;
      $scope.$parent.$parent.course = $scope.curCourse;
      $scope.$parent.$parent.justPaid = true;
      if($scope.buttonLock){
        return;
      }
      if(getAppScope().user.hasCard){
        $scope.buttonLock = true;
        apiService.payCard(
          {
            productId : $scope.curCourse.id,
            isSubscribe : $scope.isSubscription
          }, (res) =>{
            if(res.data.outStatCd === "0021") {
              $state.go('entercar');
            } else {
              $scope.$parent.$parent.showDialogTxt('err', res.data.outRsltMsg, function (){
                $state.go('insertcard');
              })
              console.error(res.data);
            }
          }, (res) =>{
            console.error(res);
            showSystemErrorDialog();
          })
      } else {
        $state.go('insertcard');
      }

    }

    $scope.cancel = function () {
      $scope.curCourse = null;
      $scope.isSubscription = false;
      $scope.bShowDialogSubscribe = false;
      $scope.bShowDialogPurchase = false;
      $scope.bShowDialogEvent = false;
    }

    apiService.getProducts(function (res) {
      $scope.courses = res.data.products.reverse();
      console.log(res);
    }, function (res) {
      console.error(res);
      showSystemErrorDialog();
    });



  });