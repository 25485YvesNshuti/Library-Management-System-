package com.Library.controller;

import com.Library.dto.FineDTO;
import com.Library.service.FineService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fines")
@RequiredArgsConstructor
public class FineController {

    private final FineService fineService;

    // ADMIN & LIBRARIAN: Create fine
    @PostMapping
    public ResponseEntity<FineDTO> createFine(@RequestBody FineDTO fineDTO) {
        return ResponseEntity.ok(fineService.createFine(fineDTO));
    }

    // ADMIN & LIBRARIAN: Mark as paid
    @PutMapping("/{fineId}/mark-paid")
    public ResponseEntity<FineDTO> markFinePaid(@PathVariable Long fineId) {
        return ResponseEntity.ok(fineService.markFinePaid(fineId));
    }

    // ADMIN: Delete/waive fine
    @DeleteMapping("/{fineId}")
    public ResponseEntity<Void> deleteFine(@PathVariable Long fineId) {
        fineService.deleteFine(fineId);
        return ResponseEntity.ok().build();
    }

    // ADMIN & LIBRARIAN: List all fines (paged)
    @GetMapping("/paged")
    public ResponseEntity<Page<FineDTO>> listAllFinesPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(fineService.listAllFinesPaged(page, size));
    }

    // ADMIN & LIBRARIAN: List unpaid fines (paged)
    @GetMapping("/unpaid/paged")
    public ResponseEntity<Page<FineDTO>> listUnpaidFinesPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(fineService.listUnpaidFinesPaged(page, size));
    }

    // STUDENT: List own fines (paged)
    @GetMapping("/my/paged")
    public ResponseEntity<Page<FineDTO>> listMyFinesPaged(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = getUserId(authentication);
        return ResponseEntity.ok(fineService.listMyFinesPaged(userId, page, size));
    }

    // ADMIN & LIBRARIAN: List by user or book (paged)
    @GetMapping("/user/{userId}/paged")
    public ResponseEntity<Page<FineDTO>> listFinesByUserPaged(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(fineService.listFinesByUser(userId, page, size));
    }

    @GetMapping("/book/{bookId}/paged")
    public ResponseEntity<Page<FineDTO>> listFinesByBookPaged(
            @PathVariable Long bookId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(fineService.listFinesByBook(bookId, page, size));
    }

    // Helper to extract userId from Authentication
    private Long getUserId(Authentication authentication) {
        // Replace with your method for extracting userId from JWT or principal
        return Long.parseLong(authentication.getName());
    }
}