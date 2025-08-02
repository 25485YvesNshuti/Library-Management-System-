package com.Library.repository;

import com.Library.model.IssuedBook;
import com.Library.model.User;
import com.Library.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface IssuedBookRepository extends JpaRepository<IssuedBook, Long> {
    List<IssuedBook> findByUser(User user);
    List<IssuedBook> findByBook(Book book);

    long countByStatus(IssuedBook.Status status);

    Page<IssuedBook> findByUser(User user, Pageable pageable);
    Page<IssuedBook> findByBook(Book book, Pageable pageable);
    Page<IssuedBook> findByStatus(IssuedBook.Status status, Pageable pageable);

    List<IssuedBook> findByStatus(IssuedBook.Status status);

    long countByBookAndStatus(Book book, IssuedBook.Status status);

    @Query("SELECT u.id, u.name, COUNT(ib.id) as borrowCount FROM IssuedBook ib JOIN ib.user u WHERE ib.issuedAt >= :since GROUP BY u.id, u.name ORDER BY borrowCount DESC")
List<Object[]> findTopBorrowers(LocalDateTime since);

    @Query("SELECT b.id, b.title, COUNT(ib.id) as borrowCount FROM IssuedBook ib JOIN ib.book b WHERE ib.issuedAt >= :since GROUP BY b.id, b.title ORDER BY borrowCount DESC")
List<Object[]> findTopBorrowedBooks(LocalDateTime since);
}
