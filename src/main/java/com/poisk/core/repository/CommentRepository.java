package com.poisk.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.poisk.core.model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

    void deleteByPoster(String poster);
}
