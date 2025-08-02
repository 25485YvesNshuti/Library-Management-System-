package com.Library.service;

import com.Library.dto.LostBookDTO;
import com.Library.model.Book;
import com.Library.model.LostBook;
import com.Library.model.User;
import com.Library.repository.BookRepository;
import com.Library.repository.LostBookRepository;
import com.Library.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LostBookService {

    private final LostBookRepository lostBookRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final com.Library.repository.FineRepository fineRepository;

    @PreAuthorize("hasAnyRole('STUDENT','LIBRARIAN','ADMIN')")
    @Transactional
    public LostBookDTO reportLostBook(Long bookId, Long userId, BigDecimal fineAmount) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        LostBook lostBook = LostBook.builder()
                .book(book)
                .user(user)
                .reportedAt(LocalDateTime.now())
                .fineAmount(fineAmount)
                .status(LostBook.Status.PENDING)
                .build();
        lostBook = lostBookRepository.save(lostBook);

        // Auto-create a fine for the lost book if not already present
        boolean fineExists = fineRepository.findByUser(user).stream()
                .anyMatch(f -> f.getBook().getId().equals(bookId) &&
                        "Lost book".equalsIgnoreCase(f.getReason()) && !Boolean.TRUE.equals(f.getPaid()));
        if (!fineExists) {
            com.Library.model.Fine fine = com.Library.model.Fine.builder()
                    .user(user)
                    .book(book)
                    .amount(fineAmount)
                    .reason("Lost book")
                    .paid(false)
                    .dateApplied(LocalDateTime.now())
                    .build();
            fineRepository.save(fine);
        }
        return toDTO(lostBook);
    }

    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    @Transactional
    public LostBookDTO updateLostBookStatus(Long lostBookId, String status) {
        LostBook lostBook = lostBookRepository.findById(lostBookId)
                .orElseThrow(() -> new IllegalArgumentException("Lost book record not found"));
        lostBook.setStatus(LostBook.Status.valueOf(status));
        // If status is PAID, mark the related fine as paid
        if (LostBook.Status.valueOf(status) == LostBook.Status.PAID) {
            fineRepository.findByUser(lostBook.getUser()).stream()
                .filter(f -> f.getBook().getId().equals(lostBook.getBook().getId()) &&
                             "Lost book".equalsIgnoreCase(f.getReason()) &&
                             !Boolean.TRUE.equals(f.getPaid()))
                .forEach(f -> {
                    f.setPaid(true);
                    fineRepository.save(f);
                });
        }
        return toDTO(lostBookRepository.save(lostBook));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void deleteLostBook(Long lostBookId) {
        lostBookRepository.deleteById(lostBookId);
    }

    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    public Page<LostBookDTO> listAllLostBooksPaged(int page, int size) {
        return lostBookRepository.findAll(PageRequest.of(page, size)).map(this::toDTO);
    }

    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    public Page<LostBookDTO> listLostBooksByStatusPaged(String status, int page, int size) {
        return lostBookRepository.findByStatus(LostBook.Status.valueOf(status), PageRequest.of(page, size)).map(this::toDTO);
    }

    @PreAuthorize("hasRole('STUDENT')")
    public Page<LostBookDTO> listMyLostBooksPaged(Long userId, int page, int size) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        return lostBookRepository.findByUser(user, PageRequest.of(page, size)).map(this::toDTO);
    }

    private LostBookDTO toDTO(LostBook lostBook) {
        return LostBookDTO.builder()
                .id(lostBook.getId())
                .bookId(lostBook.getBook().getId())
                .bookTitle(lostBook.getBook().getTitle())
                .userId(lostBook.getUser().getId())
                .userName(lostBook.getUser().getName())
                .reportedAt(lostBook.getReportedAt())
                .fineAmount(lostBook.getFineAmount())
                .status(lostBook.getStatus().name())
                .build();
    }
}