package com.crimi.fakeartistgm.generator

import java.net.URL
import kotlin.random.Random

private const val GENERATOR_URL = "https://www.wordgenerator.net/application/p.php?type=1&id=%s&spaceflag=false"

class WordGenerator {
    var categories = listOf(
        Category("General (moderate)", "charades_moderate", 1.0),
        Category("General (hard)", "charades_hard", 1.0),
        Category("Animal", "animal_names", 1.0),
        Category("Movie", "movie_names_popular", .75),
        Category("TV Show", "tv_show_names_popular", .5),
        Category("Character", "people_character_names_popular", .5),
        Category("Book", "book_names_popular", .25)
    )

    private fun getRandomCategory(): Category? {
        val weightSum = categories.map { it.weight }.reduce { acc, weight -> acc + weight }
        val rnd = Random.nextDouble(0.0, weightSum)
        var rangeStart = 0.0
        return categories.find { category ->
            rnd >= rangeStart && rnd < (rangeStart + category.weight).also { rangeStart = it }
        }
    }

    fun generateNewWord(): String? {
        val category = getRandomCategory() ?: return null
        val url = GENERATOR_URL.format(category.id)
        return URL(url).readText().split(',').random()
    }
}