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

package online.hudacek.fxradio.ui.view.menu

import javafx.scene.control.CheckMenuItem
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import online.hudacek.fxradio.media.MediaPlayer
import online.hudacek.fxradio.media.MediaPlayerFactory
import online.hudacek.fxradio.ui.disableWhenInvalidStation
import online.hudacek.fxradio.ui.menu
import online.hudacek.fxradio.utils.macos.MacUtils
import online.hudacek.fxradio.viewmodel.OsNotificationViewModel
import online.hudacek.fxradio.viewmodel.PlayerState
import online.hudacek.fxradio.viewmodel.PlayerViewModel
import tornadofx.*

class PlayerMenu : BaseMenu() {

    private val playerViewModel: PlayerViewModel by inject()
    private val osNotificationViewModel: OsNotificationViewModel by inject()

    private var playerTypeItem: CheckMenuItem by singleAssign()

    private val keyPlay = KeyCodeCombination(KeyCode.P, KeyCombination.CONTROL_DOWN)
    private val keyStop = KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN)

    init {
        playerViewModel.mediaPlayerProperty.onChange {
            playerTypeItem.isSelected = it?.playerType == MediaPlayer.Type.Humble
        }
    }

    override val menu by lazy {
        menu(messages["menu.player.controls"]) {
            item(messages["menu.player.start"], keyPlay) {
                disableWhenInvalidStation(playerViewModel.stationProperty)
                action {
                    playerViewModel.stateProperty.value = PlayerState.Playing
                }
            }

            item(messages["menu.player.stop"], keyStop) {
                disableWhenInvalidStation(playerViewModel.stationProperty)
                action {
                    playerViewModel.stateProperty.value = PlayerState.Stopped
                }
            }

            separator()

            playerTypeItem = checkmenuitem(messages["menu.player.switch"]) {
                isSelected = playerViewModel.mediaPlayerProperty.value?.playerType == MediaPlayer.Type.Humble
                action {
                    playerViewModel.stateProperty.value = PlayerState.Stopped
                    playerViewModel.mediaPlayerProperty.value?.release()
                    playerViewModel.mediaPlayerProperty.value =
                            MediaPlayerFactory.toggle(playerViewModel.mediaPlayerProperty.value.playerType)
                    playerViewModel.commit()
                }
            }

            checkmenuitem(messages["menu.player.animate"]) {
                bind(playerViewModel.animateProperty)
                action {
                    playerViewModel.commit()
                }
            }

            checkmenuitem(messages["menu.player.notifications"]) {
                bind(osNotificationViewModel.showProperty)
                action {
                    osNotificationViewModel.commit()
                }

                visibleWhen {
                    //Notifications available only on Mac
                    MacUtils.isMac.toProperty()
                }
            }
        }
    }
}
