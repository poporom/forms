package com.poisk.core.controller;

import java.security.NoSuchAlgorithmException;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.poisk.core.model.Answer;
import com.poisk.core.model.Comment;
import com.poisk.core.model.Question;
import com.poisk.core.model.Survey;
import com.poisk.core.model.User;
import com.poisk.core.service.SurveyService;
import com.poisk.core.service.UserService;

@RestController
@RequestMapping("/api/survey")
public class SurveyController {
	
	private SurveyService surveyService;
	private UserService userService;
	
	@Autowired
	public SurveyController(SurveyService surveyService, UserService userService) {
		this.surveyService = surveyService;
		this.userService = userService;
	}

	@PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
	@GetMapping
    public ResponseEntity<List<Survey>> findAll() {
    	List<Survey> surveys = surveyService.findAll();	
    	
    	if(surveys.isEmpty()) {
    		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    	}
    	
    	for(int i = 0; i < surveys.size(); i++) {
    		Survey survey = surveys.get(i);
    		survey = surveyIsActiveCheck(survey);
    		surveys.set(i, survey);
    	}
    	
        return new ResponseEntity<>(surveys, HttpStatus.OK);
    }
	
	@PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
	@GetMapping(path = "/creator")
    public ResponseEntity<List<Survey>> findAllByCreator() {
		User user = userService.getLoggedInUser();
		
		if(user == null) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		
    	List<Survey> surveys = surveyService.findAllByCreator(user.getUsername());	
    	
    	if(surveys.isEmpty()) {
    		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    	}
    	
    	for(int i = 0; i < surveys.size(); i++) {
    		Survey survey = surveys.get(i);
    		survey = surveyIsActiveCheck(survey);
    		surveys.set(i, survey);
    	}
    	
        return new ResponseEntity<>(surveys, HttpStatus.OK);
    }
	
	@GetMapping(path = "/{hashedId}")
	public ResponseEntity<Survey> findByHashedId(@PathVariable String hashedId) {
		Survey survey = surveyService.findByHashedId(hashedId);	
		
		if(survey == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
		surveyIsActiveCheck(survey);
		return new ResponseEntity<>(survey, HttpStatus.OK);
	}
	
	@GetMapping(path = "/{id}/comment")
	public ResponseEntity<List<Comment>> findAllCommentsFromSurvey(@PathVariable Integer id) {
		Survey survey = surveyService.findOne(id);
		
		if (survey == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }		
		
		List<Comment> comments = survey.getComments();
		
		if(comments.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		
		return new ResponseEntity<>(comments, HttpStatus.OK);
	}
  
	@PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
	@PostMapping
    public ResponseEntity<Survey> save(@RequestBody Survey survey) {
		User user = userService.getLoggedInUser();
		
		if(user == null) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		
		survey.setCreator(user.getUsername());
		survey.setCreationDate(new Date());
		
    	try {
			survey.generateHash();
		} catch (NoSuchAlgorithmException e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}  
    	
		Survey duplicateSurvey = surveyService.findByHashedId(survey.getHashedId());	
		
		if(duplicateSurvey != null) {
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}
		
		survey.setExpirationDate(new Date());
		survey.setExitMessage(new String());
		survey.setIsActive(false);
		survey.setIsPublic(false);
		survey.setIsFlagged(false);
		
		Survey createdSurvey = surveyService.save(survey);
		
    	return new ResponseEntity<>(createdSurvey, HttpStatus.CREATED);
    }
	
	@PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
	@PutMapping
    public ResponseEntity<Survey> update(@RequestBody Survey survey) {
		Survey surveyCheck = surveyService.findByHashedId(survey.getHashedId());
		
		if(surveyCheck == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
		User user = userService.getLoggedInUser();
		
		if(!user.getUsername().equals(survey.getCreator())) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		
		List<Question> questions = survey.getQuestions();
		
		for(int i = 0; i < questions.size(); i++) {
			Question question = questions.get(i);
			
			if(question.getQuestionType() == 2) {
				question.setAnswers(new ArrayList<Answer>());
			}
		}
		
		survey = surveyService.save(survey);
    	
    	return new ResponseEntity<>(survey, HttpStatus.OK);
    }
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PutMapping(path = "/{id}")
	public ResponseEntity<Survey> allowSurvey(@PathVariable Integer id) {
		Survey survey = surveyService.findOne(id);

		if(survey == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
		survey.setIsFlagged(false);
		surveyService.save(survey);
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
	@PutMapping(path = "/deactivate/{id}")
	public ResponseEntity<Survey> deactivate(@PathVariable Integer id) {
		Survey survey = surveyService.findOne(id);
		
		if(survey == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
		User user = userService.getLoggedInUser();
		
		if(!user.getUsername().equals(survey.getCreator())) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		
		survey.setExpirationDate(new Date());
		survey.setIsActive(false);
		surveyService.save(survey);
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
	@PutMapping(path = "/privacy/{id}")
	public ResponseEntity<Survey> togglePrivacy(@PathVariable Integer id) {
		Survey survey = surveyService.findOne(id);
		
		if(survey == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
		User user = userService.getLoggedInUser();
		
		if(!user.getUsername().equals(survey.getCreator())) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		
		survey.setIsPublic(!survey.getIsPublic());
		surveyService.save(survey);
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	
	@PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
	@DeleteMapping(path = "/{id}")
	public ResponseEntity<Survey> delete(@PathVariable Integer id) {
		Survey survey = surveyService.findOne(id);
		
		if(survey == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
		surveyService.delete(id);
		
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(path = "/question/{id}")
    public ResponseEntity<Survey> findSurveyByQuestion(@PathVariable Integer id) {
        Survey survey = surveyService.findSurveyByQuestionId(id);

        if (survey == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(survey, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping(path = "/comment/{id}")
	public ResponseEntity<Survey> findSurveyByCommentId(@PathVariable Integer id) {
		Survey survey = surveyService.findSurveyByCommentsId(id);

		if (survey == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<>(survey, HttpStatus.OK);
	}

	private Survey surveyIsActiveCheck(Survey survey) {
		Date currentDate = new Date();

		if (survey.getIsActive() != null && survey.getIsActive()) {
			return survey;
		}

		if(survey.getExpirationDate() == null || survey.getExpirationDate().after(currentDate)) {
			survey.setIsActive(true);
			survey = surveyService.save(survey);
		}
		
		if(survey.getExpirationDate() != null && (survey.getExpirationDate().before(currentDate) || survey.getExpirationDate().compareTo(currentDate) == 0)) {
			survey.setIsActive(false);
			survey = surveyService.save(survey);
		}
		
		return survey;
	}


}
