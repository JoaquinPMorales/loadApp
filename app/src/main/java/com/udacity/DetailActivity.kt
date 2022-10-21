package com.udacity

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)
        Log.i("DetailActivity", "Filename: ${intent.extras?.getString(FILENAME)}")
        Log.i("DetailActivity", "Status: ${intent.extras?.getBoolean(STATUS)}")
        findViewById<TextView>(R.id.filename_value).text = intent.extras?.getString(FILENAME)
        findViewById<TextView>(R.id.status_value).text = intent.extras?.getBoolean(STATUS).toString()
        findViewById<Button>(R.id.back_button).setOnClickListener {
            onBackPressed()
        }
    }

}
