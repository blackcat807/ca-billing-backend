package com.thejas.ca_billing_system.controller;

import com.thejas.ca_billing_system.dto.PracticeProfileDto;
import com.thejas.ca_billing_system.service.PracticeProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/practice-profile")
public class PracticeProfileController {

    private final PracticeProfileService practiceProfileService;

    public PracticeProfileController(PracticeProfileService practiceProfileService) {
        this.practiceProfileService = practiceProfileService;
    }

    @GetMapping
    public ResponseEntity<PracticeProfileDto> getMyProfile() {
        PracticeProfileDto dto = practiceProfileService.getMyProfile();
        if (dto == null) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(dto);
    }

    @PutMapping
    public ResponseEntity<PracticeProfileDto> saveMyProfile(@RequestBody PracticeProfileDto dto) {
        return ResponseEntity.ok(practiceProfileService.saveMyProfile(dto));
    }
}