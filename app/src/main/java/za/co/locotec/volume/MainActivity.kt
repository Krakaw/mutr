package za.co.locotec.volume

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Bundle
import android.provider.Settings
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.ramotion.fluidslider.FluidSlider

import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private var audioManager: AudioManager? = null
    private var notificationManager: NotificationManager? = null;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        setVolumeSlider(R.id.musicSlider, AudioManager.STREAM_MUSIC, "\uD83C\uDFB5")
        setVolumeSlider(R.id.alarmSlider, AudioManager.STREAM_ALARM, "⏰")
        setVolumeSlider(R.id.notificationSlider, AudioManager.STREAM_NOTIFICATION, "\uD83D\uDCE5")
        setVolumeSlider(R.id.ringSlider, AudioManager.STREAM_RING, "\uD83D\uDCDE")
    }

    private fun setVolumeSlider(id: Int, stream: Int, bubbleText: String) {
        val position = audioManager?.getStreamVolume(stream) ?: 0
        val realMax = audioManager?.getStreamMaxVolume(stream) ?: 0
        val max = 100;

        /*
        * Real Max = 8
        * realValue = 4
        * Max = 100
        * Min = 0
        * Value = 50
        * Value / max * Real
        * realValue / realMax * max
        * */

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
