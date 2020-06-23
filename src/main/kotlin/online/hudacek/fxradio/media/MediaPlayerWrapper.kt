/*
 *  Copyright 2020 FXRadio by hudacek.online
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package online.hudacek.fxradio.media

import com.github.thomasnield.rxkotlinfx.toObservableChanges
import com.github.thomasnield.rxkotlinfx.toObservableChangesNonNull
import javafx.application.Platform
import mu.KotlinLogging
import online.hudacek.fxradio.events.*
import online.hudacek.fxradio.model.PlayerModel
import tornadofx.*

//TODO get rid of this class in its current form
class MediaPlayerWrapper : Component(), ScopedInstance {

    private val logger = KotlinLogging.logger {}
    private val playerModel: PlayerModel by inject()

    var playingStatus = PlayingStatus.Stopped

    private var mediaPlayer: MediaPlayer = MediaPlayer.stub

    private var internalVolume = 0.0

    init {
        //Update internal player type
        playerModel.playerType.toObservableChanges()
                .map { it.newVal }
                .subscribe {
                    logger.info { "player type changed: $it" }
                    mediaPlayer.releasePlayer()
                    mediaPlayer = initMediaPlayer(it)
                }

        //Set volume for current player
        playerModel.volumeProperty.toObservableChangesNonNull()
                .map { it.newVal.toDouble() }
                .subscribe {
                    logger.info { "volume changed: $it" }
                    internalVolume = it
                    mediaPlayer.changeVolume(it)
                }

        //Toggle playing
        subscribe<PlaybackChangeEvent> {
            playingStatus = it.playingStatus
            if (it.playingStatus == PlayingStatus.Playing) {
                play(playerModel.stationProperty.value.url_resolved)
            } else {
                mediaPlayer.cancelPlaying()
            }
        }

        playerModel.stationProperty.toObservableChangesNonNull()
                .filter { it.newVal.isValidStation() }
                .map { it.newVal }
                .subscribe {
                    play(it.url_resolved)
                    playingStatus = PlayingStatus.Playing
                }
    }

    fun init() {
        logger.info { "init MediaPlayerWrapper with MediaPlayer $mediaPlayer" }
    }

    private fun initMediaPlayer(playerType: PlayerType): MediaPlayer {
        return if (playerType == PlayerType.FFmpeg) {
            FFmpegPlayer()
        } else {
            try {
                VLCPlayer()
            } catch (e: Exception) {
                playerModel.playerType.set(PlayerType.FFmpeg)
                logger.error(e) { "VLC init failed, init native library " }
                fire(NotificationEvent("Player can't be initialized. Library is not installed on the system."))
                FFmpegPlayer()
            }
        }
    }

    private fun play(url: String?) {
        url?.let {
            mediaPlayer.apply {
                changeVolume(internalVolume)
                play(url)
            }
        }
    }

    fun release() = mediaPlayer.releasePlayer()

    fun handleError(t: Throwable) {
        Platform.runLater {
            fire(PlaybackChangeEvent(PlayingStatus.Stopped))
            fire(NotificationEvent(t.localizedMessage))
            logger.error(t) { "Stream can't be played" }
        }
    }

    fun mediaMetaChanged(mediaMeta: MediaMeta) = fire(PlaybackMetaChangedEvent(mediaMeta))

    fun togglePlaying() {
        if (playingStatus == PlayingStatus.Playing) {
            fire(PlaybackChangeEvent(PlayingStatus.Stopped))
        } else {
            fire(PlaybackChangeEvent(PlayingStatus.Playing))
        }
    }
}