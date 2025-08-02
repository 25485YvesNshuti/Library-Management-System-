package com.Library.repository;

import com.Library.model.Renewal;
import com.Library.model.IssuedBook;
import com.Library.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RenewalRepository extends JpaRepository<Renewal, Long> {
    List<Renewal> findByUser(User user);
    
    Page<Renewal> findByUser(User user, Pageable pageable);
    Page<Renewal> findByStatus(Renewal.Status status, Pageable pageable);
    Page<Renewal> findByIssuedBook(IssuedBook issuedBook, Pageable pageable);
}