package com.poisk.core.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.poisk.core.model.SurveyResult;
import com.poisk.core.repository.ResultRepository;

@Service
public class ResultService {

	private ResultRepository resultRepository;
	
	@Autowired
	public ResultService(ResultRepository resultRepository) {
		this.resultRepository = resultRepository;
	}
	
	public List<SurveyResult> findAll() {
		return resultRepository.findAll();
	}
	
	public SurveyResult findOne(Integer id) {
		return resultRepository.getById(id);
	}
	
	public List<SurveyResult> findBySurveyId(Integer surveyId) {
		return resultRepository.findBySurveyId(surveyId);
	}

	@Transactional
	public SurveyResult save(SurveyResult surveyResult) {
		return resultRepository.save(surveyResult);
	}

	@Transactional
	public void delete(Integer id) {
		resultRepository.deleteById(id);
	}
	
}
