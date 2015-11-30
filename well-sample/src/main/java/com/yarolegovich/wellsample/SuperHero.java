package com.yarolegovich.wellsample;

import com.yarolegovich.wellsql.core.Identifiable;
import com.yarolegovich.wellsql.core.annotation.Check;
import com.yarolegovich.wellsql.core.annotation.Column;
import com.yarolegovich.wellsql.core.annotation.PrimaryKey;
import com.yarolegovich.wellsql.core.annotation.RawConstraints;
import com.yarolegovich.wellsql.core.annotation.Table;
import com.yarolegovich.wellsql.core.annotation.Unique;

/**
 * Created by yarolegovich on 27.11.2015.
 */

@Table
@RawConstraints({"UNIQUE (NAME, fought)"})
public class SuperHero implements Identifiable {

    @Column
    @PrimaryKey
    private int mId;

    @Column @Unique
    private String mName;

    @Column(name = "fought")
    @Check("fought >= 0")
    private int mFoughtVillains;

    public SuperHero() { }

    public SuperHero(String name, int foughtVillains) {
        mName = name;
        mFoughtVillains = foughtVillains;
    }

    @Override
    public int getId() {
        return mId;
    }

    @Override
    public void setId(int id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public int getFoughtVillains() {
        return mFoughtVillains;
    }

    public void setFoughtVillains(int foughtVillains) {
        mFoughtVillains = foughtVillains;
    }
}
