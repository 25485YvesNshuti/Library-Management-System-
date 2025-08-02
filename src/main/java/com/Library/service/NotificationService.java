package com.Library.service;

import com.Library.dto.NotificationDTO;
import com.Library.model.Notification;
import com.Library.model.User;
import com.Library.repository.NotificationRepository;
import com.Library.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    public Page<NotificationDTO> listAllNotificationsPaged(int page, int size) {
        return notificationRepository.findAll(PageRequest.of(page, size)).map(this::toDTO);
    }

    @PreAuthorize("hasRole('STUDENT')")
    public Page<NotificationDTO> listMyNotificationsPaged(Long userId, int page, int size) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        return notificationRepository.findByUser(user, PageRequest.of(page, size)).map(this::toDTO);
    }

    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    public Page<NotificationDTO> listNotificationsByTypePaged(String type, int page, int size) {
        return notificationRepository.findByType(Notification.Type.valueOf(type), PageRequest.of(page, size)).map(this::toDTO);
    }

    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    public Page<NotificationDTO> listNotificationsByChannelPaged(String channel, int page, int size) {
        return notificationRepository.findByChannel(Notification.Channel.valueOf(channel), PageRequest.of(page, size)).map(this::toDTO);
    }

    // Send notification to user (ADMIN/LIBRARIAN)
    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    public NotificationDTO sendNotification(NotificationDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Notification notification = Notification.builder()
                .user(user)
                .type(Notification.Type.valueOf(dto.getType()))
                .message(dto.getMessage())
                .sentAt(LocalDateTime.now())
                .channel(Notification.Channel.valueOf(dto.getChannel()))
                .build();
        return toDTO(notificationRepository.save(notification));
    }

    private NotificationDTO toDTO(Notification notification) {
        return NotificationDTO.builder()
                .id(notification.getId())
                .userId(notification.getUser().getId())
                .userName(notification.getUser().getName())
                .type(notification.getType().name())
                .message(notification.getMessage())
                .sentAt(notification.getSentAt())
                .channel(notification.getChannel() == null ? null : notification.getChannel().name())
                .build();
    }
}