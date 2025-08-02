package com.Library.repository;

import com.Library.model.Fine;
import com.Library.model.User;
import com.Library.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FineRepository extends JpaRepository<Fine, Long> {
    List<Fine> findByUser(User user);
    List<Fine> findByBook(Book book);
    Page<Fine> findByUser(User user, Pageable pageable);
    Page<Fine> findByBook(Book book, Pageable pageable);

    Page<Fine> findByPaid(Boolean paid, Pageable pageable);

    List<Fine> findByPaid(Boolean paid);
}