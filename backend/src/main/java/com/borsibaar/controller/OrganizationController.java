package com.borsibaar.controller;

import com.borsibaar.annotation.IsAuthenticated;
import com.borsibaar.dto.OrganizationRequestDto;
import com.borsibaar.dto.OrganizationResponseDto;
import com.borsibaar.service.OrganizationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organizations")
@RequiredArgsConstructor
public class OrganizationController {

    private final OrganizationService organizationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @IsAuthenticated
    public OrganizationResponseDto create(@RequestBody @Valid OrganizationRequestDto request) {
        return organizationService.create(request);
    }

    @GetMapping("/{id}")
    @IsAuthenticated
    public OrganizationResponseDto get(@PathVariable Long id) {
        return organizationService.getById(id);
    }

    @GetMapping
    @IsAuthenticated
    public List<OrganizationResponseDto> getAll() {
        return organizationService.getAll();
    }

    @PutMapping("/{id}")
    @IsAuthenticated
    public OrganizationResponseDto update(@PathVariable Long id, @RequestBody @Valid OrganizationRequestDto request) {
        System.out.println(request);
        return organizationService.update(id, request);
    }

}
