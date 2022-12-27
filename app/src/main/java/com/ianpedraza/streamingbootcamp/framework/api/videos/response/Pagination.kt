package com.ianpedraza.streamingbootcamp.framework.api.videos.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Pagination(
    @Expose
    @SerializedName("currentPage")
    val currentPage: Int,

    @Expose
    @SerializedName("currentPageItems")
    val currentPageItems: Int,

    @Expose
    @SerializedName("itemsTotal")
    val itemsTotal: Int,

    @Expose
    @SerializedName("links")
    val links: List<Link>,

    @Expose
    @SerializedName("pageSize")
    val pageSize: Int,

    @Expose
    @SerializedName("pagesTotal")
    val pagesTotal: Int
)
