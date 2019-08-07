package com.hodorgeek.mt.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class AccountDTO {
    private Long id;

    private BigDecimal balance = BigDecimal.ZERO;
}
