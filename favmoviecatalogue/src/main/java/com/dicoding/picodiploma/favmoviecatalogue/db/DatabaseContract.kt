package com.dicoding.picodiploma.favmoviecatalogue.db

import android.net.Uri
import android.provider.BaseColumns

object DatabaseContract {

    const val AUTHORITY = "com.dicoding.picodiploma.mymoviecatalogue"
    const val SCHEME = "content"

    class FavouriteColumns : BaseColumns {
        companion object {
            const val TABLE_NAME = "favourite"
            const val ID = "id"
            const val DATE = "date"
            const val BACKDROP = "backdrop"
            const val TITLE = "title"
            const val RATING = "rating"
            const val RELEASE = "release"
            const val CATEGORY = "category"

            // untuk membuat URI content://com.dicoding.picodiploma.mymoviecatalogue/favourite
            val CONTENT_URI: Uri = Uri.Builder().scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath(TABLE_NAME)
                .build()
        }
    }
}