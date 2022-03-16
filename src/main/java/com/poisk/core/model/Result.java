package com.poisk.core.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

@Entity
public class Result extends BaseEntity {
	
	@Column(name = "question_id")
	private Integer questionId;
	
	@Column(name = "answer_id")
	private Integer answerId;
	
	private String optional;
	
	@Cascade(CascadeType.ALL)
	@OneToMany
	@JoinColumn(name = "result_id")
	private List<ResultBoolean> resultList;

	public Integer getQuestionId() {
		return questionId;
	}

	public void setQuestionId(Integer questionId) {
		this.questionId = questionId;
	}

	public Integer getAnswerId() {
		return answerId;
	}

	public void setAnswerId(Integer answerId) {
		this.answerId = answerId;
	}

	public String getOptional() {
		return optional;
	}

	public void setOptional(String optional) {
		this.optional = optional;
	}

	public List<ResultBoolean> getResultList() {
		return resultList;
	}

	public void setResultList(List<ResultBoolean> resultList) {
		this.resultList = resultList;
	}

}
