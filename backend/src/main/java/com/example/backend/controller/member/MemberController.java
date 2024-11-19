package com.example.backend.controller.member;

import com.example.backend.dto.member.Member;
import com.example.backend.dto.member.MemberEdit;
import com.example.backend.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {

    final MemberService service;

    @PostMapping("login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Member member) {
        String token = service.token(member);
        if (token != null) {
            // success login
            return ResponseEntity.ok(Map.of("token", token,
                    "message", Map.of("type", "success",
                            "text", "success login")));
        } else {
            return ResponseEntity.status(401).body(Map.of("message", "waring", "text", "로그인에 실패하였습니다,"));
        }
    }

    @PutMapping("edit")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> edit(@RequestBody MemberEdit member, Authentication authentication) {
        if (service.hasAccess(member.getId(), authentication)) {
            if (service.update(member)) {
                return ResponseEntity.ok(Map.of("message", Map.of("type", "success",
                        "text", "회원정보를 수정하였습니다.")));
            } else {
                return ResponseEntity.badRequest().body(Map.of("message", Map.of("type", "warning",
                        "text", "정확한 정보를 입력해주세요.")));
            }
        } else {
            return ResponseEntity.status(403).build();
        }

    }

    @DeleteMapping("remove")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> remove(@RequestBody Member member, Authentication authentication) {

        if (service.hasAccess(member.getId(), authentication)) {
            if (service.remove(member)) {
                return ResponseEntity.ok(Map.of("message", Map.of("type", "success",
                        "text", "회원정보를 삭제하였습니다.")));
            } else {
                return ResponseEntity.badRequest().body(Map.of("message", Map.of("type", "warning",
                        "text", "정확한 정보를 입력해주세요.")));
            }
        } else {
            return ResponseEntity.status(403).build();
        }
    }

    @GetMapping("{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Member> getMember(@PathVariable String id, Authentication authentication) {

        if (service.hasAccess(id, authentication) || service.isAdmin(authentication)) {
            return ResponseEntity.ok(service.get(id));
        } else {
            return ResponseEntity.status(403).build();
        }
    }

    @GetMapping("list")
    @PreAuthorize("hasAuthority('SCOPE_admin')")
    public List<Member> list() {
        return service.list();
    }

    @GetMapping(value = "check", params = "id")
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

    @GetMapping(value = "check", params = "email")
    public ResponseEntity<Map<String, Object>> checkEmail(@RequestParam String email) {
        if (service.checkEmail(email)) {
            return ResponseEntity.ok().body(Map.of("message", Map.of("type", "warning", "text", "used E-mail"),
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
