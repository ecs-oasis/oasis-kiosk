app.controller('UiInputController', function AppController($scope, $interval, $state, apiService) {
  $scope.$parent.inputScope = $scope;
  let current = [];

  $scope.init = function () {
    current = [];
    $scope.bShowInput = false;
    $scope.mode = 'number'; //number || hangul
    $scope.target = false;
  }

  $scope.init();

  $scope.showInput = function (mode) {
    $scope.mode = mode;
    $scope.bShowInput = true;
  }

  $scope.hideInput = function () {
    $scope.bShowInput = false;
  }

  $scope.input = function (num) {
    if($scope.target === 'carNum') {
      if($scope.$parent.carNum.length < 8) {
        $scope.$parent.carNum = $scope.calc(num);
      }
    }
    if($scope.target === 'cardPw') {
      if($scope.mode === 'number') {
        let cardInfo = getCurrentScope().cardInfo;
        if (cardInfo.cardPwd.length < 2) {
          cardInfo.cardPwd = $scope.calc(num);
        }
      }
    }
    if($scope.target === 'birth') {
      if($scope.mode === 'number') {
        let cardInfo = getCurrentScope().cardInfo;
        if (cardInfo.idntNo.length < 6) {
          cardInfo.idntNo = $scope.calc(num);
        }
      }
    }
  }

  $scope.inputRemove = function () {
    current.pop();
    if($scope.target === 'carNum') {
      $scope.$parent.carNum = Hangul.assemble(current);
    }
    if($scope.target === 'cardPw') {
      let cardInfo = getCurrentScope().cardInfo;
      cardInfo.cardPwd = Hangul.assemble(current);
    }
    if($scope.target === 'birth') {
      let cardInfo = getCurrentScope().cardInfo;
      cardInfo.idntNo = Hangul.assemble(current);
    }
  }

  $scope.calc = function (num) {
    if($scope.mode === 'number'){
      current.push(num);
    } else if($scope.mode === 'hangul'){
      let last = null;
      if(current.length > 0) {
        last = current[current.length-1];
      }
      switch (num) {
        case '1':// ㅣ
          if(last === '•'){
            current.pop();
            current.push('ㅓ');
          } else {
            current.push('ㅣ');
          }
          break;
        case '2':// •
          if(last === 'ㅣ'){
            current.pop();
            current.push('ㅏ');
          } else if(last === 'ㅡ'){
            current.pop();
            current.push('ㅜ');
          } else {
            current.push('•');
          }
          break;
        case '3':// ㅡ
          if(last === '•'){
            current.pop();
            current.push('ㅗ');
          } else {
            current.push('ㅡ');
          }
          break;
        case '4':// ㄱㅋ
          if(last === 'ㄱ'){
            current.pop();
            current.push('ㅋ');
          } else if(last === 'ㅋ'){
            current.pop();
            current.push('ㄱ');
          } else {
            current.push('ㄱ');
          }
          break;
        case '5':// ㄴㄹ
          if(last === 'ㄴ'){
            current.pop();
            current.push('ㄹ');
          } else if(last === 'ㄹ'){
            current.pop();
            current.push('ㄴ');
          } else {
            current.push('ㄴ');
          }
          break;
        case '6':// ㄷㅌ
          if(last === 'ㄷ'){
            current.pop();
            current.push('ㅌ');
          } else if(last === 'ㅌ'){
            current.pop();
            current.push('ㄷ');
          } else {
            current.push('ㄷ');
          }
          break;
        case '7':// ㅂㅍ
          if(last === 'ㅂ'){
            current.pop();
            current.push('ㅍ');
          } else if(last === 'ㅍ'){
            current.pop();
            current.push('ㅂ');
          } else {
            current.push('ㅂ');
          }
          break;
        case '8':// ㅅㅎ
          if(last === 'ㅅ'){
            current.pop();
            current.push('ㅎ');
          } else if(last === 'ㅎ'){
            current.pop();
            current.push('ㅅ');
          } else {
            current.push('ㅅ');
          }
          break;
        case '9':// ㅈㅎ
          if(last === 'ㅈ'){
            current.pop();
            current.push('ㅎ');
          } else if(last === 'ㅎ'){
            current.pop();
            current.push('ㅈ');
          } else {
            current.push('ㅈ');
          }
          break;
        case '0':// ㅇㅁ
          if(last === 'ㅇ'){
            current.pop();
            current.push('ㅁ');
          } else if(last === 'ㅁ'){
            current.pop();
            current.push('ㅇ');
          } else {
            current.push('ㅇ');
          }
          break;
      }
    }
    return Hangul.assemble(current);
  }

  $scope.inputEnter = function () {
    $scope.bShowInput = false;
  }

});