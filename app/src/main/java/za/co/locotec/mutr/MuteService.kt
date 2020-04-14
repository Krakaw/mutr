package za.co.locotec.mutr

import android.graphics.drawable.Icon
import android.media.AudioManager
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.appcompat.app.AppCompatActivity

class MuteService : TileService() {

    private var audioManager: AudioManager? = null


    override fun onTileAdded() {
        super.onTileAdded()
        qsTile.state = Tile.STATE_INACTIVE
        qsTile.updateTile()
    }

    override fun onClick() {
        super.onClick()
        audioManager = getSystemService(AppCompatActivity.AUDIO_SERVICE) as AudioManager
        audioManager?.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0)
        qsTile.state = Tile.STATE_INACTIVE
        qsTile.icon = Icon.createWithResource (this, R.drawable.speaker_off)
        qsTile.updateTile()
    }

    override fun onStartListening() {
        super.onStartListening()

        // Called when the Tile becomes visible
        audioManager = getSystemService(AppCompatActivity.AUDIO_SERVICE) as AudioManager
        val volume = audioManager?.getStreamVolume(AudioManager.STREAM_MUSIC) ?: 0

        if(volume == 0) {
            // Turn on
            qsTile.state = Tile.STATE_INACTIVE
            qsTile.icon = Icon.createWithResource (this, R.drawable.speaker_off)
        } else {
            // Turn off
            qsTile.state = Tile.STATE_ACTIVE
            qsTile.icon = Icon.createWithResource (this, R.drawable.speaker)
        }

        // Update looks
        qsTile.updateTile()
    }
}
