package com.poisk.core.service;

import java.util.List;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.poisk.core.model.Comment;
import com.poisk.core.repository.CommentRepository;

@Service
public class CommentService {

	private CommentRepository commentRepository;

	@Autowired
	public CommentService(CommentRepository commentRepository) {
		this.commentRepository = commentRepository;
	}

	public List<Comment> findAll() {
		return commentRepository.findAll();
	}

	public Comment findOne(Integer id) {
		return commentRepository.getById(id);
	}

	@Transactional
	public Comment save(Comment comment) {
		return commentRepository.save(comment);
	}

	@Transactional
	public void delete(Integer id) {
		commentRepository.deleteById(id);
	}

	@Transactional
	public void deleteByUser(String user) {
		commentRepository.deleteByPoster(user);
	}
}
