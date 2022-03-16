package com.poisk.core.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.poisk.core.model.Notification;
import com.poisk.core.repository.NotificationRepository;

@Service
public class NotificationService {
	
	private NotificationRepository notificationRepository;

	@Autowired
	public NotificationService(NotificationRepository notificationRepository ) {
		this.notificationRepository = notificationRepository;
	}
	
	public List<Notification> findAll() {
		return notificationRepository.findAll();
	}
	
	public Notification findOne(Integer id) {
		return notificationRepository.getById(id);
	}

	@Transactional
	public Notification save(Notification notification) {
		return notificationRepository.save(notification);
	}

	@Transactional
	public void delete(Integer id) {
		notificationRepository.deleteById(id);
	}

}
