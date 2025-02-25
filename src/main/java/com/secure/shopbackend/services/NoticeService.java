package com.secure.shopbackend.services;

import com.secure.shopbackend.dtos.Notice;
import com.secure.shopbackend.repositories.NoticeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NoticeService {

    @Autowired
    private NoticeRepository noticeRepository;

    // 공지사항 생성
    public Notice createNotice(Notice notice) {
        Notice createdNotice = new Notice();
        createdNotice.setTitle(notice.getTitle());
        createdNotice.setContent(notice.getContent());
        createdNotice.setCreated_at(LocalDateTime.now());
        noticeRepository.save(createdNotice);
        return createdNotice;
    }

    // 전체 공지사항 조회
    public List<Notice> getAllNotices() {
        return noticeRepository.findAll();
    }


    // 공지사항 수정
    public Notice updateNotice(Long noticeId, Notice notice) {
        Notice existingNotice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new RuntimeException("공지사항을 찾을 수 없습니다."));

        existingNotice.setTitle(notice.getTitle());
        existingNotice.setContent(notice.getContent());

        existingNotice.setUpdated_at(LocalDateTime.now());
        return noticeRepository.save(existingNotice);
    }

    //삭제
    public void deleteNotice(Long id) {
        if (!noticeRepository.existsById(id)) {

            throw new IllegalArgumentException("공지를 찾을 수 없습니다.");
        }
        noticeRepository.deleteById(id);
    }


}
