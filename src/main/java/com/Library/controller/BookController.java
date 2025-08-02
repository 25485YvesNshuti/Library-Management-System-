package com.Library.controller;

import com.Library.dto.BookDTO;
import com.Library.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @PostMapping
    public ResponseEntity<BookDTO> createBook(@RequestBody BookDTO dto, Authentication authentication) {
        String role = getRole(authentication);
        return ResponseEntity.ok(bookService.createBook(dto, role));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookDTO> updateBook(@PathVariable Long id,
                                              @RequestBody BookDTO dto,
                                              Authentication authentication) {
        String role = getRole(authentication);
        return ResponseEntity.ok(bookService.updateBook(id, dto, role));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id, Authentication authentication) {
        String role = getRole(authentication);
        bookService.deleteBook(id, role);
        return ResponseEntity.ok().build();
    }

    // Display all books
    @GetMapping
    public ResponseEntity<List<BookDTO>> listBooks() {
        return ResponseEntity.ok(bookService.listAllBooks());
    }

    @GetMapping("/paged")
    public ResponseEntity<Page<BookDTO>> listBooksPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(bookService.listAllBooksPaged(page, size));
    }

    // Display only available books
    @GetMapping("/available")
    @PreAuthorize("hasAnyRole('STUDENT','ADMIN','LIBRARIAN')")
    public ResponseEntity<List<BookDTO>> listAvailableBooks() {
        return ResponseEntity.ok(bookService.listAvailableBooks());
    }

    @GetMapping("/available/paged")
    @PreAuthorize("hasAnyRole('STUDENT','ADMIN','LIBRARIAN')")
    public ResponseEntity<Page<BookDTO>> listAvailableBooksPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(bookService.listAvailableBooksPaged(page, size));
    }

    @GetMapping("/search")
    public ResponseEntity<List<BookDTO>> searchBooks(@RequestParam String keyword) {
        return ResponseEntity.ok(bookService.searchBooks(keyword));
    }

    @GetMapping("/search/paged")
    public ResponseEntity<Page<BookDTO>> searchBooksPaged(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(bookService.searchBooksPaged(keyword, page, size));
    }

    @GetMapping("/category/{categoryId}/paged")
    public ResponseEntity<Page<BookDTO>> getBooksByCategoryPaged(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(bookService.getBooksByCategoryPaged(categoryId, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDTO> getBookById(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<BookDTO>> getBooksByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(bookService.getBooksByCategory(categoryId));
    }

    private String getRole(Authentication authentication) {
        return authentication != null && authentication.getAuthorities().stream().findFirst().isPresent()
            ? authentication.getAuthorities().stream().findFirst().get().getAuthority().replace("ROLE_", "")
            : null;
    }
}