package com.dicoding.picodiploma.mymoviecatalogue.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.dicoding.picodiploma.mymoviecatalogue.db.DatabaseContract.FavouriteColumns.Companion.BACKDROP
import com.dicoding.picodiploma.mymoviecatalogue.db.DatabaseContract.FavouriteColumns.Companion.CATEGORY
import com.dicoding.picodiploma.mymoviecatalogue.db.DatabaseContract.FavouriteColumns.Companion.DATE
import com.dicoding.picodiploma.mymoviecatalogue.db.DatabaseContract.FavouriteColumns.Companion.ID
import com.dicoding.picodiploma.mymoviecatalogue.db.DatabaseContract.FavouriteColumns.Companion.RATING
import com.dicoding.picodiploma.mymoviecatalogue.db.DatabaseContract.FavouriteColumns.Companion.RELEASE
import com.dicoding.picodiploma.mymoviecatalogue.db.DatabaseContract.FavouriteColumns.Companion.TABLE_NAME
import com.dicoding.picodiploma.mymoviecatalogue.db.DatabaseContract.FavouriteColumns.Companion.TITLE

internal class DatabaseHelper(context: Context?) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {

        private const val DATABASE_NAME = "dbsubmissionapp"

        private const val DATABASE_VERSION = 1

        private val SQL_CREATE_TABLE_FAV = "CREATE TABLE $TABLE_NAME" +
                " (${ID} INTEGER PRIMARY KEY," +
                " ${DATE} TEXT NOT NULL," +
                " ${BACKDROP} TEXT NOT NULL," +
                " ${TITLE} TEXT NOT NULL," +
                " ${RATING} TEXT NOT NULL," +
                " ${RELEASE} TEXT NOT NULL," +
                " ${CATEGORY} TEXT NOT NULL)"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_TABLE_FAV)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }
}