package com.Library.dto;
import lombok.*;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TopBookDTO {
    private Long bookId;
    private String title;
    private long borrowCount;
}