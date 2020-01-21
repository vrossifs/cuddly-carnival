package com.dicoding.picodiploma.favmoviecatalogue.favourite

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.picodiploma.favmoviecatalogue.R

class FavouriteActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favourite)

        supportActionBar?.title = resources.getString(R.string.favourite)
        supportActionBar?.elevation = 0f

        val mFragmentManager = supportFragmentManager
        val mFavFragment = FavouriteFragment()
        val fragment = mFragmentManager.findFragmentByTag(FavouriteFragment::class.java.simpleName)
        if (fragment !is FavouriteFragment) {
            mFragmentManager
                .beginTransaction()
                .add(
                    R.id.frame_container_fav,
                    mFavFragment,
                    FavouriteFragment::class.java.simpleName
                )
                .commit()
        }
    }

//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        menuInflater.inflate(R.menu.fav_menu, menu)
//        return super.onCreateOptionsMenu(menu)
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        if (item.itemId == R.id.action_change_settings) {
//            val mIntent = Intent(Settings.ACTION_LOCALE_SETTINGS)
//            startActivity(mIntent)
//        } else if (item.itemId == R.id.home_menu) {
//            val mIntent = Intent(this, MainActivity::class.java)
//            startActivity(mIntent)
//        } else if (item.itemId == R.id.reminder_settings) {
////            val mIntent = Intent(this, ReminderActivity::class.java)
////            startActivity(mIntent)
//        }
//        return super.onOptionsItemSelected(item)
//    }
}
