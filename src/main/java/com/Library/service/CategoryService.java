package com.Library.service;

import com.Library.model.Category;
import com.Library.repository.CategoryRepository;
import com.Library.dto.CategoryDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    // Only ADMIN/LIBRARIAN
    @Transactional
    public CategoryDTO createCategory(CategoryDTO dto, String userRole) {
        if (!isStaff(userRole)) throw new AccessDeniedException("Only staff can add category");
        if (categoryRepository.existsByNameIgnoreCase(dto.getName()))
            throw new IllegalArgumentException("Category already exists");
        Category category = Category.builder().name(dto.getName()).build();
        category = categoryRepository.save(category);
        return toDTO(category);
    }

    // Only ADMIN/LIBRARIAN
    @Transactional
    public CategoryDTO updateCategory(Long id, CategoryDTO dto, String userRole) {
        if (!isStaff(userRole)) throw new AccessDeniedException("Only staff can update category");
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        category.setName(dto.getName());
        return toDTO(categoryRepository.save(category));
    }

    // Only ADMIN/LIBRARIAN
    @Transactional
    public void deleteCategory(Long id, String userRole) {
        if (!isStaff(userRole)) throw new AccessDeniedException("Only staff can delete category");
        if (!categoryRepository.existsById(id)) throw new IllegalArgumentException("Category not found");
        categoryRepository.deleteById(id);
    }

    // ALL: List, get by id
    public List<CategoryDTO> listAllCategories() {
        return categoryRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public CategoryDTO getCategoryById(Long id) {
        return categoryRepository.findById(id).map(this::toDTO)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
    }

    // ---------- Helpers ----------
    private boolean isStaff(String role) {
        return "ADMIN".equalsIgnoreCase(role) || "LIBRARIAN".equalsIgnoreCase(role);
    }

    private CategoryDTO toDTO(Category category) {
        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
}