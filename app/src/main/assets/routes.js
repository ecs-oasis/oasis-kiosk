app
  .config(['$stateProvider', '$urlRouterProvider',function ($stateProvider, $urlRouterProvider) {
    $stateProvider
      .state('main', {
        templateUrl: 'pages/main/main.html',
        url: '/main'
      })
      .state('welcome', {
        templateUrl: 'pages/welcome/welcome.html',
        url: '/welcome'
      })
      .state('welcomehome', {
        templateUrl: 'pages/welcomehome/welcomehome.html',
        url: '/welcomehome',
      })
      .state('entercar', {
        templateUrl: 'pages/entercar/entercar.html',
        url: '/entercar',
      })
      .state('nocarnum', {
        templateUrl: 'pages/nocarnum/nocarnum.html',
        url: '/nocarnum'
      })
      .state('inputcarnum', {
        templateUrl: 'pages/inputcarnum/inputcarnum.html',
        url: '/inputcarnum'
      })
      .state('inputbirth', {
        templateUrl: 'pages/inputbirth/inputbirth.html',
        url: '/inputbirth',
      })
      .state('inputcardpw', {
        templateUrl: 'pages/inputcardpw/inputcardpw.html',
        url: '/inputcardpw',
      })
      .state('agree', {
        templateUrl: 'pages/agree/agree.html',
        url: '/agree'
      })
      .state('insertcard', {
        templateUrl: 'pages/insertcard/insertcard.html',
        url: '/insertcard',
        params: { mode : null}
      })
      .state('course', {
        templateUrl: 'pages/course/course.html',
        url: '/course'
      })
      .state('membership', {
        templateUrl: 'pages/membership/membership.html',
        url: '/membership'
      })
    $urlRouterProvider.otherwise('/nocarnum')
  }]);