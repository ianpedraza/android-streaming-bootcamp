package com.ianpedraza.streamingbootcamp.domain

data class MetaData(
    val title: String,
    val description: String,
    val date: String,
    val tags: List<String>
)
