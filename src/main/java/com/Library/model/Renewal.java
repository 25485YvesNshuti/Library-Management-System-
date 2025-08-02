package com.Library.model;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "renewals")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Renewal {

    public enum Status {
        PENDING,
        APPROVED,
        REJECTED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relationships
    @OneToOne
    @JoinColumn(name = "issued_book_id", nullable = false)
    private IssuedBook issuedBook;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "requested_at")
    private LocalDateTime requestedAt;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "new_due_date")
    private LocalDateTime newDueDate;
}