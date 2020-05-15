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

package online.hudacek.broadcastsfx.controllers

import com.sun.javafx.PlatformUtil
import javafx.stage.Stage
import javafx.stage.StageStyle
import online.hudacek.broadcastsfx.Config
import online.hudacek.broadcastsfx.ImageCache
import online.hudacek.broadcastsfx.fragments.*
import online.hudacek.broadcastsfx.media.MediaPlayerWrapper
import tornadofx.*


class MenuBarController : Controller() {

    private val mediaPlayer: MediaPlayerWrapper by inject()

    val usePlatformMenuBarProperty = app.config.boolean(Config.Keys.useNativeMenuBar, true)

    val shouldUsePlatformMenuBar = PlatformUtil.isMac() && usePlatformMenuBarProperty

    fun openStats() = find<StatsFragment>().openModal(stageStyle = StageStyle.UTILITY)

    fun openStationInfo() = find<StationInfoFragment>().openModal(stageStyle = StageStyle.UTILITY)

    fun openAbout() = find<AboutAppFragment>().openModal(stageStyle = StageStyle.UTILITY)

    fun openServerSelect() = find<ServerSelectionFragment>().openModal(stageStyle = StageStyle.UTILITY)

    fun openAttributions() = find<AttributionsFragment>().openModal(stageStyle = StageStyle.UTILITY)

    fun clearCache() = ImageCache.clearCache()

    fun closeApp(currentStage: Stage?) {
        currentStage?.close()
        mediaPlayer.release()
    }

    fun openAddNewStation() = find<AddStationFragment>().openModal(stageStyle = StageStyle.UTILITY)
}