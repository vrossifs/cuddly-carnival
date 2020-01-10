package com.dicoding.picodiploma.mymoviecatalogue.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.picodiploma.mymoviecatalogue.*
import com.dicoding.picodiploma.mymoviecatalogue.entity.Data
import kotlinx.android.synthetic.main.fragment_movies.*
import kotlinx.android.synthetic.main.fragment_movies.view.*

/**
 * A simple [Fragment] subclass.
 */
class MoviesFragment : Fragment() {
    private lateinit var adapter: ContentAdapter
    private lateinit var mainViewModel: MainViewModel

    companion object {
        private const val URL = "https://api.themoviedb.org/3/discover"
        internal var LANG = "language"
        internal var CATEGORY = "movie"
        internal var TAG = "MoviesFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_movies, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ContentAdapter()
        adapter.notifyDataSetChanged()

        view.rvMovie?.layoutManager = LinearLayoutManager(requireContext())
        view.rvMovie?.adapter = adapter

        LANG = resources.getString(
            R.string.language
        )

        mainViewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        ).get(MainViewModel::class.java)

        showLoading(true)
        loadDiscover()
    }

    private fun loadDiscover() {
        val hashMap = HashMap<String, String>()
        hashMap["url"] =
            "$URL/$CATEGORY?api_key=${BuildConfig.API_KEY}&language=$LANG&sort_by=popularity.desc"
        hashMap["category"] =
            CATEGORY
        hashMap["title"] = resources.getString(R.string.conn_problem_title)
        hashMap["message"] = resources.getString(R.string.conn_problem_message)

        mainViewModel.setDataDiscover(requireContext(), hashMap)
        mainViewModel.getDataDiscover().observe(this, androidx.lifecycle.Observer {
            if (it != null) {
                adapter.setData(it)
                showLoading(false)
            } else {
                showLoading(false)
            }
        })

        adapter.setOnItemClickCallback(object :
            ContentAdapter.OnItemClickCallback {
            override fun onItemClicked(data: Data) {
                showSelectedMovie(data)
            }
        })
    }

    private fun showLoading(state: Boolean) {
        if (state) {
            progress_bar_movie.visibility = View.VISIBLE
        } else {
            progress_bar_movie.visibility = View.GONE
        }
    }

    private fun showSelectedMovie(data: Data) {
        val moveWithObjectIntent = Intent(activity, DetailActivity::class.java)
        moveWithObjectIntent.putExtra(DetailActivity.EXTRA_CONTENT, data)
        moveWithObjectIntent.putExtra(
            DetailActivity.PREVIOUS, resources.getString(
                R.string.tab_text_1
            )
        )
        startActivity(moveWithObjectIntent)
        (activity as Activity).overridePendingTransition(
            R.anim.slide_in_right,
            R.anim.slide_out_left
        )
    }
}
