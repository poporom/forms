package com.poisk.core.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

@Entity
public class Question extends BaseEntity {
	
	@Column(name = "survey_id")
	private Integer surveyId;
	
	@Size(max=240)
	private String content;

	@Cascade(CascadeType.ALL)
	@OneToMany
	@JoinColumn(name = "question_id")
	private List<Answer> answers;
	
	@Column(name = "has_other_option")
	private Boolean hasOtherOption;
	
	@Column(name = "question_type")
	private Integer questionType;

	public Integer getSurveyId() {
		return surveyId;
	}

	public void setSurveyId(Integer surveyId) {
		this.surveyId = surveyId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public List<Answer> getAnswers() {
		return answers;
	}

	public void setAnswers(List<Answer> answers) {
		this.answers = answers;
	}

	public Integer getQuestionType() {
		return questionType;
	}

	public void setQuestionType(Integer questionType) {
		this.questionType = questionType;
	}

	public Boolean getHasOtherOption() {
		return hasOtherOption;
	}

	public void setHasOtherOption(Boolean hasOtherOption) {
		this.hasOtherOption = hasOtherOption;
	}

}
