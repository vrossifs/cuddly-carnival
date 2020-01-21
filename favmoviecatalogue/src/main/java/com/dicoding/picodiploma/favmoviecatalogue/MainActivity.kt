package com.dicoding.picodiploma.favmoviecatalogue

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.picodiploma.favmoviecatalogue.favourite.FavouriteFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.title = resources.getString(R.string.favourite)
        supportActionBar?.elevation = 0f

        val mFragmentManager = supportFragmentManager
        val mFavFragment = FavouriteFragment()
        val fragment = mFragmentManager.findFragmentByTag(FavouriteFragment::class.java.simpleName)
        if (fragment !is FavouriteFragment) {
            mFragmentManager
                .beginTransaction()
                .add(
                    R.id.frame_container,
                    mFavFragment,
                    FavouriteFragment::class.java.simpleName
                )
                .commit()
        }
    }
}
