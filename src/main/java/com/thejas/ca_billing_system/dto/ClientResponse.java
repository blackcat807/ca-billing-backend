package com.thejas.ca_billing_system.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientResponse {

    private Long clientId;

    private String clientName;

    private String mobileNumber;

    private String gstin;

    private String address;

    private String notes;
}