package com.dicoding.picodiploma.mymoviecatalogue.db

import android.provider.BaseColumns

internal class DatabaseContract {

    internal class FavouriteColumns : BaseColumns {
        companion object {
            const val TABLE_NAME = "favourite"
            const val ID = "id"
            const val DATE = "date"
            const val BACKDROP = "backdrop"
            const val TITLE = "title"
            const val RATING = "rating"
            const val RELEASE = "release"
            const val CATEGORY = "category"
        }
    }
}