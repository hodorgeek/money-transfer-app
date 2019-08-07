package com.hodorgeek.mt.app;

import com.google.inject.Inject;
import com.google.inject.persist.PersistService;
import com.hodorgeek.mt.api.MoneyTransferRestService;

public class AppInitializer {

    @Inject
    public AppInitializer(PersistService persistService, MoneyTransferRestService moneyTransferRestService) {
        persistService.start();
        moneyTransferRestService.handleRequests();
    }
}
