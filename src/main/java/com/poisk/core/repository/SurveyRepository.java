package com.poisk.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.poisk.core.model.Survey;

@Repository
public interface SurveyRepository extends JpaRepository<Survey, Integer> {

	List<Survey> findAllByCreator(String creator);
	
	Survey findByHashedId(String hashedId);

	Survey findSurveyByCommentsId(Integer id);

    Survey findSurveyByQuestionsId(Integer id);
}
