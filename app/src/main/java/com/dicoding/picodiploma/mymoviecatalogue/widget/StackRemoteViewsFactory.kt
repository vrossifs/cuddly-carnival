package com.dicoding.picodiploma.mymoviecatalogue.widget

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.os.bundleOf
import com.dicoding.picodiploma.mymoviecatalogue.R
import com.dicoding.picodiploma.mymoviecatalogue.db.DatabaseContract
import com.dicoding.picodiploma.mymoviecatalogue.db.FavouriteHelper
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

internal class StackRemoteViewsFactory(private val mContext: Context) :
    RemoteViewsService.RemoteViewsFactory {

    private val mWidgetItems = ArrayList<Bitmap>()
    private val backdrop = ArrayList<String>()
    private lateinit var favouriteHelper: FavouriteHelper


    override fun onCreate() {

    }

    override fun onDataSetChanged() {
        //Ini berfungsi untuk melakukan refresh saat terjadi perubahan.
        mWidgetItems.clear()
        backdrop.clear()

        favouriteHelper = FavouriteHelper.getInstance(mContext)
        val cursor = favouriteHelper.queryByCategory("Movies")

        if (cursor.moveToFirst()) {
            do {
                backdrop.add(cursor.getString(cursor.getColumnIndex(DatabaseContract.FavouriteColumns.BACKDROP)))
            } while (cursor.moveToNext())
        }

        for (i in backdrop.indices) {
            val url = URL(backdrop[i])
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input: InputStream = connection.inputStream
            val myBitmap = BitmapFactory.decodeStream(input)
            mWidgetItems.add(myBitmap)
        }
    }

    override fun onDestroy() {

    }

    override fun getCount(): Int = mWidgetItems.size

    override fun getViewAt(position: Int): RemoteViews {
        val rv = RemoteViews(mContext.packageName, R.layout.widget_item)
        rv.setImageViewBitmap(R.id.imageView, mWidgetItems[position])

        val extras = bundleOf(
            ImageBannerWidget.EXTRA_ITEM to position
        )
        val fillInIntent = Intent()
        fillInIntent.putExtras(extras)

        rv.setOnClickFillInIntent(R.id.imageView, fillInIntent)
        return rv
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(i: Int): Long = i.toLong()

    override fun hasStableIds(): Boolean = false

}