package com.dicoding.picodiploma.mymoviecatalogue.favourite

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.picodiploma.mymoviecatalogue.DetailActivity
import com.dicoding.picodiploma.mymoviecatalogue.R
import com.dicoding.picodiploma.mymoviecatalogue.db.DatabaseContract
import com.dicoding.picodiploma.mymoviecatalogue.entity.Favourite
import com.dicoding.picodiploma.mymoviecatalogue.helper.MappingHelper
import kotlinx.android.synthetic.main.fragment_fav_tv.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.timerTask

/**
 * A simple [Fragment] subclass.
 */
class FavTvFragment : Fragment() {

    private lateinit var adapter: FavouriteAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fav_tv, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvFavTv.layoutManager = LinearLayoutManager(requireContext())
        rvFavTv.setHasFixedSize(true)
        adapter = FavouriteAdapter()
        adapter.notifyDataSetChanged()
        rvFavTv.adapter = adapter

        showLoading(true)
        Timer().schedule(timerTask {
            activity?.runOnUiThread {
                loadFavAsync()
            }
        }, 500)
    }

    private fun loadFavAsync() {
        GlobalScope.launch(Dispatchers.Main) {
            val deferredFavourite = async(Dispatchers.IO) {
                val cursor = activity?.contentResolver?.query(
                    DatabaseContract.FavouriteColumns.CONTENT_URI,
                    null,
                    null,
                    null,
                    null
                ) as Cursor
                MappingHelper.mapCursorToArrayList(cursor, "Tv")
            }
            val favourite = deferredFavourite.await()
            if (favourite.size > 0) {
                adapter.listFavourite = favourite
                showLoading(false)
                adapter.setOnItemClickCallback(object : FavouriteAdapter.OnItemClickCallback {
                    override fun onItemClicked(favourite: Favourite) {
                        showSelectedMovie(favourite)
                    }
                })
            } else {
                adapter.listFavourite = ArrayList()
                showLoading(false)
                rvFavTv.visibility = View.GONE
                message.visibility = View.VISIBLE
            }
        }
    }

    private fun showLoading(state: Boolean) {
        if (state) {
            progress_bar_fav_tv.visibility = View.VISIBLE
        } else {
            progress_bar_fav_tv.visibility = View.GONE
        }
    }

    private fun showSelectedMovie(favourite: Favourite) {
        val moveWithObjectIntent = Intent(activity, DetailActivity::class.java)
        moveWithObjectIntent.putExtra(DetailActivity.FAVOURITE, favourite)
        moveWithObjectIntent.putExtra(DetailActivity.EXTRA_CONTENT, favourite)
        moveWithObjectIntent.putExtra(
            DetailActivity.PREVIOUS,
            resources.getString(R.string.tab_text_2)
        )
        startActivity(moveWithObjectIntent)
        (activity as Activity).overridePendingTransition(
            R.anim.slide_in_right,
            R.anim.slide_out_left
        )
        activity?.onBackPressed()
    }
}
