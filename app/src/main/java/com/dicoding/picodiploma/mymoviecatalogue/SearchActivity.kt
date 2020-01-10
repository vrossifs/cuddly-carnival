package com.dicoding.picodiploma.mymoviecatalogue

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.picodiploma.mymoviecatalogue.entity.Data
import kotlinx.android.synthetic.main.activity_search.*

class SearchActivity : AppCompatActivity() {

    private lateinit var adapter: ContentAdapter
    private lateinit var mainViewModel: MainViewModel

    companion object {
        private const val URL = "https://api.themoviedb.org/3/search"
        internal var LANG = "language"
        internal var CATEGORY = "category"
        const val PREVIOUS = "previous"
        var TITLE = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        showLoading(false)

        if (intent.getStringExtra("tab") == "0") {
            TITLE = resources.getString(R.string.tab_text_1)
            CATEGORY = "movie"
        } else {
            TITLE = resources.getString(R.string.tab_text_2)
            CATEGORY = "tv"
        }

        supportActionBar?.title = TITLE

        adapter = ContentAdapter()
        adapter.notifyDataSetChanged()

        rvSearch.layoutManager = LinearLayoutManager(this)
        rvSearch.adapter = adapter

        LANG = resources.getString(R.string.language)

        mainViewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        ).get(MainViewModel::class.java)
    }

    private fun loadDiscover(query: String) {
        showLoading(true)
        val hashMap = HashMap<String, String>()
        hashMap["url"] = "$URL/$CATEGORY?api_key=${BuildConfig.API_KEY}&language=$LANG&query=$query"
        hashMap["category"] = CATEGORY
        hashMap["title"] = resources.getString(R.string.conn_problem_title)
        hashMap["message"] = resources.getString(R.string.conn_problem_message)

        mainViewModel.setDataDiscover(this, hashMap)
        mainViewModel.getDataDiscover().observe(this, androidx.lifecycle.Observer {
            if (it != null) {
                adapter.setData(it)
                showLoading(false)
            } else {
                showLoading(false)
            }
        })

        adapter.setOnItemClickCallback(object : ContentAdapter.OnItemClickCallback {
            override fun onItemClicked(data: Data) {
                showSelected(data)
            }
        })
    }

    private fun showLoading(state: Boolean) {
        if (state) {
            progress_bar_search.visibility = View.VISIBLE
        } else {
            progress_bar_search.visibility = View.GONE
        }
    }

    private fun showSelected(data: Data) {
        val moveWithObjectIntent = Intent(this, DetailActivity::class.java)
        moveWithObjectIntent.putExtra(DetailActivity.EXTRA_CONTENT, data)
        moveWithObjectIntent.putExtra(DetailActivity.PREVIOUS, TITLE)
        startActivity(moveWithObjectIntent)
        this.overridePendingTransition(
            R.anim.slide_in_right,
            R.anim.slide_out_left
        )
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.search).actionView as SearchView

        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.isIconified = false
        searchView.queryHint = resources.getString(R.string.search_hint)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            /*
            Gunakan method ini ketika search selesai atau OK
             */
            override fun onQueryTextSubmit(query: String): Boolean {
                loadDiscover(query)
                return true
            }

            /*
            Gunakan method ini untuk merespon tiap perubahan huruf pada searchView
             */
            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })
        return true
    }
}
