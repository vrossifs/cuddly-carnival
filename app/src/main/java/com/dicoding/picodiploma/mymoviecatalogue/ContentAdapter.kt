package com.dicoding.picodiploma.mymoviecatalogue

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dicoding.picodiploma.mymoviecatalogue.entity.Data
import kotlinx.android.synthetic.main.row_item.view.*

class ContentAdapter : RecyclerView.Adapter<ContentAdapter.ContentViewHolder>() {

    private val mData = ArrayList<Data>()

    fun setData(items: ArrayList<Data>) {
        mData.clear()
        mData.addAll(items)
        notifyDataSetChanged()
    }

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ContentViewHolder {
        val mView =
            LayoutInflater.from(viewGroup.context).inflate(R.layout.row_item, viewGroup, false)
        return ContentViewHolder(mView)
    }

    override fun onBindViewHolder(holder: ContentViewHolder, position: Int) {
        holder.bind(mData[position])
    }

    override fun getItemCount(): Int = mData.size

    inner class ContentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(data: Data) {
            with(itemView) {
                Glide.with(itemView.context)
                    .load(data.backdrop)
                    .apply(RequestOptions().override(500, 250))
                    .into(iv_backdrop)
                tv_rating.text = data.rating
                tv_title.text = data.title
                tv_release.text = data.release

                itemView.setOnClickListener { onItemClickCallback.onItemClicked(data) }
            }
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: Data)

    }
}
