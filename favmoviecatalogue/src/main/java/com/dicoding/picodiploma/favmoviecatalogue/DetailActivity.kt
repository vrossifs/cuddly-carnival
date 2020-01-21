package com.dicoding.picodiploma.favmoviecatalogue

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dicoding.picodiploma.favmoviecatalogue.MainViewModel
import com.dicoding.picodiploma.favmoviecatalogue.entity.Data
import com.dicoding.picodiploma.favmoviecatalogue.entity.Favourite
import com.dicoding.picodiploma.favmoviecatalogue.favourite.FavouriteActivity
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.detail.*
import kotlinx.android.synthetic.main.overview.*
import kotlinx.android.synthetic.main.review.*

class DetailActivity : AppCompatActivity() {
    private lateinit var adapter: ReviewAdapter
    private lateinit var mainViewModel: MainViewModel

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
