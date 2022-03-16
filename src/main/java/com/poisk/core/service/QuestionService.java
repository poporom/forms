package com.poisk.core.service;

import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.poisk.core.model.Question;
import com.poisk.core.repository.QuestionRepository;

@Service
public class QuestionService {

	private QuestionRepository questionRepository;
	
	@Autowired
	public QuestionService(QuestionRepository questionRepository) {
		this.questionRepository = questionRepository;
	}
	
    public List<Question> findAll() {
    	return questionRepository.findAll();
    }
    
    public Question findOne(Integer questionId) {
    	return questionRepository.getById(questionId);
    }

    @Transactional
    public Question save(Question question) {
    	return questionRepository.save(question);
    }

    @Transactional
    public void delete(Integer questionId) {
    	questionRepository.deleteById(questionId);
    }
    
	public Question findQuestionByAnswerId(Integer id) {
        return questionRepository.findByAnswersId(id);
    }
}
