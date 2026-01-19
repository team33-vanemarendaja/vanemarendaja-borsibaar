package com.borsibaar.controller;

import com.borsibaar.annotation.CurrentUser;
import com.borsibaar.dto.UserSummaryResponseDto;
import com.borsibaar.entity.User;
import com.borsibaar.mapper.UserMapper;
import com.borsibaar.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserSummaryResponseDto>> getOrganizationUsers(@CurrentUser User currentUser) {
        // Get authenticated user from SecurityContext (set by JwtAuthenticationFilter)

        List<UserSummaryResponseDto> users = userRepository.findByOrganizationId(currentUser.getOrganizationId())
                .stream()
                .map(userMapper::toSummaryDto)
                .toList();

        return ResponseEntity.ok(users);
    }
}
