package com.borsibaar.service;

import com.borsibaar.dto.CategoryRequestDto;
import com.borsibaar.dto.CategoryResponseDto;
import com.borsibaar.entity.Category;
import com.borsibaar.exception.BadRequestException;
import com.borsibaar.exception.DuplicateResourceException;
import com.borsibaar.exception.NotFoundException;
import com.borsibaar.mapper.CategoryMapper;
import com.borsibaar.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Transactional
    public CategoryResponseDto create(CategoryRequestDto request, Long organizationId) {
        String normalizedName = Optional.ofNullable(request.name())
                .map(String::trim)
                .filter(name -> !name.isEmpty())
                .orElseThrow(() -> new BadRequestException("Category name must not be blank"));

        if (categoryRepository.existsByOrganizationIdAndNameIgnoreCase(organizationId, normalizedName)) {
            throw new DuplicateResourceException("Category '" + normalizedName + "' already exists");
        }

        Category category = categoryMapper.toEntity(request);
        category.setOrganizationId(organizationId);
        category.setName(normalizedName);

        if (request.dynamicPricing() == null) {
            category.setDynamicPricing(true);
        }

        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    @Transactional(readOnly = true)
    public List<CategoryResponseDto> getAllByOrg(Long organizationId) {
        try {
            Iterable<Category> categories = categoryRepository.findAllByOrganizationId(organizationId);

            List<CategoryResponseDto> responseDtos = new ArrayList<>();
            for (Category category : categories) {
                responseDtos.add(categoryMapper.toResponse(category));
            }

            return responseDtos;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Transactional(readOnly = true)
    public CategoryResponseDto getByIdAndOrg(Long id, Long organizationId) {
        return categoryRepository.findByIdAndOrganizationId(id, organizationId)
                .map(categoryMapper::toResponse)
                .orElseThrow(() -> new NotFoundException("Category not found: " + id));
    }

    @Transactional
    public CategoryResponseDto deleteReturningDto(Long id, Long organizationId) {
        Category category = categoryRepository.findByIdAndOrganizationId(id, organizationId)
                .orElseThrow(() -> new NotFoundException("Category not found with id: " + id));

        CategoryResponseDto dto = categoryMapper.toResponse(category);
        categoryRepository.delete(category);
        return dto;
    }
}
