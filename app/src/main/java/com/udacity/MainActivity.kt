package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


private val NOTIFICATION_ID = 0
const val FILENAME = "FILENAME"
const val STATUS = "STATUS"

class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action
    private lateinit var notificationChannel : NotificationChannel
//    private lateinit var downloadManager : DownloadManager

    private var selectedUrl = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        val glideBtn = findViewById<View>(R.id.glide_btn) as RadioButton
        val loadAppBtn = findViewById<View>(R.id.load_app_btn) as RadioButton
        val retrofitBtn = findViewById<View>(R.id.retrofit_btn) as RadioButton

        custom_button.setOnClickListener {
            if(selectedUrl.isEmpty())
            {
                //toast
                val toast = Toast.makeText(applicationContext, R.string.no_radio_button_selected, Toast.LENGTH_SHORT)
                toast.show()
            }
            else{
                download()
            }
        }

        //init notificationManager and create channel
        initNotificationManagerAndCreateChannel(this)
        //downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
    }

    private fun initNotificationManagerAndCreateChannel(context: Context)
    {
        notificationManager = ContextCompat.getSystemService(
            context,
            NotificationManager::class.java
        ) as NotificationManager

        notificationChannel = NotificationChannel(
            getString(R.string.download_channel_id),
            getString(R.string.download_channel_name),
            NotificationManager.IMPORTANCE_HIGH
        )
        .apply {
                setShowBadge(false)
        }

        notificationChannel.enableLights(true)
        notificationChannel.lightColor = Color.RED
        notificationChannel.enableVibration(true)
        notificationChannel.description = "Download complete"
        notificationManager.createNotificationChannel(notificationChannel)
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            val action = intent!!.action
            if (action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
            {
                //send notification
                Log.i("MainActivity", "Download complete")
                if (context != null) {
                    notificationManager.sendNotification(getText(R.string.notification_description).toString(), context)
                }
            }
        }
    }

    private fun download() {
        val request =
            DownloadManager.Request(Uri.parse(selectedUrl))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID = downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    companion object {
        private const val GLIDE_URL = "https://github.com/bumptech/glide/archive/refs/heads/master.zip"
        private const val LOAD_APP_URL = "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/refs/heads/master.zip"
        private const val RETROFIT_URL = "https://github.com/square/retrofit/archive/refs/heads/master.zip"
        private const val CHANNEL_ID = "downloadChannel"
    }

    fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            // Is the button now checked?
            val checked = view.isChecked

            // Check which radio button was clicked
            when (view.getId()) {
                R.id.glide_btn ->
                    if (checked) {
                        Log.i("MainActivity", "glide button checked")
                        selectedUrl = GLIDE_URL
                    }
                R.id.load_app_btn ->
                    if (checked) {
                        Log.i("MainActivity", "load button checked")
                        selectedUrl = LOAD_APP_URL
                    }
                R.id.retrofit_btn ->
                    if (checked){
                        selectedUrl = RETROFIT_URL
                        Log.i("MainActivity", "retrofit button checked")
                    }
            }
        }
    }

    fun NotificationManager.sendNotification(messageBody: String, applicationContext: Context){

    val contentIntent = Intent(applicationContext, DetailActivity::class.java)
//    contentIntent.putExtra(FILENAME, filename)
//    contentIntent.putExtra(STATUS, status)

    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

        val builder = NotificationCompat.Builder(
            applicationContext,
            applicationContext.getString(R.string.download_channel_id)
        ).setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(applicationContext
            .getString(R.string.notification_title))
            .setContentText(messageBody)
            .setAutoCancel(true)
            .addAction(R.drawable.ic_launcher_foreground,
            applicationContext.getString(R.string.notification_button),
            contentPendingIntent
            ).setPriority(NotificationCompat.PRIORITY_HIGH)
        notify(NOTIFICATION_ID, builder.build())
    }
}