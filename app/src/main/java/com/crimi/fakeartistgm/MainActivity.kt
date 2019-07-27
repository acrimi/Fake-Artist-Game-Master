package com.crimi.fakeartistgm

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.crimi.fakeartistgm.databinding.ActivityMainBinding
import com.crimi.fakeartistgm.generator.WordGenerator
import com.sendgrid.SendGrid
import com.sendgrid.SendGridException
import java.io.IOException
import java.lang.Exception

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

    fun reset(view: View) {
        emailAdapter.reset()
    }

    fun send(view: View) {
        binding.isSending = true
        Thread {
            val clue = wordGenerator.generateNewWord() ?: return@Thread notifySendError(IOException("word generation failed"))
            val artists = emailAdapter.emails.shuffled().mapNotNullTo(mutableListOf()) { it.get() }
            val impostor = artists.removeAt(0)

            val clueEmail = SendGrid.Email()
            clueEmail.addTo(artists.toTypedArray())
            clueEmail.from = "gamebot@example.com"
            clueEmail.fromName = "Fake Artist Game Bot"
            clueEmail.subject = "Drawing Clue"
            clueEmail.text = "The drawing clue is: \"$clue\""

            val impostorEmail = SendGrid.Email()
            impostorEmail.addTo(impostor)
            impostorEmail.from = "gamebot@example.com"
            impostorEmail.fromName = "Fake Artist Game Bot"
            impostorEmail.subject = "Drawing Clue"
            impostorEmail.text = "You are the fake artist!"

            try {
                if (artists.size > 0) {
                    sendGrid.send(clueEmail)
                }
                sendGrid.send(impostorEmail)
                notifySendSuccess()
            } catch (e: SendGridException) {
                notifySendError(e)
            } catch (e: IOException) {
                notifySendError(e)
            } finally {
                runOnUiThread {
                    binding.isSending = false
                    binding.executePendingBindings()
                }
            }

        }.start()
    }

    private fun notifySendSuccess() {
        runOnUiThread {
            Toast.makeText(this, "Clue emails have been sent!", Toast.LENGTH_LONG).show()
        }
    }

    private fun notifySendError(e: Exception) {
        e.printStackTrace()
        runOnUiThread {
            Toast.makeText(this, "Error sending clues: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }
}
