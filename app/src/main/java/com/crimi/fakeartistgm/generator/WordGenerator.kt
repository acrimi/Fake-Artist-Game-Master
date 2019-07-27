package com.crimi.fakeartistgm.generator

import java.io.IOException
import java.net.URL
import kotlin.math.min
import kotlin.random.Random

private const val GENERATOR_URL = "https://www.wordgenerator.net/application/p.php?type=1&id=%s&spaceflag=false"

class WordGenerator {
    val categories = listOf(
        Category("charades_moderate", 1.0),
        Category("charades_hard", 1.0),
        Category("animal_names", 1.0),
        Category("movie_names_popular", .75),
        Category("tv_show_names_popular", .5),
        Category("people_character_names_popular", .5),
        Category("book_names_popular", .25)
    )
    private val maxRandom = 1000
    private val normalizedRanges = mutableListOf<Pair<String, IntRange>>()

    init {
        updateNormalizedRanges()
    }


    fun updateNormalizedRanges() {
        normalizedRanges.clear()
        val weightSum = categories.map { it.weight }.reduce { acc, weight -> acc + weight }
        val normalizationRatio = maxRandom / weightSum
        var lastIndex = 0
        for (category in categories) {
            val startIndex = lastIndex
            lastIndex = min(startIndex + (category.weight * normalizationRatio).toInt(), maxRandom)
            normalizedRanges.add(Pair(category.name, startIndex until lastIndex))
        }
    }

    private fun getRandomCategoryName(): String? {
        val index = Random.nextInt(0, maxRandom)
        return normalizedRanges.find { it.second.contains(index) }?.first
    }

    fun generateNewWord(): String? {
        val category = getRandomCategoryName() ?: return null
        val url = GENERATOR_URL.format(category)
        return URL(url).readText().split(',').random()
    }
}