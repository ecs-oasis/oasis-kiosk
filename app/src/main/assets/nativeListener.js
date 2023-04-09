let carNumActive = true;

function onCarNumDetected (carNum) { //"21고4321"
  console.log('onCarNumDetected', carNum);
  if(carNumActive && getCurrentPage() === 'nocarnum') {
    carNumActive = false;
    setTimeout(()=>{
      carNumActive = true;
    }, 1000);
    getAppScope().carNum = carNum;
    getCurrentScope().login({carNo:carNum});
  }
}

function readMagnetCard(){
  let cardInfo = AndroidClient.readMagnet();
  if (cardInfo) {
    cardInfo = JSON.parse(cardInfo);
  }
  console.log(cardInfo);
  return cardInfo;
}

function startCourse(course){
  try {
    if (course === 'premium') {
      AndroidClient.startPremiumCourse();
    } else if (course === 'standard') {
      AndroidClient.startStandardCourse();
    }
  }catch (e) {
    console.error(e);
  }
}