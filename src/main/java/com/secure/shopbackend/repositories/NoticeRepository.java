package com.secure.shopbackend.repositories;

import com.secure.shopbackend.dtos.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    Notice save(Notice notice);

    Optional<Notice> findById(Long id);

    boolean existsById(Long id);

    void deleteById(Long id);

    List<Notice> findAll();
}
