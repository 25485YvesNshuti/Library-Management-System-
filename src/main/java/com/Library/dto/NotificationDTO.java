package com.Library.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDTO {
    private Long id;
    private Long userId;
    private String userName;
    private String type;
    private String message;
    private LocalDateTime sentAt;
    private String channel;
}