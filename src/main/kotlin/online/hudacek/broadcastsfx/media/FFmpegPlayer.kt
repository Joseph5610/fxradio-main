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

package online.hudacek.broadcastsfx.media

import io.humble.video.*
import io.humble.video.javaxsound.AudioFrame
import io.humble.video.javaxsound.MediaAudioConverterFactory
import kotlinx.coroutines.*
import mu.KotlinLogging
import java.nio.ByteBuffer
import javax.sound.sampled.FloatControl
import javax.sound.sampled.LineUnavailableException
import javax.sound.sampled.SourceDataLine
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

internal class FFmpegPlayer(private val mediaPlayer: MediaPlayerWrapper)
    : MediaPlayer {

    private val logger = KotlinLogging.logger {}

    private var mediaPlayerCoroutine: Job? = null
    private var audioFrame: AudioFrame? = null

    private val handler = CoroutineExceptionHandler { _, exception ->
        mediaPlayer.handleError(exception)
    }

    override fun play(url: String) {
        mediaPlayerCoroutine = GlobalScope.launch(handler) {
            val demuxer = Demuxer.make()
            try {
                val numStreams = demuxer.stream(url)

                var audioStreamId = -1
                var audioDecoder: Decoder? = null

                for (i in 0 until numStreams) {
                    val demuxerStream = demuxer.getStream(i)

                    val decoder = demuxerStream.decoder

                    if (decoder != null && decoder.codecType == MediaDescriptor.Type.MEDIA_AUDIO) {
                        audioStreamId = i
                        audioDecoder = decoder
                        break
                    }
                }
                if (audioStreamId == -1) throw RuntimeException("could not find audio stream in container")
                if (audioDecoder == null) throw RuntimeException("could not find audio decoder")

                audioDecoder.apply {
                    open()
                    val samples = MediaAudio.make(
                            this.frameSize,
                            this.sampleRate,
                            this.channels,
                            this.channelLayout,
                            this.sampleFormat)

                    val converter = MediaAudioConverterFactory.createConverter(
                            MediaAudioConverterFactory.DEFAULT_JAVA_AUDIO,
                            samples)
                    audioFrame = AudioFrame.make(converter.javaFormat) ?: throw LineUnavailableException()

                    var rawAudio: ByteBuffer? = null

                    val packet = MediaPacket.make()
                    while (demuxer.read(packet) >= 0) {
                        if (!isActive) break
                        if (packet.streamIndex == audioStreamId) {
                            var offset = 0
                            var bytesRead = 0
                            do {
                                bytesRead += decode(samples, packet, offset)
                                if (samples.isComplete) {
                                    rawAudio = converter.toJavaAudio(rawAudio, samples)
                                    audioFrame?.play(rawAudio)
                                }
                                offset += bytesRead
                            } while (offset < packet.size)
                        }
                    }

                    do {
                        decode(samples, null, 0)
                        if (samples.isComplete) {
                            rawAudio = converter.toJavaAudio(rawAudio, samples)
                            audioFrame?.play(rawAudio)
                        }
                    } while (samples.isComplete)
                }
            } finally {
                demuxer.close()
                audioFrame?.dispose()
            }
        }
    }

    override fun changeVolume(volume: Double): Boolean {
        return try {
            AudioFrame::class.memberProperties
                    .filter { it.name == "mLine" }[0].apply {
                isAccessible = true
                val lineValue = getter.call(audioFrame) as SourceDataLine
                val gainControl = lineValue.getControl(FloatControl.Type.MASTER_GAIN) as FloatControl
                gainControl.value = volume.toFloat()
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun cancelPlaying() {
        logger.debug { "ending current stream if any" }
        mediaPlayerCoroutine?.isActive.let {
            mediaPlayerCoroutine?.cancel()
        }
    }

    override fun releasePlayer() = cancelPlaying()

    private fun Decoder.open() = this.open(null, null)

    private fun Demuxer.stream(streamUrl: String?): Int {
        this.open(streamUrl, null, false, true, null, null)
        return this.numStreams
    }
}