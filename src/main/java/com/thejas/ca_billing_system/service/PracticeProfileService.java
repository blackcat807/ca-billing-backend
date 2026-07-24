package com.thejas.ca_billing_system.service;

import com.thejas.ca_billing_system.dto.PracticeProfileDto;

public interface PracticeProfileService {
    PracticeProfileDto getMyProfile();
    PracticeProfileDto saveMyProfile(PracticeProfileDto dto);
}