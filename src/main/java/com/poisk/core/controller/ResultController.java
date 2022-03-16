package com.poisk.core.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.poisk.core.model.Question;
import com.poisk.core.model.Result;
import com.poisk.core.model.ResultBoolean;
import com.poisk.core.model.Survey;
import com.poisk.core.model.SurveyResult;
import com.poisk.core.model.User;
import com.poisk.core.service.ResultService;
import com.poisk.core.service.SurveyService;
import com.poisk.core.service.UserService;

@RestController
@RequestMapping("/api/result")
public class ResultController {
	
	private ResultService resultService;
	private SurveyService surveyService;
	private UserService userService;
	
	@Autowired
	public ResultController(ResultService resultService, SurveyService surveyService, UserService userService) {
		this.resultService = resultService;
		this.surveyService = surveyService;
		this.userService = userService;
	}
	
	// Unused
	@PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
	@GetMapping
	public ResponseEntity<List<SurveyResult>> findAll() {
		List<SurveyResult> surveyResults = resultService.findAll();
		
		if(surveyResults.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		
		return new ResponseEntity<>(surveyResults, HttpStatus.OK);
	}
	
	@PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
	@GetMapping(path = "/survey/{surveyId}")
	public ResponseEntity<List<SurveyResult>> findBySurveyId(@PathVariable Integer surveyId) {
		Survey survey = surveyService.findOne(surveyId);
		
		if(survey == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
		List<SurveyResult> surveyResults = resultService.findBySurveyId(surveyId);
		
		if(surveyResults.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		
		return new ResponseEntity<>(surveyResults, HttpStatus.OK);
	}
	
	// Unused
	@PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
	@GetMapping(path = "/{id}")
	public ResponseEntity<SurveyResult> findOne(@PathVariable Integer id) {
		SurveyResult surveyResult = resultService.findOne(id);
		
		if(surveyResult == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<>(surveyResult, HttpStatus.OK);
	}
	
	@GetMapping(path = "/generate/{surveyId}")
	public ResponseEntity<SurveyResult> generateSurveyResult(@PathVariable Integer surveyId) {
		Survey survey = surveyService.findOne(surveyId);
		
		if(survey == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
		if(survey.getIsActive() == false) {
			return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
		}
		
		SurveyResult surveyResult = new SurveyResult();
		
		User user = userService.getLoggedInUser();
		
		if(user != null && user.getUsername().equals(survey.getCreator())) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		
		List<SurveyResult> surveyResults = survey.getSurveyResults();
		
		for(int i = 0; i < surveyResults.size(); i++) {
			String poster = surveyResults.get(i).getSubmitedBy();
			
			if(user != null && poster.equals(user.getUsername())) {
				return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
			}
		}
		
		if(user == null && survey.getIsPublic() == false) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		
		if(user == null) {
			surveyResult.setSubmitedBy("anonymous");
		}
		else {
			surveyResult.setSubmitedBy(user.getUsername());
		}
		
		surveyResult.setCreationDate(new Date());
		surveyResult.setSurveyId(survey.getId());
		
		List<Question> questions = survey.getQuestions();
		
		if(questions.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
		}
		
		List<Result> results = new ArrayList<Result>();
		
		for(int i = 0; i < questions.size(); i++) {
			if(questions.get(i).getQuestionType() == 1) {
				Result result = new Result();
				result.setQuestionId(questions.get(i).getId());
				result.setAnswerId(questions.get(i).getAnswers().get(0).getId());
				result.setOptional(null);
				results.add(result);
			}
			else if(questions.get(i).getQuestionType() == 2) {
				Result result = new Result();
				result.setQuestionId(questions.get(i).getId());
				result.setAnswerId(0);
				result.setOptional(null);
				results.add(result);
			}
			else if(questions.get(i).getQuestionType() == 3) {
				List<ResultBoolean> resultList = new ArrayList<ResultBoolean>();
				
				for(int j = 0; j < questions.get(i).getAnswers().size(); j++) {
					ResultBoolean resultBoolean = new ResultBoolean();
					resultBoolean.setIsChecked(false);
					resultList.add(resultBoolean);
				}
				
				Result result = new Result();
				result.setQuestionId(questions.get(i).getId());
				result.setAnswerId(null);
				result.setOptional(null);
				result.setResultList(resultList);
				results.add(result);
			}
		}
		
		surveyResult.setResults(results);
		resultService.save(surveyResult);
		
		return new ResponseEntity<>(surveyResult, HttpStatus.OK);
	}
	
	@PostMapping(path = "/{surveyId}")
	public ResponseEntity<Object> save(@PathVariable Integer surveyId, @RequestBody SurveyResult surveyResult) {
		Survey survey = surveyService.findOne(surveyId);
		
		if(survey == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
		if(survey.getIsActive() == false) {
			return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
		}
		
		User user = userService.getLoggedInUser();
		
		if(user != null && user.getUsername().equals(survey.getCreator())) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		
		List<SurveyResult> surveyResults = survey.getSurveyResults();
		
		@SuppressWarnings("unused")
		Boolean userCheck;
				
		if(user != null) {
			userCheck = false;
			
			for(int i = 0; i < surveyResults.size(); i++) {
				String poster = surveyResults.get(i).getSubmitedBy();
				
				if(poster.equals(user.getUsername())) {
					userCheck = true;
				}
			}
		}
		else {
			userCheck = true;
		}
		
		if(userCheck = false) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		
		if(user == null && survey.getIsPublic() == false) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		
		resultService.save(surveyResult);
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	// Unused
	@PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
	@DeleteMapping(path = "/{id}")
	public ResponseEntity<Object> delete(@PathVariable Integer id) {
		SurveyResult surveyResult = resultService.findOne(id);
		
		if(surveyResult == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
		resultService.delete(id);
		
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
