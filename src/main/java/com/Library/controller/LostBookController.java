package com.Library.controller;

import com.Library.dto.LostBookDTO;
import com.Library.service.LostBookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/lost-books")
@RequiredArgsConstructor
public class LostBookController {

    private final LostBookService lostBookService;

    // STUDENT/LIBRARIAN/ADMIN: Report lost book
    @PostMapping("/report")
    public ResponseEntity<LostBookDTO> reportLostBook(
            @RequestParam Long bookId,
            @RequestParam Long userId,
            @RequestParam BigDecimal fineAmount) {
        return ResponseEntity.ok(lostBookService.reportLostBook(bookId, userId, fineAmount));
    }

    // ADMIN/LIBRARIAN: Update lost book status
    @PutMapping("/{lostBookId}/status")
    public ResponseEntity<LostBookDTO> updateLostBookStatus(
            @PathVariable Long lostBookId,
            @RequestParam String status) {
        return ResponseEntity.ok(lostBookService.updateLostBookStatus(lostBookId, status));
    }

    // ADMIN: Delete lost book record
    @DeleteMapping("/{lostBookId}")
    public ResponseEntity<Void> deleteLostBook(@PathVariable Long lostBookId) {
        lostBookService.deleteLostBook(lostBookId);
        return ResponseEntity.ok().build();
    }

    // ADMIN/LIBRARIAN: List all lost books (paged)
    @GetMapping("/paged")
    public ResponseEntity<Page<LostBookDTO>> listAllLostBooksPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(lostBookService.listAllLostBooksPaged(page, size));
    }

    // ADMIN/LIBRARIAN: List lost books by status (paged)
    @GetMapping("/status/{status}/paged")
    public ResponseEntity<Page<LostBookDTO>> listLostBooksByStatusPaged(
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(lostBookService.listLostBooksByStatusPaged(status, page, size));
    }

    // STUDENT: List my lost books (paged)
    @GetMapping("/my/paged")
    public ResponseEntity<Page<LostBookDTO>> listMyLostBooksPaged(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = getUserId(authentication);
        return ResponseEntity.ok(lostBookService.listMyLostBooksPaged(userId, page, size));
    }

    private Long getUserId(Authentication authentication) {
        return Long.parseLong(authentication.getName());
    }
}