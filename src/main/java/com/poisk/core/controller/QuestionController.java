package com.poisk.core.controller;

import java.security.NoSuchAlgorithmException;
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
import com.poisk.core.model.Survey;
import com.poisk.core.model.User;
import com.poisk.core.service.QuestionService;
import com.poisk.core.service.SurveyService;
import com.poisk.core.service.UserService;

@RestController
@RequestMapping("/api/question")
public class QuestionController {

	private QuestionService questionService;
	private SurveyService surveyService;
	private UserService userService;
	
	@Autowired
	public QuestionController(QuestionService questionService, SurveyService surveyService, UserService userService) {
		this.questionService = questionService;
		this.surveyService = surveyService;
		this.userService = userService;
	}

	@PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
	@GetMapping
	public ResponseEntity<List<Question>> findAll() {
		List<Question> questions = questionService.findAll();
		
		if(questions.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		
		return new ResponseEntity<>(questions, HttpStatus.OK);
	}
	
	// Unused
	@PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
	@GetMapping(path = "/{id}")
	public ResponseEntity<Question> findOne(@PathVariable Integer id) {
		Question question = questionService.findOne(id);
		
		if(question == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<>(question, HttpStatus.OK);
	}
	
	// Unused
	@PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
	@PostMapping(path = "/{surveyId}")
	public ResponseEntity<Question> save(@PathVariable Integer surveyId, @RequestBody Question question) throws NoSuchAlgorithmException {
		Survey survey = surveyService.findOne(surveyId);
		
		if(survey == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
		User user = userService.getLoggedInUser();
		
		if(!user.getUsername().equals(survey.getCreator())) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		
		question.setSurveyId(survey.getId());
    	survey.getQuestions().add(question);  	
        surveyService.save(survey);
        
    	return new ResponseEntity<>(question, HttpStatus.OK);
    }
	
	@PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
	@DeleteMapping(path = "/{questionId}")
	public ResponseEntity<Object> delete(@PathVariable Integer questionId) {
		Question question = questionService.findOne(questionId);
		
		if(question == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
		questionService.delete(questionId);
		
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
