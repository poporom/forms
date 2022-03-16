package com.poisk.core.controller;

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
import com.poisk.core.model.Comment;
import com.poisk.core.model.Survey;
import com.poisk.core.model.User;
import com.poisk.core.service.CommentService;
import com.poisk.core.service.SurveyService;
import com.poisk.core.service.UserService;

@RestController
@RequestMapping("/api/comment")
public class CommentController {

	private CommentService commentService;
	private UserService userService;
	private SurveyService surveyService;

	@Autowired
    public CommentController(CommentService commentService, UserService userService, SurveyService surveyService) {
        this.commentService = commentService;
        this.userService = userService;
        this.surveyService = surveyService;
    }
    
    @GetMapping
    public ResponseEntity<List<Comment>> findAll() {
		List<Comment> comments = commentService.findAll();
		
		if(comments.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		
		return new ResponseEntity<>(comments, HttpStatus.OK);
	}
    
    // Unused
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @GetMapping(path = "/{id}")
    public ResponseEntity<Comment> findOne(@PathVariable Integer id) {
    	Comment comment = commentService.findOne(id);
		return new ResponseEntity<>(comment, HttpStatus.OK);
    }
    
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @PostMapping(path = "/{surveyId}")
    public ResponseEntity<Object> save(@PathVariable Integer surveyId, @RequestBody Comment comment) {
    	Survey survey = surveyService.findOne(surveyId);
		
		if(survey == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
	    User user = userService.getLoggedInUser();
	    
	    if(user == null) {
	    	return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
	    }
	    else{
	    	comment.setPoster(user.getUsername());
	    }

	    comment.setIsFlagged(false);
	    comment.setCreationDate(new Date());
    	survey.getComments().add(comment);
    	surveyService.save(survey);
        
    	return new ResponseEntity<>(HttpStatus.OK);

    }
    
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping(path = "/{commentId}")
    public ResponseEntity<Comment> allowComment(@PathVariable Integer commentId) {
    	Comment comment = commentService.findOne(commentId);
    	
    	if(comment == null) {
    		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    	}
    	
    	comment.setIsFlagged(false);
    	commentService.save(comment);
    	
    	return new ResponseEntity<>(HttpStatus.OK);
    }
    
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @DeleteMapping(path = "/{commentId}")
    public ResponseEntity<Object> delete(@PathVariable Integer commentId) {
    	Comment comment = commentService.findOne(commentId);
    	
    	if(comment == null) {
    		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    	}
    	
    	commentService.delete(commentId);
    	
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
