package com.dicoding.picodiploma.mymoviecatalogue

import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dicoding.picodiploma.mymoviecatalogue.db.DatabaseContract
import com.dicoding.picodiploma.mymoviecatalogue.db.DatabaseContract.FavouriteColumns.Companion.CONTENT_URI
import com.dicoding.picodiploma.mymoviecatalogue.entity.Data
import com.dicoding.picodiploma.mymoviecatalogue.entity.Favourite
import com.dicoding.picodiploma.mymoviecatalogue.favourite.FavouriteActivity
import com.dicoding.picodiploma.mymoviecatalogue.helper.MappingHelper
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.detail.*
import kotlinx.android.synthetic.main.overview.*
import kotlinx.android.synthetic.main.review.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class DetailActivity : AppCompatActivity() {
    private lateinit var adapter: ReviewAdapter
    private lateinit var mainViewModel: MainViewModel
    private lateinit var mainActivity: MainActivity
    private lateinit var uriWithId: Uri
    private var favourite: Favourite? = null

    companion object {
        const val FAVOURITE = "favourite"
        const val EXTRA_CONTENT = "extra_content"
        const val PREVIOUS = "previous"
        internal var LANG = "language"
        internal const val URL = "https://api.themoviedb.org/3"
        internal const val MOVIE = "movie"
        internal const val TV = "tv"
        internal var URL_DETAIL_FINAL = "url_final"
        internal var URL_REVIEW_FINAL = "url_final"
        private const val TAG = "DetailActivity"
        var id_movie: String? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        mainActivity = MainActivity()
        mainActivity.deleteCache(this)

        supportActionBar?.title =
            resources.getString(R.string.about) + " " + intent.getStringExtra(PREVIOUS)

        progress_bar_detail.visibility = View.GONE
        LANG = resources.getString(R.string.language)

        mainViewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        ).get(MainViewModel::class.java)

        id_movie = if (id_movie == null) {
            if (intent.getParcelableExtra<Favourite>(FAVOURITE) == null) {
                val data = intent.getParcelableExtra(EXTRA_CONTENT) as Data
                data.id
            } else {
                val data = intent.getParcelableExtra(EXTRA_CONTENT) as Favourite
                data.id
            }
        } else {
            intent.getStringExtra(EXTRA_CONTENT)
        }

        if (intent?.getStringExtra(PREVIOUS) == "Movies" || intent?.getStringExtra(PREVIOUS) == "Film") {
            URL_REVIEW_FINAL =
                "${URL}/${MOVIE}/${id_movie}/reviews?api_key=${BuildConfig.API_KEY}&language=${LANG}"
            URL_DETAIL_FINAL =
                "${URL}/${MOVIE}/${id_movie}?api_key=${BuildConfig.API_KEY}&language=${LANG}"
        } else {
            URL_REVIEW_FINAL =
                "${URL}/${TV}/${id_movie}/reviews?api_key=${BuildConfig.API_KEY}&language=${LANG}"
            URL_DETAIL_FINAL =
                "${URL}/${TV}/${id_movie}?api_key=${BuildConfig.API_KEY}&language=${LANG}"
        }

        showLoading(true)
        loadDetail()
        loadReview()
    }

    private fun loadDetail() {
        val hashMap = HashMap<String, String>()
        hashMap["url"] = URL_DETAIL_FINAL
        hashMap["category"] = intent.getStringExtra(PREVIOUS)
        hashMap["menit"] = resources.getString(R.string.minute)
        hashMap["season"] = resources.getString(R.string.label_seasons)
        hashMap["episode"] = resources.getString(R.string.label_episodes)
        hashMap["null_overview"] = resources.getString(R.string.null_overview)

        mainViewModel.setDataDetail(this, hashMap)
        mainViewModel.getDataDetail().observe(this@DetailActivity, androidx.lifecycle.Observer {
            if (it != null) {
                setViewDetail(it)
                showLoading(false)
            }
        })
    }

    private fun setViewDetail(hashMap: HashMap<String, String?>) {
        Glide.with(applicationContext)
            .load(hashMap["backdrop"])
            .apply(RequestOptions().override(250, 500))
            .into(bg_trailer)

        bt_trailer.setOnClickListener {
            val openURL = Intent(Intent.ACTION_VIEW)
            openURL.data = Uri.parse(hashMap["homepage"])
            try {
                startActivity(openURL, Bundle())
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(this, resources.getString(R.string.trailer_na), Toast.LENGTH_SHORT)
                    .show()
            }
        }

        tv_rating.text = hashMap["rating"]
        tv_judul.text = hashMap["judul"]

        Glide.with(applicationContext)
            .load(hashMap["poster_path"])
            .apply(RequestOptions().override(500, 250))
            .into(poster)

        tv_sinposis.text = hashMap["overview"]
        tv_genre.text = hashMap["genre"]
        tv_durasi.text = hashMap["durasi"]
        tv_tahun.text = hashMap["tahun"]
        tv_popularity.text = hashMap["popularity"]
        tv_country.text = hashMap["country"]

        val values = ContentValues()
        values.put(DatabaseContract.FavouriteColumns.ID, hashMap["id"])
        values.put(DatabaseContract.FavouriteColumns.BACKDROP, hashMap["backdrop"])
        values.put(DatabaseContract.FavouriteColumns.TITLE, hashMap["judul"])
        values.put(DatabaseContract.FavouriteColumns.RATING, hashMap["rating"])
        values.put(DatabaseContract.FavouriteColumns.RELEASE, hashMap["tahun"])
        values.put(DatabaseContract.FavouriteColumns.DATE, getCurrentDate())
        values.put(DatabaseContract.FavouriteColumns.CATEGORY, hashMap["category"])

        uriWithId = Uri.parse("$CONTENT_URI/$id_movie")
        val cursor = contentResolver.query(
            uriWithId,
            null,
            null,
            null,
            null
        )

        if (cursor != null && cursor.moveToFirst()) {
            favourite = MappingHelper.mapCursorToObject(cursor)
            cursor.close()
        }

        if (favourite?.id == id_movie) {
            myToggleButton.setBackgroundDrawable(
                ContextCompat.getDrawable(
                    applicationContext,
                    R.drawable.ic_favorite_on
                )
            )
            myToggleButton.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    // Gunakan uriWithId dari intent activity ini
                    // content://com.dicoding.picodiploma.mynotesapp/note/id
                    uriWithId = Uri.parse("$CONTENT_URI/$id_movie")
                    contentResolver.delete(uriWithId, null, null)
                    myToggleButton.setBackgroundDrawable(
                        ContextCompat.getDrawable(
                            applicationContext,
                            R.drawable.ic_favorite_off
                        )
                    )
                    Toast.makeText(
                        this,
                        resources.getString(R.string.remove_favourite),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    contentResolver.insert(CONTENT_URI, values)

                    myToggleButton.setBackgroundDrawable(
                        ContextCompat.getDrawable(
                            applicationContext,
                            R.drawable.ic_favorite_on
                        )
                    )
                    Toast.makeText(
                        this,
                        resources.getString(R.string.add_favourite),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        } else {
            myToggleButton.setBackgroundDrawable(
                ContextCompat.getDrawable(
                    applicationContext,
                    R.drawable.ic_favorite_off
                )
            )
            myToggleButton.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    contentResolver.insert(CONTENT_URI, values)

                    myToggleButton.setBackgroundDrawable(
                        ContextCompat.getDrawable(
                            applicationContext,
                            R.drawable.ic_favorite_on
                        )
                    )
                    Toast.makeText(
                        this,
                        resources.getString(R.string.add_favourite),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    // Gunakan uriWithId dari intent activity ini
                    // content://com.dicoding.picodiploma.mynotesapp/note/id
                    uriWithId = Uri.parse("$CONTENT_URI/$id_movie")
                    contentResolver.delete(uriWithId, null, null)
                    myToggleButton.setBackgroundDrawable(
                        ContextCompat.getDrawable(
                            applicationContext,
                            R.drawable.ic_favorite_off
                        )
                    )
                    Toast.makeText(
                        this,
                        resources.getString(R.string.remove_favourite),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }
    }

    private fun loadReview() {
        adapter = ReviewAdapter()
        adapter.notifyDataSetChanged()

        rv_review.layoutManager = LinearLayoutManager(this)
        rv_review.adapter = adapter

        val hashMap = HashMap<String, String>()
        hashMap["url"] = URL_REVIEW_FINAL
        hashMap["title"] = resources.getString(R.string.conn_problem_title)
        hashMap["message"] = resources.getString(R.string.conn_problem_message)

        mainViewModel.setDataReview(this, hashMap)
        mainViewModel.getDataReview().observe(this, androidx.lifecycle.Observer {
            if (it != null) {
                adapter.setData(it)
                showLoading(false)
            }
        })
    }

    private fun showLoading(state: Boolean) {
        if (state) {
            progress_bar_detail.visibility = View.VISIBLE
        } else {
            progress_bar_detail.visibility = View.GONE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (intent.getParcelableExtra<Favourite>(FAVOURITE) == null) {
            menuInflater.inflate(R.menu.detail_menu, menu)
        } else {
            menuInflater.inflate(R.menu.fav_menu, menu)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_change_settings) {
            val mIntent = Intent(Settings.ACTION_LOCALE_SETTINGS)
            startActivity(mIntent)
        } else if (item.itemId == R.id.favourite) {
            val mIntent = Intent(this, FavouriteActivity::class.java)
            startActivity(mIntent)
        } else if (item.itemId == R.id.home_menu) {
            val mIntent = Intent(this, MainActivity::class.java)
            startActivity(mIntent)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val date = Date()

        return dateFormat.format(date)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (intent.getParcelableExtra<Favourite>(FAVOURITE) == null) {
            finish()
        } else {
            startActivity(Intent(this, FavouriteActivity::class.java))
            finish()
        }
    }
}
