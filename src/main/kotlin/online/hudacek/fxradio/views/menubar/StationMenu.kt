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

package online.hudacek.fxradio.views.menubar

import com.github.thomasnield.rxkotlinfx.actionEvents
import javafx.scene.control.Menu
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import online.hudacek.fxradio.Config
import online.hudacek.fxradio.controllers.menubar.MenuBarController
import online.hudacek.fxradio.events.NotificationEvent
import online.hudacek.fxradio.extension.shouldBeDisabled
import online.hudacek.fxradio.model.PlayerModel
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.*

class StationMenu : Component() {

    private val controller: MenuBarController by inject()
    private val playerModel: PlayerModel by inject()

    private val keyInfo = KeyCodeCombination(KeyCode.I, KeyCombination.CONTROL_DOWN)
    private val keyAdd = KeyCodeCombination(KeyCode.A, KeyCombination.CONTROL_DOWN)
    private val keyFavourites = KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN)

    val menu = Menu(messages["menu.station"]).apply {
        item(messages["menu.station.info"], keyInfo) {
            shouldBeDisabled(playerModel.stationProperty)
            action {
                controller.openStationInfo()
            }
        }

        item(messages["menu.station.favourite"], keyFavourites) {
            disableWhen(booleanBinding(playerModel.stationProperty) {
                value == null || !value.isValidStation() || value.isFavourite.blockingGet()
            })

            actionEvents()
                    .flatMapSingle { playerModel.stationProperty.value.isFavourite }
                    .filter { !it }
                    .flatMapSingle { playerModel.stationProperty.value.addFavourite() }
                    .subscribe({
                        fire(NotificationEvent(messages["menu.station.favourite.added"], FontAwesome.Glyph.CHECK))
                    }, {
                        fire(NotificationEvent(messages["menu.station.favourite.error"]))
                    })
        }

        item(messages["menu.station.favourite.remove"]) {
            visibleWhen(booleanBinding(playerModel.stationProperty) {
                value != null && value.isValidStation() && value.isFavourite.blockingGet()
            })

            actionEvents()
                    .flatMapSingle { playerModel.stationProperty.value.isFavourite }
                    .filter { it }
                    .flatMapSingle { playerModel.stationProperty.value.removeFavourite() }
                    .subscribe({
                        fire(NotificationEvent(messages["menu.station.favourite.removed"], FontAwesome.Glyph.CHECK))
                    }, {
                        fire(NotificationEvent(messages["menu.station.favourite.remove.error"]))
                    })
        }

        item(messages["menu.station.vote"]) {
            shouldBeDisabled(playerModel.stationProperty)
            action {
                controller.voteForStation()
            }
        }

        item(messages["menu.station.add"], keyAdd) {
            isVisible = Config.Flags.addStationEnabled
            action {
                controller.openAddNewStation()
            }
        }
    }
}
