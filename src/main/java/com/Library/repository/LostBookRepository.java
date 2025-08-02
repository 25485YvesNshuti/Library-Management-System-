package com.Library.repository;

import com.Library.model.LostBook;
import com.Library.model.Book;
import com.Library.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LostBookRepository extends JpaRepository<LostBook, Long> {
    List<LostBook> findByUser(User user);
    List<LostBook> findByBook(Book book);
    Page<LostBook> findByUser(User user, Pageable pageable);
    Page<LostBook> findByBook(Book book, Pageable pageable);
    Page<LostBook> findByStatus(LostBook.Status status, Pageable pageable);

    
}