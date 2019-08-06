package com.hodorgeek.mt.dto;

import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@Builder
public class TransferRequestPayload {

    private Long fromAccount;

    private Long toAccount;

    private float amount;
}
