package com.yarolegovich.wellsample;

import com.yarolegovich.wellsql.core.Identifiable;
import com.yarolegovich.wellsql.core.annotation.Column;
import com.yarolegovich.wellsql.core.annotation.PrimaryKey;
import com.yarolegovich.wellsql.core.annotation.Table;
import com.yarolegovich.wellsql.core.annotation.Unique;

import java.util.Date;

/**
 * Created by yarolegovich on 29.11.2015.
 */
@Table
public class Villain implements Identifiable {

    @Column
    @PrimaryKey(autoincrement = false)
    private int id;
    @Column @Unique
    private String name;
    @Column
    private int evilDeeds;
    @Column
    private Date birthDay;

    public Villain() { }

    public Villain(int id, String name, int evilDeeds) {
        this.id = id;
        this.name = name;
        this.evilDeeds = evilDeeds;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getEvilDeeds() {
        return evilDeeds;
    }

    public void setEvilDeeds(int evilDeeds) {
        this.evilDeeds = evilDeeds;
    }

    public Date getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(Date birthDay) {
        this.birthDay = birthDay;
    }
}
