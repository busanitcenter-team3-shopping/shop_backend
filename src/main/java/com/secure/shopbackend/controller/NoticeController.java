package com.secure.shopbackend.controller;

import com.secure.shopbackend.dtos.Notice;
import com.secure.shopbackend.services.NoticeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/notice")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;


    // 공지사항 전체 조회
    @GetMapping("/noticelist")
    public ResponseEntity<List<Notice>> getAllNotices() {
        List<Notice> notices = noticeService.getAllNotices();
        return ResponseEntity.ok(notices);
    }

}
