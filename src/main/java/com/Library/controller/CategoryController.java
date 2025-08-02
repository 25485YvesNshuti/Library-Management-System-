package com.Library.controller;

import com.Library.dto.CategoryDTO;
import com.Library.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    // STAFF: add category
    @PostMapping
    public ResponseEntity<CategoryDTO> createCategory(@RequestBody CategoryDTO dto, Authentication authentication) {
        String role = getRole(authentication);
        return ResponseEntity.ok(categoryService.createCategory(dto, role));
    }

    // STAFF: update category
    @PutMapping("/{id}")
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable Long id,
                                                     @RequestBody CategoryDTO dto,
                                                     Authentication authentication) {
        String role = getRole(authentication);
        return ResponseEntity.ok(categoryService.updateCategory(id, dto, role));
    }

    // STAFF: delete category
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id, Authentication authentication) {
        String role = getRole(authentication);
        categoryService.deleteCategory(id, role);
        return ResponseEntity.ok().build();
    }

    // ALL: list, get by id
    @GetMapping
    public ResponseEntity<List<CategoryDTO>> listCategories() {
        return ResponseEntity.ok(categoryService.listAllCategories());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    private String getRole(Authentication authentication) {
        return authentication.getAuthorities().stream().findFirst().get().getAuthority().replace("ROLE_", "");
    }
}