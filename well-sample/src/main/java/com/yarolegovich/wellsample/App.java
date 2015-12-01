package com.yarolegovich.wellsample;

import android.app.Application;

import com.wellsql.generated.SuperHeroMapper;
import com.wellsql.generated.SuperHeroTable;
import com.yarolegovich.wellsql.WellSql;

/**
 * Created by yarolegovich on 26.11.2015.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        WellSql.init(new WellConfig(this));
    }
}
