function getCurrentPage() {
  return getCurrentScope().state;
}

function getAppScope() {
  return angular.element(document.body).scope();
}

function getCurrentScope() {
  return angular.element(document.querySelector('ui-view').firstChild).scope();
}

function isValidCarNum(str) {
  if (/^\d{2}[가-힣]\d{4}/.exec(str) !== null && str.length === 7) {
    return true;
  }
  if (/^\d{3}[가-힣]\d{4}/.exec(str) !== null && str.length === 8) {
    return true;
  }
  return false
}

function isValidBirth(str) {
  if(!str) {
    return false;
  }
  if(str.length < 6) {
    return false;
  }
  let yy = parseInt(str[0] + str[1]);
  let mm = parseInt(str[2] + str[3]);
  let dd = parseInt(str[4] + str[5]);
  if(yy < 1) {
    return false;
  }
  if(mm < 1 || mm > 12) {
    return false;
  }
  if(dd < 1 || dd > 31) {
    return false;
  }
  return true;
}

function showSystemErrorDialog(){
  getAppScope().showDialogTxt('err', "시스템 에러가 발생했습니다. \n 잠시 후 다시 이용해 주세요.", function (){
    getAppScope().hideDialogTxt();
    getAppScope().$state.go('nocarnum');
  })
}