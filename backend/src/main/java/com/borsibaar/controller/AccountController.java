package com.borsibaar.controller;

import com.borsibaar.annotation.CurrentUser;
import com.borsibaar.dto.MeResponseDto;
import com.borsibaar.dto.OnboardingRequestDto;
import com.borsibaar.entity.User;
import com.borsibaar.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @GetMapping
    public ResponseEntity<MeResponseDto> me(@CurrentUser User user) {
        return ResponseEntity.ok(accountService.getCurrentUserSummary(user));
    }

    @PostMapping("/onboarding")
    @Transactional
    public ResponseEntity<Void> finish(@Valid @RequestBody OnboardingRequestDto req, @CurrentUser User user) {
        accountService.completeOnboarding(req, user);
        return ResponseEntity.noContent().build();
    }

}
