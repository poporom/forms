package com.poisk.core.controller;

import com.poisk.core.model.*;
import com.poisk.core.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@EnableScheduling
@RestController
@RequestMapping("/api/users")
public class UserController {

    private UserService userService;
    private CommentService commentService;
    private JavaMailSender javaMailSender;
    private SurveyService surveyService;
    private ImageService imageService;

    @Autowired
    public UserController(UserService userService,
                          CommentService commentService, JavaMailSender javaMailSender,
                          SurveyService surveyService, ImageService imageService) {
        this.imageService = imageService;
        this.commentService = commentService;
        this.userService = userService;
        this.surveyService = surveyService;
        this.javaMailSender = javaMailSender;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<List<User>> findAll() {
        List<User> users = userService.findAll();

        if (users == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        
        return new ResponseEntity<>(users, HttpStatus.OK);
    }
    
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(path = "/{id}")
    public ResponseEntity<User> findOne(@PathVariable("id") Integer id) {
        User user = userService.findOne(id);

        if(user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping(path = "/{id}/notifications")
	public ResponseEntity<List<Notification>> findAllNotificationsFromUser(@PathVariable("id") Integer id) {
		User user = userService.findOne(id);
		
		if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }		
		
		List<Notification> allNotifications = user.getNotifications();
		List<Notification> unreadNotifications = new ArrayList<Notification>();
		
		for(int i = 0; i < allNotifications.size(); i++) {
			Notification notification = allNotifications.get(i);
			unreadNotifications.add(notification);
			
			if(notification.getIsRead() == false) {
				notification.setIsRead(true);
				allNotifications.set(i, notification);
			}
		}
		
		if(unreadNotifications.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		
		user.setNotifications(allNotifications);
		userService.save(user);
		
		return new ResponseEntity<>(unreadNotifications, HttpStatus.OK);
	}

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable Integer id) {
        User foundUser = userService.findOne(id);

        for (UserRole role : foundUser.getRoles()) {
            if (role.getType().equals(UserRole.RoleType.ROLE_ADMIN)) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        }

        for (Survey survey : surveyService.findAll()) {
            if (survey.getCreator().equals(foundUser.getUsername())) {
                surveyService.delete(survey.getId());
            }
        }

        commentService.deleteByUser(foundUser.getUsername());
        userService.delete(id);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Object> save(@RequestBody User user) {
//Popov R.S. activation problems 20220217
//        VerificationToken token = new VerificationToken();
//        token.setToken(UUID.randomUUID().toString());

        if (userService.findByUsername(user.getUsername()) == null) {
            if (userService.findByEmail(user.getEmail()) == null) {
                //Popov R.S. activation problems 20220217
                //user.setIsEnabled(false);
                user.setIsEnabled(true);
                user.setRegistrationDate(new Date());

                //Popov R.S. activation problems 20220217
                //token.setUser(user);

                User savedUser = userService.save(user);

                Image defaultImage = imageService.findOne(1);
                defaultImage.getUsers().add(savedUser);
                imageService.save(defaultImage);

                //Popov R.S. activation problems 20220217
                //sendMail(user.getEmail(), "http://localhost:8080/api/users/activate/" + token.getToken());

                return new ResponseEntity<>(savedUser, HttpStatus.OK);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.TEXT_PLAIN).body("email");
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.TEXT_PLAIN).body("username");
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @PutMapping
    public ResponseEntity<Object> editUser(@RequestBody User user) {
        User editedUser = userService.save(user);

        if (editedUser == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(editedUser, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping(path = "/user/{username}")
    public ResponseEntity<User> findLoggedUser(@PathVariable("username") String username) {
        User loggedUser = userService.findByUsername(username);

        if (loggedUser == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(loggedUser, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping(path = "/block/{user_id}")
    public ResponseEntity<Object> changeStatus(@RequestBody String duration, @PathVariable("user_id") Integer userId) {
        User foundUser = userService.findOne(userId);
        
        if (foundUser == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if(duration.equals("Permanent")) {
            foundUser.setBanDate(null);
        } else {
            foundUser.setBanDate(addDay(new Date(), Integer.parseInt(duration)));
        }

        foundUser.setUserStatus(resolveStatus(foundUser));

        for (UserRole role : foundUser.getRoles()) {
            if (role.getType().equals(UserRole.RoleType.ROLE_ADMIN)) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        }

        userService.save(foundUser);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping("/login")
    public User user(Authentication authentication) {
        User user = userService.findByUsername(authentication.getName());
        UserStatus userStatus = new UserStatus();

        if(user.getUserStatus().getType().equals(UserStatus.UserStatusType.STATUS_INACTIVE) && new Date().after(user.getBanDate())) {
            userStatus.setType(UserStatus.UserStatusType.STATUS_ACTIVE);
            userStatus.setId(1);
            user.setUserStatus(userStatus);
            userService.save(user);
        }
        user.setPassword(null);

        return user;
    }

    private UserStatus resolveStatus(User foundUser) {
        UserStatus userStatus = new UserStatus();
        if (foundUser.getUserStatus().getType().equals(UserStatus.UserStatusType.STATUS_ACTIVE)) {
            userStatus.setType(UserStatus.UserStatusType.STATUS_INACTIVE);
            userStatus.setId(2);
        } else {
            userStatus.setType(UserStatus.UserStatusType.STATUS_ACTIVE);
            userStatus.setId(1);
        }
        return userStatus;
    }

    private Date addDay(Date date, int days)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days);
        return cal.getTime();
    }

    private void sendMail(String recipient, String activationLink) {
        SimpleMailMessage mail = new SimpleMailMessage();

        mail.setTo(recipient);
        mail.setSubject("Account verification for survey");
        mail.setText("Click on this link to activate your account: " + activationLink + "\n \n" + "This is an automatically generated email, please do not reply!");

        javaMailSender.send(mail);
    }
}
