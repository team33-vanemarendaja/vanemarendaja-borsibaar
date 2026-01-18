package com.borsibaar.controller;

import com.borsibaar.annotation.CurrentUser;
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
    public CategoryResponseDto createCategory(@RequestBody CategoryRequestDto request, @CurrentUser User user) {
        return categoryService.create(request, user.getOrganizationId());
    }

    @GetMapping
    public List<CategoryResponseDto> getAll(@RequestParam(required = false) Long organizationId, @CurrentUser User user) {
        // If organizationId is provided, use it (for public access)
        // Otherwise, get from authenticated user
        Long orgId;
        if (organizationId != null) {
            orgId = organizationId;
        } else {
            orgId = user.getOrganizationId();
        }
        return categoryService.getAllByOrg(orgId);
    }

    @GetMapping("/{id}")
    //@IsOnboardedAdmin
    // TODO: If user has no organization can he see cateogry?
    public CategoryResponseDto getById(@PathVariable Long id, @CurrentUser User user) {
        return categoryService.getByIdAndOrg(id, user.getOrganizationId());
    }

    @DeleteMapping({ "/{id}" })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    //@IsOnboardedAdmin
    // TODO: Can user without admin role delete a resource
    public void delete(@PathVariable Long id, @CurrentUser User user) {
        categoryService.deleteReturningDto(id, user.getOrganizationId());
    }
}
