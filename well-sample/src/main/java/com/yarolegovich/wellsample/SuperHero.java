package com.yarolegovich.wellsample;

import com.yarolegovich.wellsql.core.Identifiable;
import com.yarolegovich.wellsql.core.annotation.Check;
import com.yarolegovich.wellsql.core.annotation.Column;
import com.yarolegovich.wellsql.core.annotation.PrimaryKey;
import com.yarolegovich.wellsql.core.annotation.RawConstraints;
import com.yarolegovich.wellsql.core.annotation.Table;
import com.yarolegovich.wellsql.core.annotation.Unique;

import java.util.Date;

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

    /*
     * This values are to check that mapper is generated correctly for this data types
     */
    @Column private short shortField;
    @Column private long longField;
    @Column private Long longerField;
    @Column private byte shorterField;
    @Column private float floatField;
    @Column private Float floaterField;
    @Column private double doubleField;
    @Column private Double doublerField;
    @Column private Date birthday = new Date(System.currentTimeMillis());
    @Column private boolean isTrueEvil;


    public short getShortField() {
        return shortField;
    }

    public void setShortField(short shortField) {
        this.shortField = shortField;
    }

    public long getLongField() {
        return longField;
    }

    public void setLongField(long longField) {
        this.longField = longField;
    }

    public Long getLongerField() {
        return longerField;
    }

    public void setLongerField(Long longerField) {
        this.longerField = longerField;
    }

    public byte getShorterField() {
        return shorterField;
    }

    public void setShorterField(byte shorterField) {
        this.shorterField = shorterField;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

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

    public boolean isTrueEvil() {
        return isTrueEvil;
    }

    public void setIsTrueEvil(boolean isTrueEvil) {
        this.isTrueEvil = isTrueEvil;
    }

    public float getFloatField() {
        return floatField;
    }

    public void setFloatField(float floatField) {
        this.floatField = floatField;
    }

    public Float getFloaterField() {
        return floaterField;
    }

    public void setFloaterField(Float floaterField) {
        this.floaterField = floaterField;
    }

    public double getDoubleField() {
        return doubleField;
    }

    public void setDoubleField(double doubleField) {
        this.doubleField = doubleField;
    }

    public Double getDoublerField() {
        return doublerField;
    }

    public void setDoublerField(Double doublerField) {
        this.doublerField = doublerField;
    }
}
