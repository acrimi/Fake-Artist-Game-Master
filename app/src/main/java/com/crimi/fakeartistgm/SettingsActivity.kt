package com.crimi.fakeartistgm

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.crimi.fakeartistgm.databinding.ActivitySettingsBinding
import com.crimi.fakeartistgm.generator.Category
import org.parceler.Parcels

const val EXTRA_SETTINGS_CATEGORIES = "EXTRA_SETTINGS_CATEGORIES"
const val EXTRA_SETTINGS_FAKE_ARTIST_COUNT = "EXTRA_SETTINGS_FAKE_ARTIST_COUNT"

class SettingsActivity : AppCompatActivity() {

    private lateinit var adapter: CategoriesAdapter
    private val viewModel = SettingsViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivitySettingsBinding>(this, R.layout.activity_settings)

        supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            setTitle(R.string.settings_title)
        }

        viewModel.fakeArtistCount.set(intent.getIntExtra(EXTRA_SETTINGS_FAKE_ARTIST_COUNT, 1))
        binding.viewModel = viewModel

        val categories: List<Category> = Parcels.unwrap(intent.getParcelableExtra(EXTRA_SETTINGS_CATEGORIES))
        adapter = CategoriesAdapter(categories)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    fun resetDefaults(view: View) {
        adapter.resetDefaults()
        viewModel.fakeArtistCount.set(1)
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_OK, Intent().apply {
            putExtra(EXTRA_SETTINGS_FAKE_ARTIST_COUNT, Math.max(1, viewModel.fakeArtistCount.get()))
            putExtra(EXTRA_SETTINGS_CATEGORIES, Parcels.wrap(ArrayList(adapter.categories)))
        })
        super.onBackPressed()
    }
}