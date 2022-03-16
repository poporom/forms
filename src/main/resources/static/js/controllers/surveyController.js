(function () {
  angular.module('app')
    .controller('SurveyController', SurveyController);

  SurveyController.$inject = ['CaptchaService', 'SurveyService', 'ResultService', 'NotificationService', '$routeParams', '$location', '$scope'];

  function SurveyController(CaptchaService, SurveyService, ResultService, NotificationService, $routeParams, $location, $scope) {

    var self = this;
    self.checkCaptcha = checkCaptcha;
    self.reportSurvey = reportSurvey;

    self.counter = 0;

    init();

    function init() {
      self.user = $scope.mc.checkUser();
      self.surveyHashedId = $routeParams.hashedId;
      getCurrentSurvey();
    }

    function getCurrentSurvey() {
      SurveyService.getCurrentSurvey(self.surveyHashedId)
        .then(
        function(response){
          self.survey = response;
          initCheck();
        }, 
        function(error){
          console.log(error);
          self.initError = error;
        }); 
    }

    function initCheck() {
      if(!self.user && !self.survey.isActive) {
        console.log("This survey is no longer active!");
        self.initError = "This survey is no longer active!";
        return;
      }
      else if(!self.user && !self.survey.isPublic) {
        console.log("This survey is not open for unregistered users!");
        self.initError = "This survey is not open for unregistered users!";
        return;
      }
      else if(self.user && self.user.username === self.survey.creator) {
				console.log("Survey submitter is the survey owner, redirecting...");
        $location.path('survey/results/' + self.surveyHashedId);
      }
      else if(self.user && !self.survey.isActive) {
				console.log("Survey is not active, redirecting...");
        $location.path('survey/results/' + self.surveyHashedId);
      }
      else if(self.user) {
        for(i = 0; i < self.survey.surveyResults.length; i++) {
          if(self.user && self.survey.surveyResults[i].submitedBy === self.user.username) {
            console.log("You have already completed this survey!");
            self.initError = "You have already completed this survey!";
            return;
          }
        }
      }
        
			generateSurveyResult(); 
    }

    function generateSurveyResult() {
      ResultService.generateSurveyResult(self.survey.id)
        .then(
        function(response){
          self.surveyResult = response;
          renderCaptcha();
        },
        function(error){
          console.log(error);
          self.initError = error;
        });
    }

    function renderCaptcha() {
      self.recaptchaId = grecaptcha.render('captcha-survey', {
        'sitekey' : '6LfO0SwUAAAAAI73tCuECJHe4MRpJyHQQUbH1RdZ'
      });
    }

    function checkCaptcha() {
      self.captchaResponse = grecaptcha.getResponse(self.recaptchaId);

      if(!self.captchaResponse) {
        console.log("Please complete the captcha!");
        self.error = "Please complete the captcha!";
        return;
      }

      CaptchaService.sendCaptchaResponse(self.captchaResponse)
        .then(
        function(response){
          submitSurvey();
        },
        function(error){
          console.log(error);
          self.error = error;
        });
    }

    function submitSurvey() {
      ResultService.submitSurvey(self.survey.id, self.surveyResult)
        .then(
        function(response){
          postNotification();
          $location.path('/survey/finish/' + self.surveyHashedId);
        }, 
        function(error){
          console.log(error);
          self.error = error;
        });
    }

    function postNotification() {
      NotificationService.postSurveyNotification(self.survey)
        .then(
        function(response) {}, function(error){
          console.log(error);
          self.error = error;
        })
    }

    function reportSurvey() {
      NotificationService.reportSurveyNotification(self.survey)
        .then(
        function(response) {
          $location.path('/home');
        }, 
        function(error){
          console.log(error);
          self.error = error;
        })
    }

  };
})();