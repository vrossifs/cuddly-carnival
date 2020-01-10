package com.dicoding.picodiploma.mymoviecatalogue.notification

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.dicoding.picodiploma.mymoviecatalogue.BuildConfig
import com.dicoding.picodiploma.mymoviecatalogue.DetailActivity
import com.dicoding.picodiploma.mymoviecatalogue.MainActivity
import com.dicoding.picodiploma.mymoviecatalogue.R
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*


class NotificationReceiver : BroadcastReceiver() {

    private val EXTRA_TYPE = "type"
    private val TYPE_DAILY = "daily_reminder"
    private val TYPE_RELEASE = "release_reminder"
    private val ID_DAILY_REMINDER = 1000
    private val ID_RELEASE_TODAY = 1001
    val TAG = "NotificationReceiver"

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        val type = intent.getStringExtra(EXTRA_TYPE)
        if (type == TYPE_DAILY) {
            showDailyReminder(context)
        } else if (type == TYPE_RELEASE) {
            getReleaseToday(context)
        }
    }

    private fun getReminderTime(type: String): Calendar? {
        val calendar: Calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, if (type == TYPE_DAILY) 21 else 21)
        calendar.set(Calendar.MINUTE, if (type == TYPE_DAILY) 16 else 27)
        calendar.set(Calendar.SECOND, 0)
        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DATE, 1)
        }
        return calendar
    }

    private fun getReminderIntent(context: Context, type: String): Intent? {
        val intent = Intent(context, NotificationReceiver::class.java)
        intent.putExtra(EXTRA_TYPE, type)
        return intent
    }

    fun setReleaseTodayReminder(context: Context) {
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ID_RELEASE_TODAY,
            getReminderIntent(context, TYPE_RELEASE),
            0
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            getReminderTime(TYPE_RELEASE)!!.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    fun setDailyReminder(context: Context) {
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ID_DAILY_REMINDER,
            getReminderIntent(context, TYPE_DAILY),
            0
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            getReminderTime(TYPE_DAILY)!!.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    private fun getReleaseToday(context: Context) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = Date()
        val now: String = dateFormat.format(date)

        val url = "https://api.themoviedb.org/3/discover/movie?" +
                "api_key=${BuildConfig.API_KEY}&" +
                "primary_release_date.gte=${now}&" +
                "primary_release_date.lte=${now}"

        AndroidNetworking.get(url)
            .setPriority(Priority.LOW)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    kotlin.run {
                        try {
                            val json = response.getJSONArray("results")
                            var id = 2
                            for (position in 0 until json.length()) {
                                val x = json.getJSONObject(position)
                                val title: String = x.getString("title") + ""
                                val desc = context.getString(R.string.message_release)
                                id++
                                showReleaseToday(context, title, desc, id, x.getString("id"))
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun onError(anError: ANError?) {
                    Log.e(TAG, "onError: $anError") //untuk log pada onerror
                }

            })
    }

    private fun showReleaseToday(
        context: Context,
        title: String,
        desc: String,
        id: Int,
        id_movie: String
    ) {
        val CHANNEL_ID = "Channel_2"
        val CHANNEL_NAME = "Today release channel"
        val intent = Intent(context, DetailActivity::class.java)
        DetailActivity.id_movie = id_movie
        intent.putExtra(DetailActivity.EXTRA_CONTENT, id_movie)
        intent.putExtra(
            DetailActivity.PREVIOUS, context.resources.getString(
                R.string.tab_text_1
            )
        )
        val pendingIntent = PendingIntent.getActivity(
            context, id, intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val uriRingtone: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val mNotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val mBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_access_time_black_24dp)
            .setContentTitle(title)
            .setContentText(desc)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setSound(uriRingtone)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        val notification: Notification = mBuilder.build()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            mBuilder.setChannelId(CHANNEL_ID)
            mNotificationManager.createNotificationChannel(channel)
        }
        mNotificationManager.notify(id, notification)
    }

    private fun showDailyReminder(context: Context) {
        val NOTIFICATION_ID = 1
        val CHANNEL_ID = "Channel_1"
        val CHANNEL_NAME = "Daily Reminder channel"
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, NOTIFICATION_ID, intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val uriRingtone: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val mNotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val mBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_access_time_black_24dp)
            .setContentTitle(context.resources.getString(R.string.title_daily))
            .setContentText(context.resources.getString(R.string.message_daily))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setSound(uriRingtone)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        val notification: Notification = mBuilder.build()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            mBuilder.setChannelId(CHANNEL_ID)
            mNotificationManager.createNotificationChannel(channel)
        }
        mNotificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun cancelReminder(context: Context, type: String) {
        val alarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java)
        val requestCode: Int =
            if (type.equals(TYPE_DAILY, ignoreCase = true)) ID_DAILY_REMINDER else ID_RELEASE_TODAY
        val pendingIntent =
            PendingIntent.getBroadcast(context, requestCode, intent, 0)
        pendingIntent.cancel()
        alarmManager.cancel(pendingIntent)
    }

    fun cancelDailyReminder(context: Context) {
        cancelReminder(context, TYPE_DAILY)
    }

    fun cancelReleaseToday(context: Context) {
        cancelReminder(context, TYPE_RELEASE)
    }
}
