package com.borsibaar.service;

import com.borsibaar.dto.MeResponseDto;
import com.borsibaar.dto.OnboardingRequestDto;
import com.borsibaar.entity.Role;
import com.borsibaar.entity.User;
import com.borsibaar.repository.RoleRepository;
import com.borsibaar.repository.UserRepository;
import com.borsibaar.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Transactional(readOnly = true)
    public MeResponseDto getCurrentUserSummary(User user) {
        return new MeResponseDto(
                user.getEmail(),
                user.getName(),
                user.getRole() != null ? user.getRole().getName() : null,
                user.getOrganizationId(),
                user.getOrganizationId() == null
        );
    }

    @Transactional
    public void completeOnboarding(OnboardingRequestDto req, User user) {

        // Allow users without organization (that's the point of onboarding)
        if (user.getOrganizationId() != null) {
            return;
        }

        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new IllegalStateException("System configuration error: ADMIN role missing"));

        // First user in an Org becomes Admin
        boolean orgHasAdmin = userRepository.existsByOrganizationIdAndRole(req.organizationId(), adminRole);

        if (!orgHasAdmin) {
            user.setRole(adminRole);
        }

        user.setOrganizationId(req.organizationId());
        userRepository.save(user);
    }
}