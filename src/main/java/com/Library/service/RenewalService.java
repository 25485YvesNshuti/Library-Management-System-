package com.Library.service;

import com.Library.dto.RenewalDTO;
import com.Library.model.*;
import com.Library.repository.IssuedBookRepository;
import com.Library.repository.RenewalRepository;
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
public class RenewalService {

    private final RenewalRepository renewalRepository;
    private final IssuedBookRepository issuedBookRepository;
    private final UserRepository userRepository;

    // Student: Request renewal
    @PreAuthorize("hasRole('STUDENT')")
    @Transactional
    public RenewalDTO requestRenewal(Long issuedBookId, Long userId, LocalDateTime newDueDate) {
        IssuedBook issuedBook = issuedBookRepository.findById(issuedBookId)
                .orElseThrow(() -> new IllegalArgumentException("IssuedBook not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Renewal renewal = Renewal.builder()
                .issuedBook(issuedBook)
                .user(user)
                .requestedAt(LocalDateTime.now())
                .status(Renewal.Status.PENDING)
                .newDueDate(newDueDate)
                .build();
        return toDTO(renewalRepository.save(renewal));
    }

    // Librarian/Admin: Approve or reject renewal
    @PreAuthorize("hasAnyRole('LIBRARIAN','ADMIN')")
    @Transactional
    public RenewalDTO processRenewal(Long renewalId, String status) {
        Renewal renewal = renewalRepository.findById(renewalId)
                .orElseThrow(() -> new IllegalArgumentException("Renewal not found"));
        renewal.setStatus(Renewal.Status.valueOf(status));
        if (Renewal.Status.APPROVED.name().equals(status)) {
            renewal.setApprovedAt(LocalDateTime.now());
            // Update issuedBook due date
            IssuedBook issuedBook = renewal.getIssuedBook();
            issuedBook.setDueDate(renewal.getNewDueDate());
            issuedBookRepository.save(issuedBook);
        }
        return toDTO(renewalRepository.save(renewal));
    }

    @PreAuthorize("hasRole('STUDENT')")
    public Page<RenewalDTO> listMyRenewalsPaged(Long userId, int page, int size) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        return renewalRepository.findByUser(user, PageRequest.of(page, size)).map(this::toDTO);
    }

    @PreAuthorize("hasAnyRole('LIBRARIAN','ADMIN')")
    public Page<RenewalDTO> listAllRenewalsPaged(int page, int size) {
        return renewalRepository.findAll(PageRequest.of(page, size)).map(this::toDTO);
    }

    @PreAuthorize("hasAnyRole('LIBRARIAN','ADMIN')")
    public Page<RenewalDTO> listRenewalsByStatusPaged(String status, int page, int size) {
        return renewalRepository.findByStatus(Renewal.Status.valueOf(status), PageRequest.of(page, size)).map(this::toDTO);
    }

    private RenewalDTO toDTO(Renewal renewal) {
        return RenewalDTO.builder()
                .id(renewal.getId())
                .issuedBookId(renewal.getIssuedBook().getId())
                .userId(renewal.getUser().getId())
                .userName(renewal.getUser().getName())
                .requestedAt(renewal.getRequestedAt())
                .approvedAt(renewal.getApprovedAt())
                .status(renewal.getStatus().name())
                .newDueDate(renewal.getNewDueDate())
                .build();
    }
}