package com.dicoding.picodiploma.mymoviecatalogue.notification

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.picodiploma.mymoviecatalogue.R
import kotlinx.android.synthetic.main.activity_reminder.*


class ReminderActivity : AppCompatActivity() {
    private lateinit var notificationReceiver: NotificationReceiver
    val TAG = "ReminderActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminder)

        supportActionBar?.title = resources.getString(R.string.reminder_settings)

        notificationReceiver = NotificationReceiver()

        sw_daily.isChecked = readStateDaily()
        sw_daily.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                saveStateDaily(true)
                Toast.makeText(
                    this@ReminderActivity,
                    resources.getString(R.string.reminder_daily_on),
                    Toast.LENGTH_SHORT
                ).show()
                notificationReceiver.setDailyReminder(this)
            } else {
                saveStateDaily(false)
                Toast.makeText(this, resources.getString(R.string.reminder_off), Toast.LENGTH_SHORT)
                    .show()
                notificationReceiver.cancelDailyReminder(applicationContext)
            }
        }

        sw_release.isChecked = readStateRelease()
        sw_release.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                saveStateRelease(true)
                Toast.makeText(
                    this@ReminderActivity,
                    resources.getString(R.string.reminder_release_on),
                    Toast.LENGTH_SHORT
                ).show()
                notificationReceiver.setReleaseTodayReminder(this)
            } else {
                saveStateRelease(false)
                Toast.makeText(this, resources.getString(R.string.reminder_off), Toast.LENGTH_SHORT)
                    .show()
                notificationReceiver.cancelReleaseToday(applicationContext)
            }
        }
    }

    fun saveStateDaily(reminder: Boolean) {
        val aSharedPreferenes = getSharedPreferences(
            "daily", Context.MODE_PRIVATE
        )
        val aSharedPreferenesEdit = aSharedPreferenes.edit()
        aSharedPreferenesEdit.putBoolean("StateDaily", reminder)
        aSharedPreferenesEdit.commit()
    }

    private fun readStateDaily(): Boolean {
        val aSharedPreferenes = getSharedPreferences(
            "daily", Context.MODE_PRIVATE
        )
        return aSharedPreferenes.getBoolean("StateDaily", false)
    }

    fun saveStateRelease(reminder: Boolean) {
        val aSharedPreferenes = getSharedPreferences(
            "release", Context.MODE_PRIVATE
        )
        val aSharedPreferenesEdit = aSharedPreferenes.edit()
        aSharedPreferenesEdit.putBoolean("StateRelease", reminder)
        aSharedPreferenesEdit.commit()
    }

    private fun readStateRelease(): Boolean {
        val aSharedPreferenes = getSharedPreferences(
            "release", Context.MODE_PRIVATE
        )
        return aSharedPreferenes.getBoolean("StateRelease", false)
    }
}
