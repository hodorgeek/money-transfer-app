package com.hodorgeek.mt.dto;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class CustomerDTO {

    private UUID id;

    private String firstName;

    private String lastName;

    private List<AccountDTO> accounts = new ArrayList<>();
}
