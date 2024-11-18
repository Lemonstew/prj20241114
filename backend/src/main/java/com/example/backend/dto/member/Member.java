package com.example.backend.dto.member;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Member {
    private String id;
    private String email;
    private String password;
    private String description;
    private LocalDateTime inserted;
}
