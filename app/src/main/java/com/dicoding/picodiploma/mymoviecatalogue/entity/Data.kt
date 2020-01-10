package com.dicoding.picodiploma.mymoviecatalogue.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Data(
    var id: String,
    var poster: String,
    var backdrop: String,
    var title: String,
    var release: String,
    var rating: String
) : Parcelable