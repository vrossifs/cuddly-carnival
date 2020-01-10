package com.dicoding.picodiploma.mymoviecatalogue.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Favourite(
    var id: String?,
    var backdrop: String?,
    var title: String?,
    var release: String?,
    var rating: String?,
    var date: String?,
    var category: String?
) : Parcelable