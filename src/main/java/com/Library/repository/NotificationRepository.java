package com.Library.repository;

import com.Library.model.Notification;
import com.Library.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findByUser(User user, Pageable pageable);
    Page<Notification> findByType(Notification.Type type, Pageable pageable);
    Page<Notification> findByChannel(Notification.Channel channel, Pageable pageable);
}