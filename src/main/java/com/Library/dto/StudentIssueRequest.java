package com.Library.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentIssueRequest {
    private Long bookId;
    private Long userId; // Optional for admin flow
    private LocalDateTime dueDate;
}
