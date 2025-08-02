package com.Library.service;

import com.Library.model.Book;
import com.Library.model.Category;
import com.Library.repository.BookRepository;
import com.Library.repository.CategoryRepository;
import com.Library.dto.BookDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public BookDTO createBook(BookDTO dto, String userRole) {
        if (!isStaff(userRole)) throw new AccessDeniedException("Only staff can add books");
        if (bookRepository.existsByTitleIgnoreCaseAndAuthorIgnoreCase(dto.getTitle(), dto.getAuthor()))
            throw new IllegalArgumentException("Book with this title/author already exists");
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid category"));
        Book book = Book.builder()
                .title(dto.getTitle())
                .author(dto.getAuthor())
                .category(category)
                .totalCopies(dto.getTotalCopies())
                .availableCopies(dto.getAvailableCopies() == null ? dto.getTotalCopies() : dto.getAvailableCopies())
                .createdAt(LocalDateTime.now())
                .build();
        book = bookRepository.save(book);
        return toDTO(book);
    }

    @Transactional
    public BookDTO updateBook(Long id, BookDTO dto, String userRole) {
        if (!isStaff(userRole)) throw new AccessDeniedException("Only staff can update books");
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));
        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());
        if (!book.getCategory().getId().equals(dto.getCategoryId())) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid category"));
            book.setCategory(category);
        }
        int diff = dto.getTotalCopies() - book.getTotalCopies();
        book.setTotalCopies(dto.getTotalCopies());
        book.setAvailableCopies(book.getAvailableCopies() + diff);
        bookRepository.save(book);
        return toDTO(book);
    }

    @Transactional
    public void deleteBook(Long id, String userRole) {
        if (!isStaff(userRole)) throw new AccessDeniedException("Only staff can delete books");
        if (!bookRepository.existsById(id)) throw new IllegalArgumentException("Book not found");
        bookRepository.deleteById(id);
    }

    // Display all books
    public List<BookDTO> listAllBooks() {
        return bookRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public Page<BookDTO> listAllBooksPaged(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return bookRepository.findAll(pageable).map(this::toDTO);
    }

    // Display only available books
    public List<BookDTO> listAvailableBooks() {
        return bookRepository.findAllAvailable()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Page<BookDTO> listAvailableBooksPaged(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return bookRepository.findAllAvailable(pageable)
                .map(this::toDTO);
    }

    public List<BookDTO> searchBooks(String keyword) {
        return bookRepository.searchByTitleOrAuthor(keyword).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public Page<BookDTO> searchBooksPaged(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return bookRepository.searchByTitleOrAuthor(keyword, pageable).map(this::toDTO);
    }

    public BookDTO getBookById(Long id) {
        return bookRepository.findById(id).map(this::toDTO)
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));
    }

    public List<BookDTO> getBooksByCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        return bookRepository.findByCategory(category).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public Page<BookDTO> getBooksByCategoryPaged(Long categoryId, int page, int size) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        Pageable pageable = PageRequest.of(page, size);
        return bookRepository.findByCategory(category, pageable).map(this::toDTO);
    }

    private boolean isStaff(String role) {
        return "ADMIN".equalsIgnoreCase(role) || "LIBRARIAN".equalsIgnoreCase(role);
    }

    private BookDTO toDTO(Book book) {
        return BookDTO.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .categoryId(book.getCategory().getId())
                .categoryName(book.getCategory().getName())
                .totalCopies(book.getTotalCopies())
                .availableCopies(book.getAvailableCopies())
                .createdAt(book.getCreatedAt())
                .build();
    }
}