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

package online.hudacek.fxradio.views.menu

import com.github.thomasnield.rxkotlinfx.actionEvents
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import online.hudacek.fxradio.events.LibraryType
import online.hudacek.fxradio.events.NotificationEvent
import online.hudacek.fxradio.events.RefreshFavourites
import online.hudacek.fxradio.storage.Database
import online.hudacek.fxradio.utils.menu
import online.hudacek.fxradio.viewmodel.LibraryViewModel
import online.hudacek.fxradio.viewmodel.PlayerViewModel
import online.hudacek.fxradio.viewmodel.SelectedLibrary
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.*

class FavouritesMenu : Component(), ScopedInstance {

    private val libraryViewModel: LibraryViewModel by inject()
    private val playerViewModel: PlayerViewModel by inject()

    private val keyFavourites = KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN)

    val menu by lazy {
        menu(messages["menu.favourites"]) {
            item(messages["menu.favourites.show"]).action {
                libraryViewModel.selectedProperty.value = SelectedLibrary(LibraryType.Favourites)
            }
            separator()
            item(messages["menu.station.favourite"], keyFavourites) {
                disableWhen(playerViewModel.stationProperty.booleanBinding {
                    it == null || !it.isValid() || Database.isFavourite(it).blockingGet()
                })

                actionEvents()
                        .flatMapSingle { Database.isFavourite(playerViewModel.stationProperty) }
                        .filter { !it }
                        .flatMapSingle { Database.addFavourite(playerViewModel.stationProperty) }
                        .subscribe({
                            fire(NotificationEvent(messages["menu.station.favourite.added"], FontAwesome.Glyph.CHECK))
                            fire(RefreshFavourites())
                        }, {
                            fire(NotificationEvent(messages["menu.station.favourite.error"]))
                        })
            }

            item(messages["menu.station.favourite.remove"]) {
                visibleWhen(playerViewModel.stationProperty.booleanBinding {
                    it != null && it.isValid() && Database.isFavourite(it).blockingGet()
                })

                actionEvents()
                        .flatMapSingle { Database.isFavourite(playerViewModel.stationProperty) }
                        .filter { it }
                        .flatMapSingle { Database.removeFavourite(playerViewModel.stationProperty) }
                        .subscribe({
                            fire(NotificationEvent(messages["menu.station.favourite.removed"], FontAwesome.Glyph.CHECK))
                            fire(RefreshFavourites())
                        }, {
                            fire(NotificationEvent(messages["menu.station.favourite.remove.error"]))
                        })
            }
        }
    }
}