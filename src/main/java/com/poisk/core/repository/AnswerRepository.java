package com.poisk.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.poisk.core.model.Answer;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Integer> {

}
