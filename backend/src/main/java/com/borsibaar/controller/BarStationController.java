package com.borsibaar.controller;

import com.borsibaar.annotation.CurrentUser;
import com.borsibaar.annotation.IsOnboardedAdmin;
import com.borsibaar.dto.BarStationRequestDto;
import com.borsibaar.dto.BarStationResponseDto;
import com.borsibaar.entity.User;
import com.borsibaar.service.BarStationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bar-stations")
@RequiredArgsConstructor
public class BarStationController {

    private final BarStationService barStationService;

    @GetMapping
    @IsOnboardedAdmin
    public ResponseEntity<List<BarStationResponseDto>> getAllStations(@CurrentUser User user) {
        return ResponseEntity.ok(barStationService.getAllStations(user.getOrganizationId()));
    }

    @GetMapping("/user")
    @PreAuthorize("principal.organizationId != null")
    public ResponseEntity<List<BarStationResponseDto>> getUserStations(@CurrentUser User user) {
        return ResponseEntity.ok(barStationService.getUserStations(user.getId(), user.getOrganizationId()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("principal.organizationId != null")
    public ResponseEntity<BarStationResponseDto> getStationById(@PathVariable Long id, @CurrentUser User user) {
        return ResponseEntity.ok(barStationService.getStationById(user.getOrganizationId(), id));
    }

    @PostMapping
    @IsOnboardedAdmin
    public ResponseEntity<BarStationResponseDto> createStation(@Valid @RequestBody BarStationRequestDto request, @CurrentUser User user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(barStationService.createStation(user.getOrganizationId(), request));
    }

    @PutMapping("/{id}")
    @IsOnboardedAdmin
    public ResponseEntity<BarStationResponseDto> updateStation(
            @PathVariable Long id,
            @Valid @RequestBody BarStationRequestDto request,
            @CurrentUser User user) {
        return ResponseEntity.ok(barStationService.updateStation(user.getOrganizationId(), id, request));
    }

    @DeleteMapping("/{id}")
    @IsOnboardedAdmin
    public ResponseEntity<Void> deleteStation(@PathVariable Long id, @CurrentUser User user) {
        barStationService.deleteStation(user.getOrganizationId(), id);
        return ResponseEntity.noContent().build();
    }
}

