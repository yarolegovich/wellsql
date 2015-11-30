# WellSql
Android API for working with SQLiteDatabase can be characterized as inconvenient and ugly (at least for me). This library tends to make work with it easier and reduce amount of boilerplate code you perhaps used to write. 
##Features
* Just a wrapper for old trusted methods query, insert, delete etc.
* Generates classes with public static final strings for each column name.
* Generates mappers for your model (Conversion to ContentValues and from Cursor).
* In 98% of use cases library takes care of opening/closing db and cursors.
* Just one reflesive call, nothing critical

##Add to your project

In build.gradle for your project add 
```
buildscript {
  dependencies {
    classpath 'com.neenbedankt.gradle.plugins:android-apt:1.8'
  }
}

```
In build.gradle for your module add

```

apply plugin: 'com.neenbedankt.android-apt'

dependencies {
  compile 'com.yarolegovich:wellsql:1.0.2'
  apt 'com.yarolegovich:wellsql-processor:1.0.2'
}

```
##Table creation and setup

One of the most important features to ease your life is boilerplate code generation. Here are the steps to create a table.
1. Create your model class, make it implement Identifiable (just two methods - getId() and setId(int id) and configure it with the help of annotations.

```
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
}
```
Here is some weird example to show annotations you have in your arsenal. **Class also needs getters and setters, so generated mapper can work**. 
2. Rebuild your project. Two classes SuperHeroTable and SuperHeroMapper will be generated. You don't need to do anything with the second one, but the first will contain useful fields:

```
public final class SuperHeroTable implements TableClass {
  public static final String ID = "_id";

  public static final String NAME = "NAME";

  public static final String FOUGHT = "fought";
  
  ...
}
```
**I strongly reccomend you to have field id or mId of type int in your model class, or better write custom mappers.**
3. Last step is to create config for WellSql. You will need it to call
```
WellSql.init(new MyWellConfig(context)); 
```
on application startup (extend application class and call this method in overriden onCreate). You can either implement interface WellConfig or extend class DefaultWellConfig (I advice to do the latter, because this class take care of binding with generated classes). Here is an example of how to extend it:

```
public class WellConfig extends DefaultWellConfig {

    public WellConfig(Context context) {
        super(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db, WellTableManager helper) {
        helper.createTable(SuperHero.class);
        helper.createTable(Villain.class);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, WellTableManager helper, int newVersion, int oldVersion) {
        helper.dropTable(SuperHero.class);
        helper.dropTable(Villain.class);
        onCreate(db, helper);
    }

    @Override
    protected Map<Class<?>, SQLiteMapper<?>> registerMappers() {
        return super.registerMappers();
    }
    
    ...
}
```
Method registerMethods() can be used to register your custom mappers for classes. If class has generated mapper - it will be ignored, if you pass your own implementation.
If you don't need classes to be generated #confused, you can use 

```
@Table(generateMapper = false, generateTable = false)
public class Villain implements Identifiable {
  ...
}
```

##Usage examples

Builder for queries. You can get results as Map<String, Object>, where keys are column names and values are extracted from cursor. Or results can be converted to model (with the help of automatically generated mappers or your custom mappers.

You can also perform async queries. Result will be delivered to Callback object you provide on the main thread. 

```
/*
 * Asynchronously select all from SuperHeroTable
 */
WellSql.select(SuperHero.class).getAsModelAsync(new SelectQuery.Callback<List<SuperHero>>() {
    @Override
    public void onDataReady(List<SuperHero> data) {
      assertTrue(Thread.currentThread() == Looper.getMainLooper().getThread());
    }
});

/*
 * Some pointless query to show query builder
 */
List<SuperHero> heroes = WellSql.select(SuperHero.class)
    .where().greaterThen(SuperHeroTable.FOUGHT, 12)
    .beginGroup().equals(SuperHeroTable.NAME, "Groot").or()
    .equals(SuperHeroTable.NAME, "Rocket Raccoon").endGroup().endWhere()
    .orderBy(SelectQuery.ORDER_DESCENDING, SuperHeroTable.FOUGHT)
    .limit(12)
    .getAsModel();
```
The same way you can peform insert, update and delete queries.

```
WellSql.insert(getHeroes()).asSingleTransaction(true).execute();

WellSql.delete(SuperHero.class).execute();

WellSql.delete(SuperHero.class).where()
    .greaterThenOrEqual(SuperHeroTable.FOUGHT, 12)
    .endWhere().execute();

WellSql.update(SuperHero.class).whereId(hero.getId()).put(anotherHero).execute();
```
Factory methods of WellSql class covers most use cases (I think so), but if you want to make something unusual with db you can always call

```
SQLiteDatabase db = WellSql.giveMeReadableDb();

SQLiteDatabase db = WellSql.giveMeWritableDb();
```
For more usage examples you can see tests of well-sample, but I think nothing extraordinary in api :)
##Licence
MIT licence, I don't really care. 
