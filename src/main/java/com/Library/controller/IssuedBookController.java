package com.Library.controller;

import com.Library.dto.IssuedBookDTO;
import com.Library.dto.StudentIssueRequest;
import com.Library.service.IssuedBookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/issued-books")
@RequiredArgsConstructor
public class IssuedBookController {

    private final IssuedBookService issuedBookService;

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        if (ex.getMessage() != null && ex.getMessage().contains("Book not found")) {
            return ResponseEntity.status(404).body("Book not found");
        }
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
    // LIBRARIAN & ADMIN: Issue a book manually
    @PostMapping("/issue")
    public ResponseEntity<IssuedBookDTO> issueBook(@RequestBody StudentIssueRequest request) {
        return ResponseEntity.ok(issuedBookService.issueBook(
            request.getBookId(), request.getUserId(), request.getDueDate()
        ));
    }

    // STUDENT: Issue book for self via JWT
    @PostMapping("/my/issue")
    public ResponseEntity<IssuedBookDTO> studentIssueBook(@RequestBody StudentIssueRequest request,
                                                        Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        return ResponseEntity.ok(issuedBookService.issueBook(
            request.getBookId(), userId, request.getDueDate()
        ));
    }

    @PutMapping("/{issuedBookId}/return")
    public ResponseEntity<IssuedBookDTO> returnBook(@PathVariable Long issuedBookId) {
        return ResponseEntity.ok(issuedBookService.returnBook(issuedBookId));
    }

    @PutMapping("/{issuedBookId}/overdue")
    public ResponseEntity<IssuedBookDTO> markOverdue(@PathVariable Long issuedBookId) {
        return ResponseEntity.ok(issuedBookService.markOverdue(issuedBookId));
    }

    @GetMapping("/my/paged")
    public ResponseEntity<Page<IssuedBookDTO>> listMyIssuedBooksPaged(Authentication authentication,
                                                                    @RequestParam(defaultValue = "0") int page,
                                                                    @RequestParam(defaultValue = "10") int size) {
        Long userId = Long.parseLong(authentication.getName());
        return ResponseEntity.ok(issuedBookService.listMyIssuedBooksPaged(userId, page, size));
    }

    @GetMapping("/paged")
    public ResponseEntity<Page<IssuedBookDTO>> listAllIssuedBooksPaged(@RequestParam(defaultValue = "0") int page,
                                                                    @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(issuedBookService.listAllIssuedBooksPaged(page, size));
    }

    @GetMapping("/book/{bookId}/paged")
    public ResponseEntity<Page<IssuedBookDTO>> listIssuedBooksByBookPaged(@PathVariable Long bookId,
                                                                        @RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(issuedBookService.listIssuedBooksByBookPaged(bookId, page, size));
    }

    @GetMapping("/status/{status}/paged")
    public ResponseEntity<Page<IssuedBookDTO>> listIssuedBooksByStatusPaged(@PathVariable String status,
                                                                            @RequestParam(defaultValue = "0") int page,
                                                                            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(issuedBookService.listIssuedBooksByStatusPaged(status, page, size));
    }
}
