package com.Library.model;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "issued_books")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IssuedBook {

    public enum Status {
        ISSUED,
        RETURNED,
        OVERDUE
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
    private User user; // Student

    @Column(name = "issued_at")
    private LocalDateTime issuedAt;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Column(name = "returned_at")
    private LocalDateTime returnedAt;

    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToOne(mappedBy = "issuedBook", cascade = CascadeType.ALL, orphanRemoval = true)
    private Renewal renewal;
}