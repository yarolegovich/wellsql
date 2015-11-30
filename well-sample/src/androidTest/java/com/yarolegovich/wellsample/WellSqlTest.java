package com.yarolegovich.wellsample;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Looper;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
//
//import com.wellsql.generated.SuperHeroTable;
import com.yarolegovich.wellsql.SelectQuery;
import com.yarolegovich.wellsql.WellSql;
import com.yarolegovich.wellsql.mapper.InsertMapper;
import com.yarolegovich.wellsql.mapper.SelectMapper;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by yarolegovich on 27.11.2015.
 */
@RunWith(AndroidJUnit4.class)
public class WellSqlTest {

//    @BeforeClass
//    public static void setUpDb() {
//        Context context = InstrumentationRegistry.getTargetContext();
//        WellSql.init(new WellConfig(context));
//    }
//
//    @Before
//    public void clearDb() {
//        WellSql.delete(SuperHero.class).execute();
//        WellSql.delete(Villain.class).execute();
//    }
//
//    @Test
//    public void idSetAfterInsert() {
//        WellSql.autoincrementFor(SuperHero.class).reset();
//        SuperHero hero = getHeroes().get(0);
//        WellSql.insert(hero).execute();
//        assertEquals(1, hero.getId());
//    }
//
//    @Test
//    public void updateByIdWorks() {
//        SuperHero hero = getHeroes().get(0);
//        WellSql.insert(hero).execute();
//        hero.setFoughtVillains(4);
//        WellSql.update(SuperHero.class).whereId(hero.getId()).put(hero).execute();
//        hero = WellSql.select(SuperHero.class)
//                .where().equals(SuperHeroTable.NAME, hero.getName()).endWhere()
//                .getAsModel(new SelectMapper<SuperHero>() {
//                    @Override
//                    public SuperHero convert(Cursor cursor) {
//                        SuperHero hero = new SuperHero();
//                        hero.setFoughtVillains(cursor.getInt(cursor.getColumnIndex(SuperHeroTable.FOUGHT)));
//                        return hero;
//                    }
//                }).get(0);
//        assertEquals(4, hero.getFoughtVillains());
//    }
//
//    @Test
//    public void selectAllWorks() {
//        List<SuperHero> heroes = getHeroes();
//        WellSql.insert(heroes).execute();
//        List<SuperHero> stored = WellSql.select(SuperHero.class).getAsModel();
//        assertEquals(heroes.size(), stored.size());
//    }
//
//    @Test
//    public void limitWorks() {
//        WellSql.insert(getHeroes()).asSingleTransaction(true).execute();
//        List<SuperHero> heroes = WellSql.select(SuperHero.class).limit(1).getAsModel();
//        assertEquals(1, heroes.size());
//    }
//
//    @Test
//    public void asyncDeliversToMainThread() {
//        final List<SuperHero> heroes = getHeroes();
//        WellSql.insert(heroes).execute();
//        WellSql.select(SuperHero.class).getAsModelAsync(new SelectQuery.Callback<List<SuperHero>>() {
//            @Override
//            public void onDataReady(List<SuperHero> data) {
//                assertTrue(Thread.currentThread() == Looper.getMainLooper().getThread());
//                assertEquals(heroes.size(), data.size());
//            }
//        });
//    }
//
//    @Test
//    public void conditionalDeleteWorks() {
//        List<SuperHero> heroes = getHeroes();
//        WellSql.insert(heroes).execute();
//        WellSql.delete(SuperHero.class).where()
//                .equals(SuperHeroTable.NAME, heroes.get(0).getName())
//                .endWhere().execute();
//        List<SuperHero> dbHeroes = WellSql.select(SuperHero.class).getAsModel();
//        assertEquals(heroes.size() - 1, dbHeroes.size());
//    }
//
//    @Test
//    public void complexSelectsWork() {
//        WellSql.insert(getHeroes()).execute();
//        List<SuperHero> heroes = WellSql.select(SuperHero.class)
//                .where().greaterThen(SuperHeroTable.FOUGHT, 12)
//                .beginGroup().equals(SuperHeroTable.NAME, "Groot").or()
//                .equals(SuperHeroTable.NAME, "Rocket Raccoon").endGroup().endWhere()
//                .orderBy(SelectQuery.ORDER_DESCENDING, SuperHeroTable.FOUGHT)
//                .limit(12)
//                .getAsModel();
//
//        assertEquals(4, heroes.size());
//        assertTrue(heroes.get(0).getName().equals("Douglas Adams"));
//    }
//
//    @Test
//    public void constraintsWork() {
//        SuperHero hero = getHeroes().get(0);
//        hero.setFoughtVillains(-1);
//        WellSql.insert(hero).execute();
//        assertEquals(0, WellSql.select(SuperHero.class).getAsModel().size());
//    }
//
//    @Test
//    public void conditionalUpdateWorks() {
//        List<SuperHero> heroes = getHeroes();
//        WellSql.insert(heroes).execute();
//        SuperHero iWantBeLikeHim = heroes.get(6);
//        WellSql.update(SuperHero.class).where()
//                .equals(SuperHeroTable.NAME, iWantBeLikeHim.getName()).endWhere()
//                .put("yarolegovich", new InsertMapper<String>() {
//                    @Override
//                    public ContentValues toCv(String item) {
//                        ContentValues cv = new ContentValues();
//                        cv.put(SuperHeroTable.NAME, item);
//                        return cv;
//                    }
//                }).execute();
//        List<SuperHero> updated = WellSql.select(SuperHero.class)
//                .where().contains(SuperHeroTable.NAME, "govich").endWhere()
//                .getAsModel();
//        assertEquals(1, updated.size());
//        assertEquals(iWantBeLikeHim.getId(), updated.get(0).getId());
//    }
//
//    @Test
//    public void conditionClauseInWorks() {
//        List<SuperHero> heroes = getHeroes();
//        WellSql.insert(heroes).execute();
//        List<SuperHero> found = WellSql.select(SuperHero.class)
//                .where().isIn(SuperHeroTable.FOUGHT, Arrays.asList(3, 4)).endWhere()
//                .getAsModel();
//        assertEquals(3, found.size());
//    }
//
//    @Test
//    public void insertIdNoAutoincrementWorks() {
//        WellSql.insert(getVillains()).execute();
//        List<Villain> villains = WellSql.select(Villain.class).getAsModel();
//        assertEquals(getVillains().get(0).getId(), villains.get(0).getId());
//    }

    private List<SuperHero> getHeroes() {
        return Arrays.asList(
                new SuperHero("Hank Pym", 1),
                new SuperHero("Hulk", 3),
                new SuperHero("Rocket Raccoon", 22),
                new SuperHero("Douglas Adams", 42),
                new SuperHero("Iron Man", 4),
                new SuperHero("Thor", 12),
                new SuperHero("Jake Wharton", 7),
                new SuperHero("Groot", 2),
                new SuperHero("Nick Fury", 4)
        );
    }

    private List<Villain> getVillains() {
        return Arrays.asList(
                new Villain(12, "Electro", 133),
                new Villain(1488, "Red Scull", 1214),
                new Villain(95, "Sandman", 241)
        );
    }
}
