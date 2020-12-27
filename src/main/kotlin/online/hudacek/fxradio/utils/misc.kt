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

package online.hudacek.fxradio.utils

import com.github.thomasnield.rxkotlinfx.observeOnFx
import io.reactivex.SingleTransformer
import io.reactivex.schedulers.Schedulers
import javafx.application.Platform
import mu.KotlinLogging
import online.hudacek.fxradio.NotificationEvent
import online.hudacek.fxradio.macos.MacUtils
import online.hudacek.fxradio.media.PlayerType
import org.apache.logging.log4j.Level
import tornadofx.FX
import java.io.BufferedReader
import java.io.InputStreamReader

private val logger = KotlinLogging.logger {}

/**
 * Perform async calls on correct thread
 */
internal fun <T> applySchedulers(): SingleTransformer<T, T>? =
        SingleTransformer {
            it.subscribeOn(Schedulers.io())
                    .observeOnFx()
        }

internal fun String.asLevel() = Level.valueOf(this)

/**
 * Helper for loading of playerType from app.properties file
 */
internal fun String.asPlayerType() = try {
    PlayerType.valueOf(this)
} catch (e: IllegalArgumentException) {
    Platform.runLater {
        FX.eventbus.fire(NotificationEvent("Invalid player type value detected. Using PlayerType.Custom"))
    }
    logger.error(e) { "This playerType is invalid. Using PlayerType.Custom" }
    PlayerType.Custom
}

//Command line utilities
internal fun command(command: String) = Runtime.getRuntime().exec(command)

internal val Process.result: String
    get() {
        val sb = StringBuilder()
        val reader = BufferedReader(InputStreamReader(inputStream))
        reader.forEachLine {
            sb.append(it)
        }
        return sb.toString()
    }

internal val isSystemDarkMode: Boolean
    get() = if (MacUtils.isMac) MacUtils.isSystemDarkMode
    else {
        logger.error { "isSystemDarkMode: Unsupported OS" }
        false
    }