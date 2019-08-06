package com.hodorgeek.mt.service;


import com.hodorgeek.mt.dto.TransferRequestPayload;

public interface TransferService {

    boolean transfer(TransferRequestPayload transferRequest);
}
