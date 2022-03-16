(function () {
  angular.module('app')
    .controller('HomeController', HomeController);

  HomeController.$inject = ['SurveyService', '$scope', '$location'];

  function HomeController(SurveyService, $scope, $location) {

    var self = this;
    self.deleteSurvey = deleteSurvey;
    self.setCurrentSurvey = setCurrentSurvey;
    self.deactivateSurvey = deactivateSurvey;
    self.toggleSurveyPrivacy = toggleSurveyPrivacy;

    init();

    function init(){
      if (!$scope.mc.checkUser()) {
        $location.path('/');
      }
      else {
        getUserSurveys();
      }
    }

    function getUserSurveys(){
      SurveyService.getUserSurveys()
        .then(
        function(response) {
          self.surveys = response;
        }, 
        function(error){
          console.log(error);
          self.initError = error;
        })
    }

    function setCurrentSurvey(survey) {
      self.currentSurvey = survey;
    }

    function deleteSurvey() {
      SurveyService.deleteSurvey(self.currentSurvey.id)
        .then(
        function(response) {
          $('#deleteSurveyModal').modal('hide');
          getUserSurveys();
        }, 
        function(error){
          console.log(error);
          self.error = error;
        })
    }

    function deactivateSurvey() {
      SurveyService.deactivateSurvey(self.currentSurvey.id)
        .then(
        function(response){
          getUserSurveys();
        }, 
        function(error){
          console.log(error);
          self.error = error;
        })
    }

    function toggleSurveyPrivacy() {
      SurveyService.toggleSurveyPrivacy(self.currentSurvey.id)
        .then(
        function(response){
          getUserSurveys();
        }, 
        function(error){
          console.log(error);
          self.error = error;
        })
    }
  };
})();
