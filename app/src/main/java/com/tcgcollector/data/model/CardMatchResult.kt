package com.tcgcollector.data.model

data class CardMatchResult(
    val id: String,
    val name: String,
    val number: String,
    val imageUrl: String,
    val variants: List<String>
)
