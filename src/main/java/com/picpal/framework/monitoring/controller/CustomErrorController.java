package com.picpal.framework.monitoring.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;

@Slf4j
@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        // 에러 정보 추출
        Object status = request.getAttribute("javax.servlet.error.status_code");
        Object error = request.getAttribute("javax.servlet.error.message");
        Object path = request.getAttribute("javax.servlet.error.request_uri");
        
        Integer statusCode = status != null ? (Integer) status : 500;
        String errorMessage = error != null ? error.toString() : "알 수 없는 오류가 발생했습니다.";
        String requestPath = path != null ? path.toString() : "알 수 없음";
        
        log.warn("오류 페이지 요청 - 상태코드: {}, 경로: {}, 메시지: {}", statusCode, requestPath, errorMessage);
        
        // 404 에러의 경우 특별한 메시지 설정
        if (statusCode == 404) {
            errorMessage = "요청하신 페이지를 찾을 수 없습니다.";
        }
        
        // 모델에 오류 정보 추가
        model.addAttribute("status", statusCode);
        model.addAttribute("error", getErrorName(statusCode));
        model.addAttribute("message", errorMessage);
        model.addAttribute("path", requestPath);
        model.addAttribute("timestamp", LocalDateTime.now());
        
        return "error";
    }
    
    private String getErrorName(int statusCode) {
        return switch (statusCode) {
            case 400 -> "Bad Request";
            case 401 -> "Unauthorized";
            case 403 -> "Forbidden";
            case 404 -> "Not Found";
            case 500 -> "Internal Server Error";
            case 502 -> "Bad Gateway";
            case 503 -> "Service Unavailable";
            default -> "Error";
        };
    }
}