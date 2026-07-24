package com.thejas.ca_billing_system.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PracticeProfileDto {
    private String firmName;
    private String addressLine1;
    private String addressLine2;
    private String phone;
    private String email;
    private String bankAccountName;
    private String bankAccountNumber;
    private String bankAccountType;
    private String bankName;
    private String bankIfsc;
    private String bankBranch;
    private String signatoryName;
}