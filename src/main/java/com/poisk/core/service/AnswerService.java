package com.poisk.core.service;

import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.poisk.core.model.Answer;
import com.poisk.core.repository.AnswerRepository;

@Service
public class AnswerService {

	private AnswerRepository answerRepository;
	
	@Autowired
	public AnswerService(AnswerRepository answerRepository) {
		this.answerRepository = answerRepository;
	}
	
	public List<Answer> findAll() {
    	return answerRepository.findAll();
    }
    
    public Answer findOne(Integer answerId) {
    	return answerRepository.getById(answerId);
    }

    @Transactional
    public Answer save(Answer answer) {
    	return answerRepository.save(answer);
    }

    @Transactional
    public void delete(Integer answerId) {
    	answerRepository.deleteById(answerId);
    }
}
