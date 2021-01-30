package br.ufpe.cin.helloword

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sampleText: TextView = findViewById(R.id.sample_text)
        sampleText.text = getString(R.string.sample_text)
    }
}