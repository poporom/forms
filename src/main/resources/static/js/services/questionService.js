(function () {
  angular.module("app")
    .factory('QuestionService', QuestionService);

  QuestionService.$inject = ['$http', '$q'];

  function QuestionService($http, $q) {

    var service = {
      deleteQuestion: deleteQuestion,
      findAllQuestions: findAllQuestions,
      getQuestionByAnswer: getQuestionByAnswer
    };
    
    function deleteQuestion(id) {
      var def = $q.defer();
      var req = {
        method: 'DELETE',
        url: "/api/question/" + id
      };
      $http(req).success(function (data) {
        def.resolve(data);
      })
        .error(function () {
        def.reject("Failed to delete a question");
      });
      return def.promise;
    }
    
    function findAllQuestions() {
        var def = $q.defer();
        var req = {
            method: 'GET',
            url: "/api/question/"
        };
        $http(req)
            .success(function (data) {
                def.resolve(data);
            })
            .error(function () {
                def.reject("Failed to get all questions!");
            });
        return def.promise;
    }
    
    function getQuestionByAnswer(id) {
        var def = $q.defer();
        var req = {
            method: 'GET',
            url: "/api/question/answer/" + id
        };
        $http(req)
            .success(function (data) {
                def.resolve(data);
            })
            .error(function () {
                def.reject("Failed to get question for the given answer!");
            });
        return def.promise;
    }

    return service;

  }
} ());