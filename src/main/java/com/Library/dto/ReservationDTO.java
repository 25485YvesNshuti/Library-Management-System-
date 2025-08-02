package com.Library.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationDTO {
    private Long id;
    private Long bookId;
    private String bookTitle;
    private Long userId;
    private String userName;
    private LocalDateTime reservedAt;
    private LocalDateTime notifiedAt;
    private LocalDateTime expiresAt;
    private String status;
}