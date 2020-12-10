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

import javafx.scene.control.Menu
import javafx.scene.control.MenuBar
import online.hudacek.fxradio.FxRadio
import online.hudacek.fxradio.macos.MacMenu
import online.hudacek.fxradio.macos.MacUtils
import online.hudacek.fxradio.ui.viewmodel.MenuModel
import online.hudacek.fxradio.ui.viewmodel.MenuViewModel
import online.hudacek.fxradio.ui.viewmodel.PlayerViewModel
import online.hudacek.fxradio.utils.Properties
import online.hudacek.fxradio.utils.Property
import tornadofx.*

class MenuBarView : View() {

    private val menuViewModel: MenuViewModel by inject()
    private val playerViewModel: PlayerViewModel by inject()

    private val historyMenu: HistoryMenu by inject()
    private val favouritesMenu: FavouritesMenu by inject()
    private val helpMenu: HelpMenu by inject()
    private val stationMenu: StationMenu by inject()
    private val playerMenu: PlayerMenu by inject()

    init {
        menuViewModel.item = MenuModel(MacUtils.isMac && Property(Properties.NATIVE_MENU_BAR).get(true))
    }

    override val root = if (menuViewModel.useNativeProperty.value) {
        platformMenuBar()
    } else {
        defaultMenuBar()
    }

    private fun defaultMenuBar() = menubar {
        menu(FxRadio.appName) {
            addAppMenuContent()
            item(messages["menu.app.quit"]).action {
                currentStage?.close()
                playerViewModel.releasePlayer()
                FxRadio.shutDown()
            }
        }
        menus.addAll(stationMenu.menu,
                playerMenu.menu,
                favouritesMenu.menu,
                historyMenu.menu,
                helpMenu.menu)
    }

    /**
     * Platform specific menu bar working on OSX
     * used instead of in-app menubar
     */
    private fun platformMenuBar(): MenuBar {
        return MacMenu.menuBar {
            MacMenu.appMenu {
                addAppMenuContent()
            }
        }.apply {
            menus.addAll(stationMenu.menu,
                    playerMenu.menu,
                    favouritesMenu.menu,
                    historyMenu.menu,
                    MacMenu.windowMenu(messages["macos.menu.window"]),
                    helpMenu.menu)
        }
    }

    private fun Menu.addAppMenuContent() {
        item(messages["menu.app.about"] + " " + FxRadio.appName).action {
            menuViewModel.openAbout()
        }
        separator()
        item(messages["menu.app.server"]).action {
            menuViewModel.openAvailableServer()
        }
        separator()
    }
}