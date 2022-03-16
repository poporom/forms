package com.poisk.core.model;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

@Entity
public class Survey extends BaseEntity {
	
	@Size(max = 512)
	@Column(nullable = false)
	private String name;
	
	@Column(nullable = false)
	private String creator;
	
	@Column(name = "hashed_id", unique = true, updatable = false, nullable = false)
	private String hashedId;
	
	@Column(name = "creation_date", updatable = false, nullable = false)
	@Temporal(TemporalType.DATE)
	private Date creationDate;
	
	@Column(name = "expiration_date")
	@Temporal(TemporalType.DATE)
	private Date expirationDate;
	
	@Size(max = 512)
	@Column(name = "exit_message", nullable = false)
	private String exitMessage;
	
	@Size(max = 512)
	@Column(nullable = false)
	private String description;
	
	@Column(name = "is_active", nullable = false)
	private Boolean isActive;
	
	@Column(name = "is_public", nullable = false)
	private Boolean isPublic;
	
	@Column(name = "is_flagged", nullable = false)
    private Boolean isFlagged;
	
	@Cascade(CascadeType.ALL)
	@OneToMany
	@JoinColumn(name = "survey_id")
	private List<Question> questions;
	
	@Cascade(CascadeType.ALL)
	@OneToMany
	@JoinColumn(name = "survey_id")
	private List<SurveyResult> surveyResults;
	
	@Cascade(CascadeType.ALL)
	@OneToMany
	@JoinColumn(name = "survey_id")
	private List<Comment> comments;
	
	public void generateHash() throws NoSuchAlgorithmException {
		String dateString = getCreationDate().toString();
		String userString = getCreator().toString();
		String hashString = userString + dateString;
		byte[] hashBytes = hashString.getBytes();
		MessageDigest m = MessageDigest.getInstance("MD5");
		byte[] hashedId = m.digest(hashBytes);
		String hashedIdString = new BigInteger(1, hashedId).toString(16);  
		this.setHashedId(hashedIdString);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getHashedId() {
		return hashedId;
	}

	public void setHashedId(String hashedId) {
		this.hashedId = hashedId;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	public String getExitMessage() {
		return exitMessage;
	}

	public void setExitMessage(String exitMessage) {
		this.exitMessage = exitMessage;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public Boolean getIsPublic() {
		return isPublic;
	}

	public void setIsPublic(Boolean isPublic) {
		this.isPublic = isPublic;
	}

	public Boolean getIsFlagged() {
		return isFlagged;
	}

	public void setIsFlagged(Boolean isFlagged) {
		this.isFlagged = isFlagged;
	}

	public List<Question> getQuestions() {
		return questions;
	}

	public void setQuestions(List<Question> questions) {
		this.questions = questions;
	}

	public List<SurveyResult> getSurveyResults() {
		return surveyResults;
	}

	public void setSurveyResults(List<SurveyResult> surveyResults) {
		this.surveyResults = surveyResults;
	}

	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

}
