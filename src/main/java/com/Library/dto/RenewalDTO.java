package com.Library.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RenewalDTO {
    private Long id;
    private Long issuedBookId;
    private Long userId;
    private String userName;
    private LocalDateTime requestedAt;
    private LocalDateTime approvedAt;
    private String status;
    private LocalDateTime newDueDate;
}