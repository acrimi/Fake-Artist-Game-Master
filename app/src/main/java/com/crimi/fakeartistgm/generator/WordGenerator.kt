package com.crimi.fakeartistgm.generator

import java.net.URL
import kotlin.random.Random

private const val GENERATOR_URL = "https://www.wordgenerator.net/application/p.php?type=1&id=%s&spaceflag=false"

class WordGenerator {
    var categories = listOf(
        Category("charades_moderate", 1.0),
        Category("charades_hard", 1.0),
        Category("animal_names", 1.0),
        Category("movie_names_popular", .75),
        Category("tv_show_names_popular", .5),
        Category("people_character_names_popular", .5),
        Category("book_names_popular", .25)
    )

    private fun getRandomCategoryName(): Category? {
        val weightSum = categories.map { it.weight }.reduce { acc, weight -> acc + weight }
        val rnd = Random.nextDouble(0.0, weightSum)
        var rangeStart = 0.0
        return categories.find { category ->
            rnd >= rangeStart && rnd < (rangeStart + category.weight).also { rangeStart = it }
        }
    }

    fun generateNewWord(): String? {
        val category = getRandomCategoryName() ?: return null
        val url = GENERATOR_URL.format(category)
        return URL(url).readText().split(',').random()
    }
}