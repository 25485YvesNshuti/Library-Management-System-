package com.Library.service;

import com.Library.dto.AdminDashboardDTO;
import com.Library.dto.TopBookDTO;
import com.Library.dto.TopBorrowerDTO;
import com.Library.dto.RecentActivityDTO;
import com.Library.model.*;
import com.Library.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminDashboardService {
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final IssuedBookRepository issuedBookRepository;
    private final FineRepository fineRepository;
    private final LostBookRepository lostBookRepository;
    private final RenewalRepository renewalRepository;
    private final ReservationRepository reservationRepository;
    private final NotificationRepository notificationRepository;

   public AdminDashboardDTO getDashboard() {
    long totalUsers = userRepository.count();
    long totalAdmins = userRepository.countByRole(User.Role.ADMIN);
    long totalLibrarians = userRepository.countByRole(User.Role.LIBRARIAN);
    long totalStudents = userRepository.countByRole(User.Role.STUDENT);

    long totalBooks = bookRepository.count();

    // Sum available copies for all books
    long booksAvailable = bookRepository.findAll().stream()
        .mapToLong(Book::getAvailableCopies)
        .sum();

    long booksIssued = issuedBookRepository.countByStatus(IssuedBook.Status.ISSUED);
    long booksOverdue = issuedBookRepository.countByStatus(IssuedBook.Status.OVERDUE);

    // Optionally cross-check: booksIssued = totalBooks - booksAvailable

    BigDecimal totalFinesCollected = BigDecimal.ZERO;
    List<Fine> paidFines = fineRepository.findByPaid(true);
    if (paidFines != null && !paidFines.isEmpty()) {
        totalFinesCollected = paidFines.stream().map(Fine::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    BigDecimal totalFinesUnpaid = BigDecimal.ZERO;
    List<Fine> unpaidFines = fineRepository.findByPaid(false);
    if (unpaidFines != null && !unpaidFines.isEmpty()) {
        totalFinesUnpaid = unpaidFines.stream().map(Fine::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    long lostBooksPending = 0;
    long lostBooksPaid = 0;
    long lostBooksWaived = 0;
    try {
        lostBooksPending = lostBookRepository.findByStatus(LostBook.Status.PENDING, null).getTotalElements();
        lostBooksPaid = lostBookRepository.findByStatus(LostBook.Status.PAID, null).getTotalElements();
        lostBooksWaived = lostBookRepository.findByStatus(LostBook.Status.WAIVED, null).getTotalElements();
    } catch (Exception e) {
        // Defensive: If repo method fails, don't block dashboard
    }

    long pendingRenewals = 0;
    long pendingReservations = 0;
    try {
        pendingRenewals = renewalRepository.findByStatus(Renewal.Status.PENDING, null).getTotalElements();
        pendingReservations = reservationRepository.findByStatus(Reservation.Status.WAITING, null).getTotalElements();
    } catch (Exception e) {
        // Defensive
    }

    long notificationsSent = notificationRepository.count();

    // Top borrowed books (last 30 days)
    List<TopBookDTO> topBooks = new ArrayList<>();
    try {
        List<Object[]> topBooksRaw = issuedBookRepository.findTopBorrowedBooks(LocalDateTime.now().minusDays(30));
        if (topBooksRaw != null) {
            topBooks = topBooksRaw.stream()
                .map(r -> new TopBookDTO((Long) r[0], (String) r[1], (Long) r[2]))
                .collect(Collectors.toList());
        }
    } catch (Exception e) {
        // Defensive
    }

    // Top borrowers (last 30 days)
    List<TopBorrowerDTO> topBorrowers = new ArrayList<>();
    try {
        List<Object[]> topBorrowersRaw = issuedBookRepository.findTopBorrowers(LocalDateTime.now().minusDays(30));
        if (topBorrowersRaw != null) {
            topBorrowers = topBorrowersRaw.stream()
                .map(r -> {
                    Long userId = (Long) r[0];
                    String name = (String) r[1];
                    Long count = (Long) r[2];
                    // Defensive: If name is null, fetch from userRepository
                    if (name == null || name.isEmpty()) {
                        Optional<User> userOpt = userRepository.findById(userId);
                        name = userOpt.map(User::getName).orElse("Unknown");
                    }
                    return new TopBorrowerDTO(userId, name, count);
                })
                .collect(Collectors.toList());
        }
    } catch (Exception e) {
        // Defensive
    }

    // Recent activity (last 7 days) - (TODO: Implement if you have an Activity/Event table)
    List<RecentActivityDTO> recentActivity = new ArrayList<>();

    return AdminDashboardDTO.builder()
            .totalUsers(totalUsers)
            .totalAdmins(totalAdmins)
            .totalLibrarians(totalLibrarians)
            .totalStudents(totalStudents)
            .totalBooks(totalBooks)
            .booksAvailable(booksAvailable)
            .booksIssued(booksIssued)
            .booksOverdue(booksOverdue)
            .totalFinesCollected(totalFinesCollected)
            .totalFinesUnpaid(totalFinesUnpaid)
            .lostBooksPending(lostBooksPending)
            .lostBooksPaid(lostBooksPaid)
            .lostBooksWaived(lostBooksWaived)
            .pendingRenewals(pendingRenewals)
            .pendingReservations(pendingReservations)
            .notificationsSent(notificationsSent)
            .topBooks(topBooks)
            .topBorrowers(topBorrowers)
            .recentActivity(recentActivity)
            .build();
}
}