package za.co.locotec.mutr

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.ramotion.fluidslider.FluidSlider

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var audioManager: AudioManager? = null
    private var notificationManager: NotificationManager? = null
    private val CHANNEL_ID = "MutrChannel"
    private val NOTIFICATION_ID = 1489374234

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel()
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        initSliders()
    }

    override fun onResume() {
        super.onResume()
        initSliders()
    }

    override fun onPause() {
        super.onPause()
        initSliders()
    }

    private fun initSliders() {
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager

        checkNotificationPolicyAccess(getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
        //Hacky way to keep checking the volumes, I'm sure there's a way to catch a volume change intent of some sort.
        val handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                setSliders()
                handler.postDelayed(this, 5000)
            }
        }, 0)
    }

    private fun setSliders() {
        setVolumeSlider(R.id.musicSlider, AudioManager.STREAM_MUSIC, "\uD83C\uDFB5")
        setVolumeSlider(R.id.alarmSlider, AudioManager.STREAM_ALARM, "⏰")
        setVolumeSlider(R.id.notificationSlider, AudioManager.STREAM_NOTIFICATION, "\uD83D\uDCE5")
        setVolumeSlider(R.id.ringSlider, AudioManager.STREAM_RING, "\uD83D\uDCDE")

        showNotification(getVolume(AudioManager.STREAM_MUSIC) > 0)
    }

    private fun showNotification(colorized : Boolean) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        val icon = if (getVolume(AudioManager.STREAM_MUSIC) > 0) R.drawable.speaker else R.drawable.speaker_off

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(icon)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setContentIntent(pendingIntent)
            .setColorized(colorized)
            .setOngoing(true)
            .setAutoCancel(false)

        if (colorized) {
            builder.color = ContextCompat.getColor(this, R.color.colorPrimary)
        }

        with(NotificationManagerCompat.from(this)) {
            notify(NOTIFICATION_ID, builder.build())
        }
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun getVolume(stream: Int): Int {
        return audioManager?.getStreamVolume(stream) ?: 0;
    }

    private fun setVolumeSlider(id: Int, stream: Int, bubbleText: String) {
        val position = getVolume(stream) ?: 0
        val realMax = audioManager?.getStreamMaxVolume(stream) ?: 0
        val max = 100

        val slider = findViewById<FluidSlider>(id)
        slider.bubbleText = bubbleText//$newPosition
        slider.positionListener = { pos ->
            val newPosition = ((pos * 100) / max * realMax).toInt()
            if (position != newPosition) {
                if (!audioManager?.isVolumeFixed!!) {
                    audioManager?.setStreamVolume(stream, newPosition, 0)
                }
            }
        }
        val newPosition: Float = (position.toFloat() / realMax.toFloat() * max.toFloat()).toFloat()
        slider.position = newPosition / 100
        slider.startText = ""
        slider.endText = ""
    }

    // Method to check notification policy access status
    private fun checkNotificationPolicyAccess(notificationManager: NotificationManager): Boolean {
        if (notificationManager.isNotificationPolicyAccessGranted) {
            //toast("Notification policy access granted.")
            return true
        } else {
            toast("You need to grant notification policy access.")
            // If notification policy access not granted for this package
            val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
            startActivity(intent)
        }
        return false
    }

    // Extension function to show toast message
    fun Context.toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
