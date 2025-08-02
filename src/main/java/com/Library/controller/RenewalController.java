package com.Library.controller;

import com.Library.dto.RenewalDTO;
import com.Library.service.RenewalService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/renewals")
@RequiredArgsConstructor
public class RenewalController {

    private final RenewalService renewalService;

    // STUDENT: Request renewal
    @PostMapping("/request")
    public ResponseEntity<RenewalDTO> requestRenewal(
            @RequestParam Long issuedBookId,
            @RequestParam Long userId,
            @RequestParam LocalDateTime newDueDate) {
        return ResponseEntity.ok(renewalService.requestRenewal(issuedBookId, userId, newDueDate));
    }

    // LIBRARIAN/ADMIN: Approve or reject renewal
    @PutMapping("/{renewalId}/status")
    public ResponseEntity<RenewalDTO> processRenewal(
            @PathVariable Long renewalId,
            @RequestParam String status) {
        return ResponseEntity.ok(renewalService.processRenewal(renewalId, status));
    }

    // STUDENT: List my renewals (paged)
    @GetMapping("/my/paged")
    public ResponseEntity<Page<RenewalDTO>> listMyRenewalsPaged(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = getUserId(authentication);
        return ResponseEntity.ok(renewalService.listMyRenewalsPaged(userId, page, size));
    }

    // LIBRARIAN/ADMIN: List all renewals (paged)
    @GetMapping("/paged")
    public ResponseEntity<Page<RenewalDTO>> listAllRenewalsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(renewalService.listAllRenewalsPaged(page, size));
    }

    // LIBRARIAN/ADMIN: List by status (paged)
    @GetMapping("/status/{status}/paged")
    public ResponseEntity<Page<RenewalDTO>> listRenewalsByStatusPaged(
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(renewalService.listRenewalsByStatusPaged(status, page, size));
    }

    private Long getUserId(Authentication authentication) {
        return Long.parseLong(authentication.getName());
    }
}