package com.Library.dto;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TopBorrowerDTO {
    private Long userId;
    private String userName;
    private long borrowCount;
}