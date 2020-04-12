package za.co.locotec.volume

import android.content.Intent
import android.media.AudioManager
import android.os.IBinder
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.appcompat.app.AppCompatActivity

class MuteService : TileService() {

    private var audioManager: AudioManager? = null


    override fun onTileAdded() {
        super.onTileAdded()

        // Update state
        qsTile.state = Tile.STATE_INACTIVE

        // Update looks
        qsTile.updateTile()
    }

    override fun onClick() {
        super.onClick()
        audioManager = getSystemService(AppCompatActivity.AUDIO_SERVICE) as AudioManager
        audioManager?.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0)
        qsTile.state = Tile.STATE_INACTIVE
//        if(qsTile.state == Tile.STATE_INACTIVE) {
//            // Turn on
//            qsTile.state = Tile.STATE_ACTIVE
//
//        } else {
//            // Turn off
//            qsTile.state = Tile.STATE_INACTIVE
//
//        }

        // Update looks
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

        } else {
            // Turn off
            qsTile.state = Tile.STATE_ACTIVE

        }

        // Update looks
        qsTile.updateTile()
    }
}
