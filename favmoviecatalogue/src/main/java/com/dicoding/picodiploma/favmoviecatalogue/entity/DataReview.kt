package com.dicoding.picodiploma.favmoviecatalogue.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DataReview(
    var id: String,
    var author: String,
    var content: String,
    var url: String
) : Parcelable