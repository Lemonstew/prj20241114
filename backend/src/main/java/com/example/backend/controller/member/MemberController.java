package com.example.backend.controller.member;

import com.example.backend.dto.member.Member;
import com.example.backend.dto.member.MemberEdit;
import com.example.backend.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {

    final MemberService service;

    @PutMapping("edit")
    public void edit(@RequestBody MemberEdit member) {
        service.update(member);

    }

    @DeleteMapping("remove")
    public ResponseEntity<Map<String, Object>> remove(@RequestBody Member member) {
        if (service.remove(member)) {
            return ResponseEntity.ok(Map.of("message", Map.of("type", "success",
                    "text", "회원정보를 삭제하였습니다.")));
        } else {
            return ResponseEntity.badRequest().body(Map.of("message", Map.of("type", "warning",
                    "text", "정확한 정보를 입력해주세요.")));
        }
    }

    @GetMapping("{id}")
    public Member getMember(@PathVariable String id) {
        return service.get(id);
    }

    @GetMapping("list")
    public List<Member> list() {
        return service.list();
    }

    @GetMapping("check")
    public ResponseEntity<Map<String, Object>> checkId(@RequestParam String id) {
        if (service.checkId(id)) {
            return ResponseEntity.ok().body(Map.of("message", Map.of("type", "warning", "text", "used ID"),
                    "available", false)
            );
        } else {
            // 없으면
            return ResponseEntity.ok().body(Map.of("message", Map.of("type", "info", "text", "Can use"),
                    "available", true)
            );
        }
    }

    @PostMapping("signup")
    public ResponseEntity<Map<String, Object>> signup(@RequestBody Member member) {
        try {
            if (service.add(member)) {
                return ResponseEntity.ok().body(Map.of("message", Map.of("type", "success",
                        "text", "회원 가입이 완료되었습니다.")));
            } else {
                return ResponseEntity.internalServerError().body(Map.of("message",
                        Map.of("type", "error",
                                "text", "회원 가입 중 문제가 발생하였습니다.")));
            }
        } catch (DuplicateKeyException e) {
            return ResponseEntity.internalServerError().body(Map.of("message",
                    Map.of("type", "error", "text", "이미 존재하는 아이디입니다.")));
        }
    }
}
