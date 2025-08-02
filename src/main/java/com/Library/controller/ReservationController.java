package com.Library.controller;

import com.Library.dto.ReservationDTO;
import com.Library.service.ReservationService;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    // STUDENT: Make reservation (accept JSON body)
    @PostMapping("/my/reserve")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ReservationDTO> reserveBook(
            Authentication authentication,
            @RequestBody ReservationRequest request) {
        Long userId = getUserId(authentication);
        return ResponseEntity.ok(reservationService.reserveBook(request.getBookId(), userId, request.getExpiresAt()));
    }

    // LIBRARIAN/ADMIN: Update reservation status
    @PutMapping("/{reservationId}/status")
    public ResponseEntity<ReservationDTO> updateReservationStatus(
            @PathVariable Long reservationId,
            @RequestParam String status) {
        return ResponseEntity.ok(reservationService.updateReservationStatus(reservationId, status));
    }

    // STUDENT: List my reservations (paged)
    @GetMapping("/my/paged")
    public ResponseEntity<Page<ReservationDTO>> listMyReservationsPaged(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = getUserId(authentication);
        return ResponseEntity.ok(reservationService.listMyReservationsPaged(userId, page, size));
    }

    // LIBRARIAN/ADMIN: List all reservations (paged)
    @GetMapping("/paged")
    public ResponseEntity<Page<ReservationDTO>> listAllReservationsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(reservationService.listAllReservationsPaged(page, size));
    }

    // LIBRARIAN/ADMIN: List by status (paged)
    @GetMapping("/status/{status}/paged")
    public ResponseEntity<Page<ReservationDTO>> listReservationsByStatusPaged(
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(reservationService.listReservationsByStatusPaged(status, page, size));
    }

    private Long getUserId(Authentication authentication) {
        return Long.parseLong(authentication.getName());
    }

    // DTO for reservation request
    @Data
    public static class ReservationRequest {
        private Long bookId;
        private LocalDateTime expiresAt;
    }
}