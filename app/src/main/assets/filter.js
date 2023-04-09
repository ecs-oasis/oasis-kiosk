app
  .filter('password', function() {
    return function(input) {
      let ret = "";
      for(let i=0; i<input.length; i++) {
        if(i === (input.length-1)) {
          ret += input[i]+'';
        } else {
          ret += '*';
        }
      }
      return ret;
    };
  })
  .filter('safeHtml', function ($sce) {
    return function (val) {
      return $sce.trustAsHtml(val);
    };
  });;