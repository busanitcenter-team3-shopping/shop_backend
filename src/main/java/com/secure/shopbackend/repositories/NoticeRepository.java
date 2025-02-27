package com.secure.shopbackend.repositories;

import com.secure.shopbackend.dtos.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    Notice save(Notice notice);

    // id값으로 비교
    Optional<Notice> findById(Long id);

    // id가 참인지 거짓인지 비교
    boolean existsById(Long id);

    // 삭제
    void deleteById(Long id);

    // 전체 출력
    List<Notice> findAll();
}
