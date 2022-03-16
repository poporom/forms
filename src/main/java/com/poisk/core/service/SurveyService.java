package com.poisk.core.service;

import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.poisk.core.model.Survey;
import com.poisk.core.repository.SurveyRepository;

@Service
public class SurveyService {
	
    private SurveyRepository surveyRepository;

    @Autowired
    public SurveyService(SurveyRepository surveyRepository) {
        this.surveyRepository = surveyRepository;
    }
    
    public List<Survey> findAll() {
        return surveyRepository.findAll();
    }
    
    public List<Survey> findAllByCreator(String creator) {
    	return surveyRepository.findAllByCreator(creator);
    }
    
    public Survey findOne(Integer surveyId) {
    	return surveyRepository.getById(surveyId);
    }
    
    public Survey findByHashedId(String hashedId) {
    	return surveyRepository.findByHashedId(hashedId);
    }

    @Transactional
    public Survey save(Survey survey) {  	
    	return surveyRepository.save(survey);
    }

    @Transactional
    public void delete(Integer surveyId) {
    	surveyRepository.deleteById(surveyId);
    }

	public Survey findSurveyByCommentsId(Integer id) {
		 return surveyRepository.findSurveyByCommentsId(id);
	}

    public Survey findSurveyByQuestionId(Integer id) {
        return surveyRepository.findSurveyByQuestionsId(id);
    }

}
