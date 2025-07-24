package com.picpal.framework.monitoring.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;

@Slf4j
@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model) {
        log.info("홈 페이지 요청");
        
        model.addAttribute("currentTime", LocalDateTime.now());
        model.addAttribute("systemName", "모니터링 시스템");
        model.addAttribute("version", "1.0.0");
        
        return "index";
    }
}