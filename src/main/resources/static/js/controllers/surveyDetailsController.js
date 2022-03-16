(function(){
  angular.module('app')
    .controller('SurveyDetailsController', SurveyDetailsController);

  SurveyDetailsController.$inject = ['SurveyService', '$routeParams', '$scope', '$location'];

  function SurveyDetailsController(SurveyService, $routeParams, $scope, $location) {

    var self = this;
    self.getCurrentSurvey = getCurrentSurvey;

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
        }, 
        function(error){
          console.log(error);
          self.initError = error;
        });
    }

  }
})();