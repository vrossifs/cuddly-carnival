package com.dicoding.picodiploma.mymoviecatalogue.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.dicoding.picodiploma.mymoviecatalogue.db.DatabaseContract.FavouriteColumns.Companion.CATEGORY
import com.dicoding.picodiploma.mymoviecatalogue.db.DatabaseContract.FavouriteColumns.Companion.DATE
import com.dicoding.picodiploma.mymoviecatalogue.db.DatabaseContract.FavouriteColumns.Companion.ID
import com.dicoding.picodiploma.mymoviecatalogue.db.DatabaseContract.FavouriteColumns.Companion.TABLE_NAME
import java.sql.SQLException

class FavouriteHelper(context: Context?) {
    private val dataBaseHelper: DatabaseHelper = DatabaseHelper(context)

    private lateinit var database: SQLiteDatabase

    companion object {
        const val DATABASE_TABLE = TABLE_NAME
        private var INSTANCE: FavouriteHelper? = null

        fun getInstance(context: Context?): FavouriteHelper {
            if (INSTANCE == null) {
                synchronized(SQLiteOpenHelper::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = FavouriteHelper(context)
                    }
                }
            }
            return INSTANCE as FavouriteHelper
        }
    }

    @Throws(SQLException::class)
    fun open() {
        database = dataBaseHelper.writableDatabase
    }

    fun close() {
        dataBaseHelper.close()

        if (database.isOpen)
            database.close()
    }

    fun queryAll(): Cursor {
        return database.query(
            DATABASE_TABLE,
            null,
            null,
            null,
            null,
            null,
            "$DATE DESC",
            null
        )
    }

    fun queryByCategory(category: String?): Cursor {
        return database.query(
            DATABASE_TABLE,
            null,
            "$CATEGORY = ?",
            arrayOf(category),
            null,
            null,
            "$DATE DESC",
            null
        )
    }

    fun SelectIdById(id: String?): Cursor {
        return database.rawQuery("SELECT * FROM $DATABASE_TABLE WHERE $ID = $id", null)
    }

    fun insert(values: ContentValues?): Long {
        return database.insert(DATABASE_TABLE, null, values)
    }

    fun update(id: String?, values: ContentValues?): Int {
        return database.update(DATABASE_TABLE, values, "$ID = ?", arrayOf(id))
    }

    fun deleteById(id: String?): Int {
        return database.delete(DATABASE_TABLE, "$ID = '$id'", null)
    }
}