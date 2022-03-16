package com.poisk.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.poisk.core.model.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {

}
