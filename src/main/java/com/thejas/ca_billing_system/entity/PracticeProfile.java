package com.thejas.ca_billing_system.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "practice_profiles")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PracticeProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private String firmName;

    @Column(columnDefinition = "TEXT")
    private String addressLine1;

    @Column(columnDefinition = "TEXT")
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