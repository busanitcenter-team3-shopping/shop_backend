package com.secure.shopbackend.controller;

import com.secure.shopbackend.dtos.Notice;
import com.secure.shopbackend.services.NoticeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/notice")
public class AdminNoticeController {

    @Autowired
    private NoticeService noticeService;

    //공지사항 생성
    @PostMapping("/create")
    public ResponseEntity<?> createNotice(@Valid @RequestBody Notice notice, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String error = bindingResult.getFieldError().getDefaultMessage();
            return ResponseEntity.badRequest().body(error);
        }
        try {
            Notice createdNotice = noticeService.createNotice(notice);
            return ResponseEntity.ok(createdNotice);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //공지사항 수정
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateNotice(@RequestBody Notice notice, @PathVariable Long id) {
        try {
            Notice updatedNotice = noticeService.updateNotice(id, notice);
            return ResponseEntity.ok(updatedNotice);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //공지사항 삭제
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteNotice(@PathVariable Long id) {
        try {
            noticeService.deleteNotice(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
