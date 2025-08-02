package com.Library.dto;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecentActivityDTO {
    private String type; // BOOK_ADDED, FINE_ADDED, USER_REGISTERED, etc
    private String description;
    private String timestamp; // ISO format or LocalDateTime
}