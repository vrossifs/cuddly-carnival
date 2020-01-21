package com.dicoding.picodiploma.mymoviecatalogue.helper

import android.database.Cursor
import com.dicoding.picodiploma.mymoviecatalogue.db.DatabaseContract
import com.dicoding.picodiploma.mymoviecatalogue.entity.Favourite

object MappingHelper {
    fun mapCursorToArrayList(favCursor: Cursor, condition: String): ArrayList<Favourite> {
        val favList = ArrayList<Favourite>()
        while (favCursor.moveToNext()) {
            val id =
                favCursor.getString(favCursor.getColumnIndexOrThrow(DatabaseContract.FavouriteColumns.ID))
            val backdrop =
                favCursor.getString(favCursor.getColumnIndexOrThrow(DatabaseContract.FavouriteColumns.BACKDROP))
            val title =
                favCursor.getString(favCursor.getColumnIndexOrThrow(DatabaseContract.FavouriteColumns.TITLE))
            val release =
                favCursor.getString(favCursor.getColumnIndexOrThrow(DatabaseContract.FavouriteColumns.RELEASE))
            val rating =
                favCursor.getString(favCursor.getColumnIndexOrThrow(DatabaseContract.FavouriteColumns.RATING))
            val date =
                favCursor.getString(favCursor.getColumnIndexOrThrow(DatabaseContract.FavouriteColumns.DATE))
            val category =
                favCursor.getString(favCursor.getColumnIndexOrThrow(DatabaseContract.FavouriteColumns.CATEGORY))
            if (condition == category) {
                favList.add(
                    Favourite(
                        id + "",
                        backdrop + "",
                        title + "",
                        release + "",
                        rating + "",
                        date + "",
                        category + ""
                    )
                )
            }
        }
        return favList
    }

    fun mapCursorToObject(favCursor: Cursor): Favourite {
        favCursor.moveToFirst()
        val id =
            favCursor.getString(favCursor.getColumnIndexOrThrow(DatabaseContract.FavouriteColumns.ID))
        val backdrop =
            favCursor.getString(favCursor.getColumnIndexOrThrow(DatabaseContract.FavouriteColumns.BACKDROP))
        val title =
            favCursor.getString(favCursor.getColumnIndexOrThrow(DatabaseContract.FavouriteColumns.TITLE))
        val release =
            favCursor.getString(favCursor.getColumnIndexOrThrow(DatabaseContract.FavouriteColumns.RELEASE))
        val rating =
            favCursor.getString(favCursor.getColumnIndexOrThrow(DatabaseContract.FavouriteColumns.RATING))
        val date =
            favCursor.getString(favCursor.getColumnIndexOrThrow(DatabaseContract.FavouriteColumns.DATE))
        val category =
            favCursor.getString(favCursor.getColumnIndexOrThrow(DatabaseContract.FavouriteColumns.CATEGORY))
        return Favourite(
            id + "",
            backdrop + "",
            title + "",
            release + "",
            rating + "",
            date + "",
            category + ""
        )
    }
}