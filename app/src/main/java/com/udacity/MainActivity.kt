package com.udacity

import android.animation.ObjectAnimator
import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Build
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
const val NOTIFICATION_KEY = "NOTIFICATION"

class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action
    private lateinit var notificationChannel : NotificationChannel
    private lateinit var downloadManager : DownloadManager
    private var animator: ObjectAnimator? = null

    private var selectedUrl = ""
    private var radioButtonSelected = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager

        custom_button.setOnClickListener {
            if(selectedUrl.isEmpty())
            {
                custom_button.buttonState = ButtonState.Completed
                //toast
                val toast = Toast.makeText(applicationContext, R.string.no_radio_button_selected, Toast.LENGTH_SHORT)
                toast.show()
            }
            else{
                custom_button.buttonState = ButtonState.Loading
                download()
            }
        }

        //init notificationManager and create channel
        initNotificationManagerAndCreateChannel(this)
    }

    private fun initNotificationManagerAndCreateChannel(context: Context)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
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
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            val action = intent!!.action
            if (action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                id?.let{
                    val cursor: Cursor? =
                        downloadManager?.query(DownloadManager.Query().setFilterById(downloadID))
                    if (cursor != null){
                        if (cursor.moveToFirst()) {
                            custom_button.buttonState = ButtonState.Completed
                            notificationManager.sendNotification(
                                applicationContext.getString(R.string.notification_description),
                                applicationContext,
                                radioButtonSelected,
                                when(cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
                                    DownloadManager.STATUS_SUCCESSFUL -> true
                                    DownloadManager.STATUS_FAILED -> false
                                    else -> false
                                })
                        }
                    }
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

        // Check connectivity
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkInfo = connectivityManager.activeNetworkInfo

        if (networkInfo != null && networkInfo.isAvailable == true) {

            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            downloadID = downloadManager.enqueue(request)// enqueue puts the download request in the queue.

        } else {

            Toast.makeText(this@MainActivity, "Connection is not available", Toast.LENGTH_LONG).show()
            custom_button.buttonState = ButtonState.Completed
        }
    }

    companion object {
        private const val GLIDE_URL = "https://github.com/bumptech/glide/archive/refs/heads/master.zip"
        private const val LOAD_APP_URL = "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/refs/heads/master.zip"
        private const val RETROFIT_URL = "https://github.com/square/retrofit/archive/refs/heads/master.zip"
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
                        radioButtonSelected = "GLIDE"
                    }
                R.id.load_app_btn ->
                    if (checked) {
                        Log.i("MainActivity", "load button checked")
                        selectedUrl = LOAD_APP_URL
                        radioButtonSelected = "LOAD APP"
                    }
                R.id.retrofit_btn ->
                    if (checked){
                        selectedUrl = RETROFIT_URL
                        Log.i("MainActivity", "retrofit button checked")
                        radioButtonSelected = "RETROFIT"
                    }
            }
        }
    }

    fun NotificationManager.sendNotification(messageBody: String, applicationContext: Context, filename : String, status : Boolean) {

    Log.i("MainActivity", "status received: $status")
    val contentIntent = Intent(applicationContext, DetailActivity::class.java)
    contentIntent.putExtra(FILENAME, filename)
    contentIntent.putExtra(STATUS, status)
    contentIntent.putExtra(NOTIFICATION_KEY, NOTIFICATION_ID)

    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_CANCEL_CURRENT
    )

        val builder = NotificationCompat.Builder(
            applicationContext,
            applicationContext.getString(R.string.download_channel_id)
        ).setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(applicationContext
            .getString(R.string.notification_title))
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .addAction(R.drawable.ic_launcher_foreground,
            applicationContext.getString(R.string.notification_button),
            contentPendingIntent
            )
        notify(NOTIFICATION_ID, builder.build())
    }
}