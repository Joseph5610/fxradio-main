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

package online.hudacek.fxradio.ui.view.player

import javafx.geometry.Pos
import javafx.scene.layout.Priority
import online.hudacek.fxradio.media.PlayerType
import online.hudacek.fxradio.ui.style.Styles
import online.hudacek.fxradio.ui.viewmodel.PlayerModel
import online.hudacek.fxradio.ui.viewmodel.PlayerState
import online.hudacek.fxradio.ui.viewmodel.PlayerViewModel
import online.hudacek.fxradio.utils.*
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.*

/**
 * Main player view above stations
 * Play/pause, volume controls
 */
class PlayerView : View() {

    private val viewModel: PlayerViewModel by inject()

    private val playerStationBoxView: PlayerStationBoxView by inject()

    private val playGlyph = FontAwesome.Glyph.PLAY.make(size = 22.0, useStyle = false)
    private val stopGlyph = FontAwesome.Glyph.STOP.make(size = 22.0, useStyle = false)
    private val volumeDown = FontAwesome.Glyph.VOLUME_DOWN.make(size = 14.0, useStyle = false)
    private val volumeUp = FontAwesome.Glyph.VOLUME_UP.make(size = 14.0, useStyle = false)

    private val playerControlsBinding = viewModel.playerStateProperty.objectBinding {
        if (it == PlayerState.Playing) {
            stopGlyph
        } else {
            playGlyph
        }
    }

    private val playerControls = button {
        id = "playerControls"
        graphicProperty().bind(playerControlsBinding)
        requestFocusOnSceneAvailable()
        disableWhen {
            viewModel.stationProperty.booleanBinding {
                it == null || !it.isValid()
            }
        }
        action {
            viewModel.togglePlayer()
        }
        addClass(Styles.playerControls)
    }

    private val volumeSlider by lazy {
        slider(-30..5) {
            bind(viewModel.volumeProperty)

            id = "volumeSlider"
            maxWidth = 90.0
            majorTickUnit = 8.0
            isSnapToTicks = true
            isShowTickMarks = true
            paddingTop = 10.0

            //Save new value
            valueProperty().onChange {
                viewModel.commit()
            }
        }
    }

    init {
        viewModel.item = PlayerModel(
                animate = property(Properties.PLAYER_ANIMATE, true),
                playerType = PlayerType.valueOf(property(Properties.PLAYER, "VLC")),
                notifications = property(Properties.NOTIFICATIONS, true),
                volume = property(Properties.VOLUME, 0.0))
    }

    override val root = vbox {
        hbox(12) {
            vgrow = Priority.NEVER
            alignment = Pos.CENTER_LEFT
            paddingLeft = 30.0

            //Play/Pause buttons
            add(playerControls)

            region {
                hgrow = Priority.ALWAYS
            }

            //Station info box
            add(playerStationBoxView)

            region {
                hgrow = Priority.ALWAYS
            }

            //Volume controls
            hbox {
                paddingRight = 30.0
                alignment = Pos.CENTER_LEFT
                button {
                    id = "volumeMinIcon"
                    graphic = volumeDown
                    onLeftClick {
                        volumeSlider.value = volumeSlider.min
                    }
                    addClass(Styles.playerControls)
                }
                add(volumeSlider)
                button {
                    id = "volumeMaxIcon"
                    graphic = volumeUp
                    minWidth = 20.0
                    onLeftClick {
                        volumeSlider.value = volumeSlider.max
                    }
                    addClass(Styles.playerControls)
                }
            }
        }
        addClass(Styles.playerMainBox)
        addClass(Styles.backgroundWhiteSmoke)
    }

    override fun onDock() {
        currentWindow?.setOnSpacePressed {
            viewModel.togglePlayer()
        }
    }
}