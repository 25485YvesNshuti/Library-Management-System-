package com.Library.service;

import com.Library.model.*;
import com.Library.dto.IssuedBookDTO;
import com.Library.repository.IssuedBookRepository;
import com.Library.repository.UserRepository;
import com.Library.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class IssuedBookService {

    private final IssuedBookRepository issuedBookRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN','STUDENT')")
    @Transactional
    public IssuedBookDTO issueBook(Long bookId, Long userId, LocalDateTime dueDate) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));
        if (book.getAvailableCopies() <= 0)
            throw new IllegalArgumentException("No available copies");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Enforce max 3 books per student
        long activeIssuedCount = issuedBookRepository.findByUser(user).stream()
                .filter(ib -> ib.getStatus() == IssuedBook.Status.ISSUED || ib.getStatus() == IssuedBook.Status.OVERDUE)
                .count();
        if (activeIssuedCount >= 3) {
            throw new IllegalArgumentException("Student has already borrowed the maximum number of books (3)");
        }

        // Set default borrow period to 14 days if dueDate is null
        LocalDateTime now = LocalDateTime.now();
        if (dueDate == null) {
            dueDate = now.plusDays(14);
        }

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);

        IssuedBook issued = IssuedBook.builder()
                .book(book)
                .user(user)
                .issuedAt(now)
                .dueDate(dueDate)
                .status(IssuedBook.Status.ISSUED)
                .build();

        return toDTO(issuedBookRepository.save(issued));
    }

    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    @Transactional
    public IssuedBookDTO returnBook(Long issuedBookId) {
        IssuedBook issued = issuedBookRepository.findById(issuedBookId)
                .orElseThrow(() -> new IllegalArgumentException("IssuedBook not found"));

        if (issued.getStatus() != IssuedBook.Status.ISSUED &&
            issued.getStatus() != IssuedBook.Status.OVERDUE) {
            throw new IllegalStateException("Book already returned");
        }

        issued.setReturnedAt(LocalDateTime.now());
        issued.setStatus(IssuedBook.Status.RETURNED);

        Book book = issued.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);

        return toDTO(issuedBookRepository.save(issued));
    }

    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    @Transactional
    public IssuedBookDTO markOverdue(Long issuedBookId) {
        IssuedBook issued = issuedBookRepository.findById(issuedBookId)
                .orElseThrow(() -> new IllegalArgumentException("IssuedBook not found"));
        issued.setStatus(IssuedBook.Status.OVERDUE);
        return toDTO(issuedBookRepository.save(issued));
    }

    @PreAuthorize("hasRole('STUDENT')")
    public Page<IssuedBookDTO> listMyIssuedBooksPaged(Long userId, int page, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return issuedBookRepository.findByUser(user, PageRequest.of(page, size)).map(this::toDTO);
    }

    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    public Page<IssuedBookDTO> listAllIssuedBooksPaged(int page, int size) {
        return issuedBookRepository.findAll(PageRequest.of(page, size)).map(this::toDTO);
    }

    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    public Page<IssuedBookDTO> listIssuedBooksByBookPaged(Long bookId, int page, int size) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));
        return issuedBookRepository.findByBook(book, PageRequest.of(page, size)).map(this::toDTO);
    }

    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    public Page<IssuedBookDTO> listIssuedBooksByStatusPaged(String status, int page, int size) {
        IssuedBook.Status statusEnum = IssuedBook.Status.valueOf(status);
        return issuedBookRepository.findByStatus(statusEnum, PageRequest.of(page, size)).map(this::toDTO);
    }

    private IssuedBookDTO toDTO(IssuedBook issued) {
        return IssuedBookDTO.builder()
                .id(issued.getId())
                .bookId(issued.getBook().getId())
                .bookTitle(issued.getBook().getTitle())
                .userId(issued.getUser().getId())
                .userName(issued.getUser().getName())
                .issuedAt(issued.getIssuedAt())
                .dueDate(issued.getDueDate())
                .returnedAt(issued.getReturnedAt())
                .status(issued.getStatus().name())
                .build();
    }
}
