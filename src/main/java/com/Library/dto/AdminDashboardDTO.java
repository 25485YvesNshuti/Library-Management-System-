package com.Library.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminDashboardDTO {
    private long totalUsers;
    private long totalAdmins;
    private long totalLibrarians;
    private long totalStudents;
    private long totalBooks;
    private long booksAvailable;
    private long booksIssued;
    private long booksOverdue;
    private BigDecimal totalFinesCollected;
    private BigDecimal totalFinesUnpaid;
    private long lostBooksPending;
    private long lostBooksPaid;
    private long lostBooksWaived;
    private long pendingRenewals;
    private long pendingReservations;
    private long notificationsSent;
    private List<TopBookDTO> topBooks;
    private List<TopBorrowerDTO> topBorrowers;
    private List<RecentActivityDTO> recentActivity;
}