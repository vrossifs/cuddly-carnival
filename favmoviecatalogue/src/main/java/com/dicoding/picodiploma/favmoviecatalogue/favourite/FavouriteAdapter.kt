package com.dicoding.picodiploma.favmoviecatalogue.favourite

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dicoding.picodiploma.favmoviecatalogue.R
import com.dicoding.picodiploma.favmoviecatalogue.entity.Favourite
import kotlinx.android.synthetic.main.row_item.view.*

class FavouriteAdapter : RecyclerView.Adapter<FavouriteAdapter.FavouriteViewHolder>() {
    var listFavourite = ArrayList<Favourite>()
        set(listFavourite) {
            if (listFavourite.size > 0) {
                this.listFavourite.clear()
            }
            this.listFavourite.addAll(listFavourite)

            notifyDataSetChanged()
        }

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    fun addItem(Favourite: Favourite) {
        this.listFavourite.add(Favourite)
        notifyItemInserted(this.listFavourite.size - 1)
    }

    fun updateItem(position: Int, Favourite: Favourite) {
        this.listFavourite[position] = Favourite
        notifyItemChanged(position, Favourite)
    }

    fun removeItem(position: Int) {
        this.listFavourite.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, this.listFavourite.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_item, parent, false)
        return FavouriteViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavouriteViewHolder, position: Int) {
        holder.bind(listFavourite[position])
    }

    override fun getItemCount(): Int = this.listFavourite.size

    inner class FavouriteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(favourite: Favourite) {
            with(itemView) {
                Glide.with(itemView.context)
                    .load(favourite.backdrop)
                    .apply(RequestOptions().override(500, 250))
                    .into(iv_backdrop)
                tv_rating.text = favourite.rating
                tv_title.text = favourite.title
                tv_release.text = favourite.release

                itemView.setOnClickListener { onItemClickCallback.onItemClicked(favourite) }
            }
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(favourite: Favourite)

    }
}