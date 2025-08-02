package com.Library.service;

import com.Library.dto.ReservationDTO;
import com.Library.model.*;
import com.Library.repository.BookRepository;
import com.Library.repository.ReservationRepository;
import com.Library.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    // Student: Make reservation
    @PreAuthorize("hasRole('STUDENT')")
    @Transactional
    public ReservationDTO reserveBook(Long bookId, Long userId, LocalDateTime expiresAt) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Reservation reservation = Reservation.builder()
                .book(book)
                .user(user)
                .reservedAt(LocalDateTime.now())
                .expiresAt(expiresAt)
                .status(Reservation.Status.WAITING)
                .build();
        return toDTO(reservationRepository.save(reservation));
    }

    // Librarian/Admin: Notify, expire, update status
    @PreAuthorize("hasAnyRole('LIBRARIAN','ADMIN')")
    @Transactional
    public ReservationDTO updateReservationStatus(Long reservationId, String status) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));
        reservation.setStatus(Reservation.Status.valueOf(status));
        if (Reservation.Status.NOTIFIED.name().equals(status)) {
            reservation.setNotifiedAt(LocalDateTime.now());
        }
        return toDTO(reservationRepository.save(reservation));
    }

    @PreAuthorize("hasRole('STUDENT')")
    public Page<ReservationDTO> listMyReservationsPaged(Long userId, int page, int size) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        return reservationRepository.findByUser(user, PageRequest.of(page, size)).map(this::toDTO);
    }

    @PreAuthorize("hasAnyRole('LIBRARIAN','ADMIN')")
    public Page<ReservationDTO> listAllReservationsPaged(int page, int size) {
        return reservationRepository.findAll(PageRequest.of(page, size)).map(this::toDTO);
    }

    @PreAuthorize("hasAnyRole('LIBRARIAN','ADMIN')")
    public Page<ReservationDTO> listReservationsByStatusPaged(String status, int page, int size) {
        return reservationRepository.findByStatus(Reservation.Status.valueOf(status), PageRequest.of(page, size)).map(this::toDTO);
    }

    private ReservationDTO toDTO(Reservation reservation) {
        return ReservationDTO.builder()
                .id(reservation.getId())
                .bookId(reservation.getBook().getId())
                .bookTitle(reservation.getBook().getTitle())
                .userId(reservation.getUser().getId())
                .userName(reservation.getUser().getName())
                .reservedAt(reservation.getReservedAt())
                .notifiedAt(reservation.getNotifiedAt())
                .expiresAt(reservation.getExpiresAt())
                .status(reservation.getStatus().name())
                .build();
    }
}