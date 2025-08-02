package com.Library.service;

import com.Library.model.Fine;
import com.Library.model.User;
import com.Library.model.Book;
import com.Library.dto.FineDTO;
import com.Library.repository.FineRepository;
import com.Library.repository.UserRepository;
import com.Library.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FineService {

    private final FineRepository fineRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    /*** ADMIN & LIBRARIAN: Create fine ***/
    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    @Transactional
    public FineDTO createFine(FineDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Book book = bookRepository.findById(dto.getBookId())
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));
        Fine fine = Fine.builder()
                .user(user)
                .book(book)
                .amount(dto.getAmount())
                .reason(dto.getReason())
                .paid(false)
                .dateApplied(dto.getDateApplied() == null ? java.time.LocalDateTime.now() : dto.getDateApplied())
                .build();
        return toDTO(fineRepository.save(fine));
    }

    /*** ADMIN & LIBRARIAN: Mark fine as paid ***/
    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    @Transactional
    public FineDTO markFinePaid(Long fineId) {
        Fine fine = fineRepository.findById(fineId)
                .orElseThrow(() -> new IllegalArgumentException("Fine not found"));
        fine.setPaid(true);
        return toDTO(fineRepository.save(fine));
    }

    /*** ADMIN: Delete/waive fine ***/
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void deleteFine(Long fineId) {
        if (!fineRepository.existsById(fineId))
            throw new IllegalArgumentException("Fine not found");
        fineRepository.deleteById(fineId);
    }

    /*** ADMIN & LIBRARIAN: All fines (paged) ***/
    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    public Page<FineDTO> listAllFinesPaged(int page, int size) {
        return fineRepository.findAll(PageRequest.of(page, size)).map(this::toDTO);
    }

    /*** ADMIN & LIBRARIAN: All unpaid fines (paged) ***/
    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    public Page<FineDTO> listUnpaidFinesPaged(int page, int size) {
        return fineRepository.findByPaid(false, PageRequest.of(page, size)).map(this::toDTO);
    }

    /*** STUDENT: List their own fines (paged) ***/
    @PreAuthorize("hasRole('STUDENT')")
    public Page<FineDTO> listMyFinesPaged(Long userId, int page, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return fineRepository.findByUser(user, PageRequest.of(page, size)).map(this::toDTO);
    }

    /*** ADMIN & LIBRARIAN: By user or book ***/
    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    public Page<FineDTO> listFinesByUser(Long userId, int page, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return fineRepository.findByUser(user, PageRequest.of(page, size)).map(this::toDTO);
    }

    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    public Page<FineDTO> listFinesByBook(Long bookId, int page, int size) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));
        return fineRepository.findByBook(book, PageRequest.of(page, size)).map(this::toDTO);
    }

    /*** DTO conversion ***/
    private FineDTO toDTO(Fine fine) {
        return FineDTO.builder()
                .id(fine.getId())
                .userId(fine.getUser().getId())
                .userName(fine.getUser().getName())
                .bookId(fine.getBook().getId())
                .bookTitle(fine.getBook().getTitle())
                .amount(fine.getAmount())
                .reason(fine.getReason())
                .paid(fine.getPaid())
                .dateApplied(fine.getDateApplied())
                .build();
    }
}