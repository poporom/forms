(function () {
  angular.module('app')
    .controller('SearchController', SearchController);

  SearchController.$inject = ['SurveyService', '$scope', '$location'];

  function SearchController(SurveyService, $scope, $location) {

    init();

    function init(){
      self.user = $scope.mc.checkUser();

      if (!self.user) {
        $location.path('/');
      }
      else {
        $scope.mc.search = '';
        getSurveys();
      }
    }

    function getSurveys(){
      SurveyService.getSurveys()
        .then(
        function(response){
          self.surveys = response;
        }, 
        function(error){
          console.log(error);
          self.initError = error;
        });
    }
  };
})();
