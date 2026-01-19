package com.borsibaar.controller;

import com.borsibaar.annotation.CurrentUser;
import com.borsibaar.annotation.IsAuthenticated;
import com.borsibaar.annotation.IsOnboardedAdmin;
import com.borsibaar.dto.CategoryRequestDto;
import com.borsibaar.dto.CategoryResponseDto;
import com.borsibaar.entity.User;
import com.borsibaar.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @IsOnboardedAdmin
    @IsAuthenticated
    public CategoryResponseDto createCategory(@RequestBody CategoryRequestDto request, @CurrentUser User user) {
        return categoryService.create(request, user.getOrganizationId());
    }

    @GetMapping
    @IsAuthenticated
    public List<CategoryResponseDto> getAll(@RequestParam(required = false) Long organizationId, @CurrentUser User user) {
        // If organizationId is provided, use it (for public access)
        // Otherwise, get from authenticated user
        try {
            Long orgId;
            if (organizationId != null) {
                orgId = organizationId;
            } else {
                orgId = user.getOrganizationId();
            }
            return categoryService.getAllByOrg(orgId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @GetMapping("/{id}")
    @IsAuthenticated
    public CategoryResponseDto getById(@PathVariable Long id, @CurrentUser User user) {
        try {
            return categoryService.getByIdAndOrg(id, user.getOrganizationId());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @DeleteMapping({ "/{id}" })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @IsAuthenticated
    // TODO: Can user without admin role delete a resource
    public void delete(@PathVariable Long id, @CurrentUser User user) {
        categoryService.deleteReturningDto(id, user.getOrganizationId());
    }
}
