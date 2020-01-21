package com.dicoding.picodiploma.mymoviecatalogue.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import com.dicoding.picodiploma.mymoviecatalogue.db.DatabaseContract.AUTHORITY
import com.dicoding.picodiploma.mymoviecatalogue.db.DatabaseContract.FavouriteColumns.Companion.CONTENT_URI
import com.dicoding.picodiploma.mymoviecatalogue.db.DatabaseContract.FavouriteColumns.Companion.TABLE_NAME
import com.dicoding.picodiploma.mymoviecatalogue.db.FavouriteHelper

class MyContentProvider : ContentProvider() {

    companion object {
        private const val FAV = 1
        private const val FAV_ID = 2
        private val sUriMatcher = UriMatcher(UriMatcher.NO_MATCH)
        private lateinit var favouriteHelper: FavouriteHelper

        init {
            // content://com.dicoding.picodiploma.mymoviecatalogue/favourite
            sUriMatcher.addURI(AUTHORITY, TABLE_NAME, FAV)
            // content://com.dicoding.picodiploma.mymoviecatalogue/favourite/id
            sUriMatcher.addURI(AUTHORITY, "$TABLE_NAME/#", FAV_ID)
        }
    }

    override fun delete(uri: Uri, s: String?, strings: Array<String>?): Int {
        val deleted: Int = when (FAV_ID) {
            sUriMatcher.match(uri) -> favouriteHelper.deleteById(uri.lastPathSegment.toString())
            else -> 0
        }
        context?.contentResolver?.notifyChange(CONTENT_URI, null)
        return deleted
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, contentValues: ContentValues?): Uri? {
        val added: Long = when (FAV) {
            sUriMatcher.match(uri) -> favouriteHelper.insert(contentValues)
            else -> 0
        }
        context?.contentResolver?.notifyChange(CONTENT_URI, null)
        return Uri.parse("$CONTENT_URI/$added")
    }

    override fun onCreate(): Boolean {
        favouriteHelper = FavouriteHelper.getInstance(context as Context)
        favouriteHelper.open()
        return true
    }

    override fun query(
        uri: Uri,
        strings: Array<String>?,
        s: String?,
        strings1: Array<String>?,
        s1: String?
    ): Cursor? {
        val cursor: Cursor?
        when (sUriMatcher.match(uri)) {
            FAV -> cursor = favouriteHelper.queryAll()
            FAV_ID -> cursor = favouriteHelper.SelectIdById(uri.lastPathSegment.toString())
            else -> cursor = null
        }
        return cursor
    }

    override fun update(
        uri: Uri,
        contentValues: ContentValues?,
        s: String?,
        strings: Array<String>?
    ): Int {
        val updated: Int = when (FAV_ID) {
            sUriMatcher.match(uri) -> favouriteHelper.update(
                uri.lastPathSegment.toString(),
                contentValues
            )
            else -> 0
        }
        context?.contentResolver?.notifyChange(CONTENT_URI, null)
        return updated
    }
}
