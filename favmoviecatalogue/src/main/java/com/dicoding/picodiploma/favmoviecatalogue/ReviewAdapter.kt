package com.dicoding.picodiploma.favmoviecatalogue

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.picodiploma.favmoviecatalogue.entity.DataReview
import kotlinx.android.synthetic.main.cardview_review.view.*

class ReviewAdapter : RecyclerView.Adapter<ReviewAdapter.ContentViewHolder>() {

    private val mData = ArrayList<DataReview>()

    fun setData(items: ArrayList<DataReview>) {
        mData.clear()
        mData.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ContentViewHolder {
        val view: View = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.cardview_review, viewGroup, false)
        return ContentViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContentViewHolder, position: Int) {
        holder.bind(mData[position])
    }

    override fun getItemCount(): Int = mData.size

    inner class ContentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(data: DataReview) {
            with(itemView) {
                if (data.id == "null") {
                    dataReview.visibility = View.GONE
                    dataMessage.visibility = View.VISIBLE
                    tv_pesan.text = resources.getString(R.string.null_review)
                } else {
                    dataReview.visibility = View.VISIBLE
                    dataMessage.visibility = View.GONE
                    tv_author.text = resources.getString(R.string.review_by) + " " + data.author
                    tv_content.text = data.content
                    bt_detail.setOnClickListener {
                        val openURL = Intent(Intent.ACTION_VIEW)
                        openURL.data = Uri.parse(data.url + "")
                        try {
                            startActivity(context, openURL, Bundle())
                        } catch (e: ActivityNotFoundException) {
                            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
}