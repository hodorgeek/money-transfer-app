package com.hodorgeek.mt.app;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.AbstractModule;
import com.hodorgeek.mt.api.MoneyTransferRestService;
import com.hodorgeek.mt.api.MoneyTransferRestServiceImpl;
import com.hodorgeek.mt.dao.AccountDao;
import com.hodorgeek.mt.dao.CustomerDao;
import com.hodorgeek.mt.dao.impl.AccountDaoImpl;
import com.hodorgeek.mt.dao.impl.CustomerDaoImpl;
import com.hodorgeek.mt.service.AccountService;
import com.hodorgeek.mt.service.CustomerService;
import com.hodorgeek.mt.service.TransferService;
import com.hodorgeek.mt.service.impl.AccountServiceImpl;
import com.hodorgeek.mt.service.impl.CustomerServiceImpl;
import com.hodorgeek.mt.service.impl.InternalTransferService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AppModule extends AbstractModule {

    private static final String UNDERSCORE = "_";

    @Override
    protected void configure() {
        bind(Gson.class).toInstance(new GsonBuilder().setExclusionStrategies(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes f) {
                return f.getName().startsWith(UNDERSCORE);
            }

            @Override
            public boolean shouldSkipClass(Class<?> clazz) {
                return false;
            }
        }).create());

        bind(AccountDao.class).to(AccountDaoImpl.class);
        bind(CustomerDao.class).to(CustomerDaoImpl.class);
        bind(CustomerService.class).to(CustomerServiceImpl.class);
        bind(AccountService.class).to(AccountServiceImpl.class);
        bind(TransferService.class).to(InternalTransferService.class);
        bind(MoneyTransferRestService.class).to(MoneyTransferRestServiceImpl.class);
    }
}
