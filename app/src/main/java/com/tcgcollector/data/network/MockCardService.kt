package com.tcgcollector.data.network

import com.tcgcollector.data.model.CardMatchResult
import kotlinx.coroutines.delay

object MockCardService {
    suspend fun searchCardByNameAndNumber(name: String, number: String): List<CardMatchResult> {
        delay(1000) // simulate network delay

        val formattedName = name.trim().lowercase()
        val formattedNumber = number.trim()

        return when {
            formattedName.contains("slowking") && formattedNumber == "058/142" -> listOf(
                CardMatchResult(
                    id = "slowking-1",
                    name = "Slowking",
                    number = "058/142",
                    imageUrl = "https://images.pokemontcg.io/swsh7/058_hires.png",
                    variants = listOf("Normal", "Holo", "Reverse Holo")
                )
            )

            formattedName.contains("bulbasaur") && formattedNumber == "1/25" -> listOf(
                CardMatchResult(
                    id = "bulbasaur-1",
                    name = "Bulbasaur",
                    number = "1/25",
                    imageUrl = "https://images.pokemontcg.io/smp/SM158_hires.png",
                    variants = listOf("Normal", "Confetti Holo", "Reverse Holo")
                )
            )

            else -> emptyList() // no match
        }
    }
}
