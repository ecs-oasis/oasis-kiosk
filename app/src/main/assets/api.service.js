app.service('apiService', function($http, $state)
{
  if(typeof AndroidClient !== 'undefined') {
    this.host = AndroidClient.getHost();
  } else {
    // this.host = 'http://13.124.85.244:3001';
    this.host = 'https://api.oasiscar.link';
  }
  this.accessToken = null;
  this.setAccessToken = function (accessToken) {
    this.accessToken = accessToken;
  }

  //결제
  this.registerCard = function({cardNo, vldDtMon, vldDtYear}, onSuccess, onError) {
    let req = {
      method: 'POST',
      url: this.host + '/card/register',
      headers: {
        'accept': '*/*',
        'Content-Type': 'application/json',
        'Authorization': 'Bearer ' + this.accessToken
      },
      data: {cardNo, vldDtMon, vldDtYear}
    }
    $http(req).then(onSuccess, onError);
  };

  this.payCard = function({productId, isSubscribe}, onSuccess, onError) {
    let req = {
      method: 'POST',
      url: this.host + '/card/pay/billingkey',
      headers: {
        'accept': '*/*',
        'Content-Type': 'application/json',
        'Authorization': 'Bearer ' + this.accessToken
      },
      data: {productId, isSubscribe}
    }
    $http(req).then(onSuccess, onError);
  };

  this.payCardNew = function({cardNo, vldDtMon, vldDtYear, productId, isSubscribe}, onSuccess, onError) {
    let req = {
      method: 'POST',
      url: this.host + '/card/pay',
      headers: {
        'accept': '*/*',
        'Content-Type': 'application/json',
        'Authorization': 'Bearer ' + this.accessToken
      },
      data: {cardNo, vldDtMon, vldDtYear, productId, isSubscribe}
    }
    $http(req).then(onSuccess, onError);
  };

  this.changeMembership = function({productId}, onSuccess, onError) {
    let req = {
      method: 'POST',
      url: this.host + '/card/change/membership',
      headers: {
        'accept': '*/*',
        'Content-Type': 'application/json',
        'Authorization': 'Bearer ' + this.accessToken
      },
      data: {productId}
    }
    $http(req).then(onSuccess, onError);
  };

  this.cancelCard = function(onSuccess, onError) {
    let req = {
      method: 'POST',
      url: this.host + '/card/cancel/membership',
      headers: {
        'accept': '*/*',
        'Content-Type': 'application/json',
        'Authorization': 'Bearer ' + this.accessToken
      },
    }
    $http(req).then(onSuccess, onError);
  };

  //default
  this.info = function() {
  };

  //인증
  this.login = function({carNo}) {
    if(!isValidCarNum(carNo)){
      return;
    }
    let req = {
      method: 'POST',
      url: this.host + '/auth/login',
      headers: {
        'accept': '*/*',
        'Content-Type': 'application/json'
      },
      data: {carNo}
    }
    $http(req).then((res)=>{
      getAppScope().user = res.data;
      this.setAccessToken(res.data.accessToken);
      if(!res.data.hasAgreedTerms) {
        $state.go('welcome');
      }
      else if(!res.data.subscriptionStatus || res.data.subscriptionStatus === "NONE" || res.data.subscriptionStatus.includes('FAILED')) {
        $state.go('course');
      }
      // else if(res.data.isUsedToday){
      //   getAppScope().showDialogTxt('err', "오늘 이미 이용하셨습니다.\n하루 한번 이용 가능합니다.", function (){
      //     getAppScope().$state.go('nocarnum');
      //   })
      // }
      else {
        let course;
        if(res.data.subscriptionStatus === 'MEMBERSHIP_PREMIUM'){
          course= {name:"프리미엄 코스"}
        } else {
          course= {name:"디럭스 코스"}
        }
        getAppScope().course = course;
        $state.go('welcomehome');
      }
    }, (res)=>{
      console.error(res);
      showSystemErrorDialog();
    });
  };

  //사용자
  this.updateTerms = function({hasAgreedTerms, hasAgreedMarketing}, onSuccess, onError) {
    let req = {
      method: 'PATCH',
      url: this.host + '/users/me/terms',
      headers: {
        'accept': '*/*',
        'Content-Type': 'application/json',
        'Authorization': 'Bearer ' + this.accessToken
      },
      data: {hasAgreedTerms, hasAgreedMarketing}
    }
    $http(req).then(onSuccess, onError);
  };

  //세차코스
  this.getProducts = function(onSuccess, onError) {
    let req = {
      method: 'GET',
      url: this.host + '/products',
      headers: {
        'accept': '*/*',
        'Authorization': 'Bearer ' + this.accessToken
      },
    }
    $http(req).then(onSuccess, onError);
  };
});