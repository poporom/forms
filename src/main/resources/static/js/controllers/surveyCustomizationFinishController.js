(function(){
  angular.module('app')
    .controller('SurveyCustomizationFinishController', SurveyCustomizationFinishController);

  SurveyCustomizationFinishController.$inject = ['SurveyService', '$location', '$routeParams', '$filter', '$scope'];

  function SurveyCustomizationFinishController(SurveyService, $location, $routeParams, $filter, $scope) {

    var self = this;
    self.getCurrentSurvey = getCurrentSurvey;
    self.saveSurvey = saveSurvey;
    self.setExpirationDate = setExpirationDate;

    self.$scope = $scope;
    
    init();

    function init() {
      if (!$scope.mc.checkUser()) {
        $location.path('/');
      }
      else {
        self.surveyHashedId = $routeParams.hashedId;
        getCurrentSurvey();
      }
    }

    function getCurrentSurvey() {
      SurveyService.getCurrentSurvey(self.surveyHashedId)
        .then(
        function(response){
          self.survey = response;
          self.minDate = new Date();
          self.minDate.setDate(self.minDate.getDate() + 1);
          self.surveyDeactivation = 1;
          setExpirationDate();
        }, 
        function(error){
          console.log(error);
          self.initError = error;
        })
    }

    function saveSurvey() {
      if(!checkForm()) {
        return;
      }

      if(self.surveyDeactivation === 1) {
        self.survey.isActive = true;
        self.survey.expirationDate = null;
      }

      if(self.surveyDeactivation === 2) {
        self.survey.isActive = false;
      }

      SurveyService.saveSurvey(angular.copy(self.survey))
        .then(
        function(response){
          $location.path('/home');
        }, 
        function(error){
          console.log(error);
          self.error = error;
        })
    }

    function setExpirationDate() {
      self.survey.expirationDate = new Date();
      self.survey.expirationDate.setDate(self.survey.expirationDate.getDate() + 1);
    }

    function checkForm() {
      var focusedElement;

      if(self.surveyForm.$invalid) {
        if(self.surveyForm.expirationDate && self.surveyForm.expirationDate.$invalid) {
          self.surveyForm.expirationDate.$setDirty();
          focusedElement = '#expirationDate';
        }

        if(self.surveyForm.exitMsg.$invalid) {
          self.surveyForm.exitMsg.$setDirty();
          focusedElement = '#exitMsg';
        }

        $(focusedElement).focus();

        return false;
      }

      return true;
    }

  };
})();