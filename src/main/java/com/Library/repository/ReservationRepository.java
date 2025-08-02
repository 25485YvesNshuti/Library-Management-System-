package com.Library.repository;

import com.Library.model.Reservation;
import com.Library.model.Book;
import com.Library.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUser(User user);
    List<Reservation> findByBook(Book book);
    Page<Reservation> findByUser(User user, Pageable pageable);
    Page<Reservation> findByBook(Book book, Pageable pageable);
    Page<Reservation> findByStatus(Reservation.Status status, Pageable pageable);
}