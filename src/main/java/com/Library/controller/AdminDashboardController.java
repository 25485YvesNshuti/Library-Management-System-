package com.Library.controller;

import com.Library.dto.AdminDashboardDTO;
import com.Library.service.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final AdminDashboardService dashboardService;

    // Health check for debugging (optional, REMOVE in production)
    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("Admin Dashboard Controller is running!");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<AdminDashboardDTO> getDashboard() {
        AdminDashboardDTO dto = dashboardService.getDashboard();
        if (dto == null) {
            // Defensive: return 204 No Content (never expected, but avoids 500)
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(dto);
    }
}