package com.deulbull.performance.domain.admin.web.controller;

import com.deulbull.performance.domain.admin.web.dto.AdminLoginRequestDto;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @PostMapping("/login")
    public String login(@RequestBody AdminLoginRequestDto adminLoginRequestDto) {

    }
}
