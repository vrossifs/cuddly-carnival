package com.dicoding.picodiploma.mymoviecatalogue

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.picodiploma.mymoviecatalogue.favourite.FavouriteActivity
import com.dicoding.picodiploma.mymoviecatalogue.fragment.HomeFragment
import com.dicoding.picodiploma.mymoviecatalogue.fragment.HomeFragment.Companion.TAB
import com.dicoding.picodiploma.mymoviecatalogue.fragment.MoviesFragment
import com.dicoding.picodiploma.mymoviecatalogue.notification.ReminderActivity
import java.io.File


class MainActivity : AppCompatActivity() {

    private lateinit var moviesFragment: MoviesFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.title = resources.getString(R.string.home)
        supportActionBar?.elevation = 0f

        moviesFragment =
            MoviesFragment()

        val mFragmentManager = supportFragmentManager
        val mHomeFragment =
            HomeFragment()
        val fragment = mFragmentManager.findFragmentByTag(HomeFragment::class.java.simpleName)
        if (fragment !is HomeFragment) {
            mFragmentManager
                .beginTransaction()
                .add(R.id.frame_container, mHomeFragment, HomeFragment::class.java.simpleName)
                .commit()
        }

        deleteCache(this)
    }

    fun deleteCache(context: Context) {

        try {
            val dir: File = context.cacheDir
            deleteDir(dir)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun deleteDir(dir: File?): Boolean {
        return if (dir != null && dir.isDirectory) {
            val children: Array<String> = dir.list()
            for (i in children.indices) {
                val success = deleteDir(File(dir, children[i]))
                if (!success) {
                    return false
                }
            }
            dir.delete()
        } else if (dir != null && dir.isFile) {
            dir.delete()
        } else {
            false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_change_settings) {
            val mIntent = Intent(Settings.ACTION_LOCALE_SETTINGS)
            startActivity(mIntent)
        } else if (item.itemId == R.id.favourite) {
            val mIntent = Intent(this, FavouriteActivity::class.java)
            startActivity(mIntent)
        } else if (item.itemId == R.id.search) {
            val mIntent = Intent(this, SearchActivity::class.java)
            mIntent.putExtra("tab", TAB.toString())
            startActivity(mIntent)
        } else if (item.itemId == R.id.reminder_settings) {
            val mIntent = Intent(this, ReminderActivity::class.java)
            startActivity(mIntent)
        }
        return super.onOptionsItemSelected(item)
    }
}