# WellSql
Android API for working with SQLiteDatabase can be characterized as inconvenient and ugly (at least for me). This library tends to make work with it easier and reduce amount of boilerplate code you perhaps used to write. 
##Features
* Just a wrapper for old trusted methods query, insert, delete etc.
* Generates classes with public static final strings for each column name.
* Generates mappers for your model (Conversion to ContentValues and from Cursor).
* Just one recursive call, nothing critical
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
  compile 'com.yarolegovich:wellsql:1.0.1'
  apt 'com.yarolegovich.wellsql-processor:1.0.1'
}

```
