package com.hodorgeek.mt;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.hodorgeek.mt.app.AppInitializer;
import com.hodorgeek.mt.app.AppModule;
import com.hodorgeek.mt.app.DataLoader;

public class MoneyTransferApplication {

    private static final String PERSISTENCE_UNIT_NAME = "money-transfer";

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new AppModule(), new JpaPersistModule(PERSISTENCE_UNIT_NAME));
        injector.getInstance(AppInitializer.class);

        if (dataLoaderEnabled(args)) {
            injector.getInstance(DataLoader.class);
        }
    }

    private static boolean dataLoaderEnabled(String[] args) {
        return args.length > 0 && Boolean.valueOf(args[0]);
    }
}
