package com.thejas.ca_billing_system.service.impl;

import com.thejas.ca_billing_system.dto.PracticeProfileDto;
import com.thejas.ca_billing_system.entity.PracticeProfile;
import com.thejas.ca_billing_system.entity.User;
import com.thejas.ca_billing_system.repository.PracticeProfileRepository;
import com.thejas.ca_billing_system.security.SecurityHelper;
import com.thejas.ca_billing_system.service.PracticeProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PracticeProfileServiceImpl implements PracticeProfileService {

    private final PracticeProfileRepository practiceProfileRepository;
    private final SecurityHelper securityHelper;

    @Override
    public PracticeProfileDto getMyProfile() {
        User user = securityHelper.currentUser();
        return practiceProfileRepository.findByUserId(user.getId())
                .map(this::toDto)
                .orElse(null);
    }

    @Override
    public PracticeProfileDto saveMyProfile(PracticeProfileDto dto) {
        User user = securityHelper.currentUser();

        PracticeProfile profile = practiceProfileRepository.findByUserId(user.getId())
                .orElse(PracticeProfile.builder().user(user).build());

        profile.setFirmName(dto.getFirmName());
        profile.setAddressLine1(dto.getAddressLine1());
        profile.setAddressLine2(dto.getAddressLine2());
        profile.setPhone(dto.getPhone());
        profile.setEmail(dto.getEmail());
        profile.setBankAccountName(dto.getBankAccountName());
        profile.setBankAccountNumber(dto.getBankAccountNumber());
        profile.setBankAccountType(dto.getBankAccountType());
        profile.setBankName(dto.getBankName());
        profile.setBankIfsc(dto.getBankIfsc());
        profile.setBankBranch(dto.getBankBranch());
        profile.setSignatoryName(dto.getSignatoryName());

        PracticeProfile saved = practiceProfileRepository.save(profile);
        return toDto(saved);
    }

    private PracticeProfileDto toDto(PracticeProfile p) {
        return PracticeProfileDto.builder()
                .firmName(p.getFirmName())
                .addressLine1(p.getAddressLine1())
                .addressLine2(p.getAddressLine2())
                .phone(p.getPhone())
                .email(p.getEmail())
                .bankAccountName(p.getBankAccountName())
                .bankAccountNumber(p.getBankAccountNumber())
                .bankAccountType(p.getBankAccountType())
                .bankName(p.getBankName())
                .bankIfsc(p.getBankIfsc())
                .bankBranch(p.getBankBranch())
                .signatoryName(p.getSignatoryName())
                .build();
    }
}