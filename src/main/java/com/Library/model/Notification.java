package com.Library.model;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    public enum Type {
        DUE_DATE_REMINDER,
        OVERDUE,
        RESERVED_AVAILABLE,
        LOST_BOOK_FINE,
        RENEWAL_APPROVAL,
        RENEWAL_REJECTION
        // Add more as needed
    }

    public enum Channel {
        EMAIL,
        SMS
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relationships
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Enumerated(EnumType.STRING)
    private Channel channel;
}