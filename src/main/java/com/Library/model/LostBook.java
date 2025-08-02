package com.Library.model;

import lombok.*;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "lost_books")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LostBook {

    public enum Status {
        PENDING,
        PAID,
        WAIVED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relationships
    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "reported_at")
    private LocalDateTime reportedAt;

    @Column(nullable = false)
    private BigDecimal fineAmount;

    @Enumerated(EnumType.STRING)
    private Status status;
}