package com.example.a2in1app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    private lateinit var myLayoutg: ConstraintLayout
    private lateinit var tvPhrase: TextView
    private lateinit var tvLetters: TextView
    private lateinit var myHighScore: TextView
    private lateinit var editText: EditText
    private lateinit var button: Button

    private lateinit var rvMessages: RecyclerView
    private lateinit var messages: ArrayList<String>

    private val phrase = "All that glitters is not gold".uppercase()
    private val AnswerDictionary = mutableMapOf<Int, Char>()
    private var Answer = ""
    private var guessedLetters = ""
    private var count = 0
    private var guessPhrase = true

    private lateinit var sharedPreferences: SharedPreferences

    private var score = 0
    private var highScore = 0

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = this.getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE
        )
        highScore = sharedPreferences.getInt("HighScore", 0)

        myHighScore = findViewById(R.id.tvHighScore)
        myHighScore.text = "High Score: $highScore"

        for (i in phrase.indices) {
            if (phrase[i] == ' ') {
                AnswerDictionary[i] = ' '
                Answer += ' '
            } else {
                AnswerDictionary[i] = '*'
                Answer += '*'
            }
        }

        myLayoutg = findViewById(R.id.clMain2)
        messages = ArrayList()

        rvMessages = findViewById(R.id.rvMessages)
        rvMessages.adapter = MessageAdapter(this, messages)
        rvMessages.layoutManager = LinearLayoutManager(this)

        editText = findViewById(R.id.editText)
        button = findViewById(R.id.button)
        button.setOnClickListener { message() }

        tvPhrase = findViewById(R.id.tvPhrase)
        tvLetters = findViewById(R.id.tvLetters)

        updateText()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun message() {
        val msg = editText.text.toString().uppercase()

        if (guessPhrase) {
            if (msg == phrase) {
                disableEntry()
                updateScore()
                showAlertDialog("You win!\n\nPlay again?")
            } else {
                messages.add("Wrong guess: $msg")
                guessPhrase = false
                updateText()
            }
        } else {
            if (msg.isNotEmpty() && msg.length == 1) {
                Answer = ""
                guessPhrase = true
                checkLetters(msg[0])
            } else {
                Snackbar.make(myLayoutg, "Please enter one letter only", Snackbar.LENGTH_LONG).show()
            }
        }

        editText.text.clear()
        editText.clearFocus()
        rvMessages.adapter?.notifyDataSetChanged()
    }

    private fun disableEntry() {
        button.isEnabled = false
        button.isClickable = false
        editText.isEnabled = false
        editText.isClickable = false
    }

    @SuppressLint("SetTextI18n")
    private fun updateText() {
        tvPhrase.text = "Phrase: $Answer"
        tvLetters.text = "Guessed Letters: $guessedLetters"
        if (guessPhrase) {
            editText.hint = "Guess the full phrase"
        } else {
            editText.hint = "Guess a letter"
        }
    }

    private fun checkLetters(guessedLetter: Char) {
        var found = 0
        for (i in phrase.indices) {
            if (phrase[i] == guessedLetter) {
                AnswerDictionary[i] = guessedLetter
                found++
            }
        }
        for (i in AnswerDictionary) {
            Answer += AnswerDictionary[i.key]
        }
        if (Answer == phrase) {
            disableEntry()
            updateScore()
            showAlertDialog("You win!\n\nPlay again?")
        }
        if (guessedLetters.isEmpty()) {
            guessedLetters += guessedLetter
        } else {
            guessedLetters += ", " + guessedLetter
        }
        if (found > 0) {
            messages.add("Found $found ${guessedLetter.uppercase()}(s)")
        } else {
            messages.add("No ${guessedLetter.uppercase()}s found")
        }
        count++
        val guessesLeft = 10 - count
        if (count < 10) {
            messages.add("$guessesLeft guesses remaining")
        }
        updateText()
        rvMessages.scrollToPosition(messages.size - 1)
    }

    private fun updateScore() {
        score = 10 - count
        if (score >= highScore) {
            highScore = score
            with(sharedPreferences.edit()) {
                putInt("HighScore", highScore)
                apply()
            }
            Snackbar.make(myLayoutg, "NEW HIGH SCORE!", Snackbar.LENGTH_LONG).show()
        }
    }

    private fun showAlertDialog(title: String) {
        // build alert dialog
        val dialogBuilder = AlertDialog.Builder(this)

        // set message of alert dialog
        dialogBuilder.setMessage(title)
            // if the dialog is cancelable
            .setCancelable(false)
            // positive button text and action
            .setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, id ->
                this.recreate()
            })
            // negative button text and action
            .setNegativeButton("No", DialogInterface.OnClickListener { dialog, id ->
                dialog.cancel()
            })

        // create dialog box
        val alert = dialogBuilder.create()
        // set title for alert dialog box
        alert.setTitle("Game Over")
        // show alert dialog
        alert.show()
    }

}




