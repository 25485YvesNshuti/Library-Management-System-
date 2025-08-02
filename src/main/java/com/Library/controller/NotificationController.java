package com.Library.controller;

import com.Library.dto.NotificationDTO;
import com.Library.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // ADMIN/LIBRARIAN: List all notifications (paged)
    @GetMapping("/paged")
    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    public ResponseEntity<Page<NotificationDTO>> listAllNotificationsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(notificationService.listAllNotificationsPaged(page, size));
    }

    // STUDENT: List my notifications (paged)
    @GetMapping("/my/paged")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Page<NotificationDTO>> listMyNotificationsPaged(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = getUserId(authentication);
        return ResponseEntity.ok(notificationService.listMyNotificationsPaged(userId, page, size));
    }

    // ADMIN/LIBRARIAN: List by type (paged)
    @GetMapping("/type/{type}/paged")
    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    public ResponseEntity<Page<NotificationDTO>> listNotificationsByTypePaged(
            @PathVariable String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(notificationService.listNotificationsByTypePaged(type, page, size));
    }

    // ADMIN/LIBRARIAN: List by channel (paged)
    @GetMapping("/channel/{channel}/paged")
    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    public ResponseEntity<Page<NotificationDTO>> listNotificationsByChannelPaged(
            @PathVariable String channel,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(notificationService.listNotificationsByChannelPaged(channel, page, size));
    }

    // ADMIN/LIBRARIAN: Send notification
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    public ResponseEntity<NotificationDTO> sendNotification(@RequestBody NotificationDTO dto) {
        return ResponseEntity.ok(notificationService.sendNotification(dto));
    }

    private Long getUserId(Authentication authentication) {
        return Long.parseLong(authentication.getName());
    }
}