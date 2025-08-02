package com.Library.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookDTO {
    private Long id;
    private String title;
    private String author;
    private Long categoryId;
    private String categoryName;
    private Integer totalCopies;
    private Integer availableCopies;
    private LocalDateTime createdAt;
}