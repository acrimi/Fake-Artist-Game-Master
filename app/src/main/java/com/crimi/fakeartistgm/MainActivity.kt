package com.crimi.fakeartistgm

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.crimi.fakeartistgm.databinding.ActivityMainBinding
import com.crimi.fakeartistgm.generator.Prompt
import com.crimi.fakeartistgm.generator.WordGenerator
import com.sendgrid.SendGrid
import com.sendgrid.SendGridException
import org.parceler.Parcels
import java.io.IOException

private const val REQUEST_EDIT_SETTINGS = 1

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val emailAdapter = EmailAdapter()
    private val sendGrid = SendGrid(BuildConfig.SEND_GRID_API_KEY)
    private val wordGenerator = WordGenerator()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = emailAdapter
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_EDIT_SETTINGS && resultCode == Activity.RESULT_OK) {
            wordGenerator.categories = Parcels.unwrap(data?.getParcelableExtra(EXTRA_SETTINGS_CATEGORIES))
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.settings) {
            settingsClicked()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun settingsClicked() {
        val intent = Intent(this, SettingsActivity::class.java)
        intent.putExtra(EXTRA_SETTINGS_CATEGORIES, Parcels.wrap(ArrayList(wordGenerator.categories)))
        startActivityForResult(intent, REQUEST_EDIT_SETTINGS)
    }

    fun reset(view: View) {
        emailAdapter.reset()
    }

    fun send(view: View) {
        binding.isSending = true
        Thread {
            val prompt: Prompt
            try {
                prompt = wordGenerator.generateNewPrompt() ?:
                        return@Thread notifySendError(IOException("word generation failed"))
            } catch (e: IOException) {
                notifySendError(e)
                return@Thread
            }
            val artists = emailAdapter.emails.mapNotNullTo(mutableListOf()) { it.get() }
            val fakeArtist = artists.removeAt(0)
            var sender = BuildConfig.CUSTOM_SENDER_EMAIL
            if (sender.isEmpty()) {
                sender = "fakeartistgamebot@example.com"
            }

            val promptEmail = SendGrid.Email()
            promptEmail.addTo(artists.toTypedArray())
            promptEmail.from = sender
            promptEmail.fromName = "Fake Artist Game Bot"
            promptEmail.subject = "Drawing Prompt"
            promptEmail.text = "The category is: \"${prompt.category}\"\nThe word to draw is: \"${prompt.word}\""

            val fakeArtistEmail = SendGrid.Email()
            fakeArtistEmail.addTo(fakeArtist)
            fakeArtistEmail.from = sender
            fakeArtistEmail.fromName = "Fake Artist Game Bot"
            fakeArtistEmail.subject = "Drawing Prompt"
            fakeArtistEmail.text = "The category is: \"${prompt.category}\"\nYou are the fake artist!"

            try {
                if (artists.size > 0) {
                    sendGrid.send(promptEmail)
                }
                sendGrid.send(fakeArtistEmail)
                notifySendSuccess()
            } catch (e: SendGridException) {
                notifySendError(e)
            } catch (e: IOException) {
                notifySendError(e)
            }

        }.start()
    }

    private fun notifySendSuccess() {
        runOnUiThread {
            Toast.makeText(this, "Prompt emails have been sent!", Toast.LENGTH_LONG).show()
            binding.isSending = false
            binding.executePendingBindings()
        }
    }

    private fun notifySendError(e: Exception) {
        e.printStackTrace()
        runOnUiThread {
            Toast.makeText(this, "Error sending prompts: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            binding.isSending = false
            binding.executePendingBindings()
        }
    }
}
