package online.hudacek.broadcastsfx.views

import de.codecentric.centerdevice.MenuToolkit
import javafx.scene.control.*
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import online.hudacek.broadcastsfx.About
import online.hudacek.broadcastsfx.controllers.MenuBarController
import online.hudacek.broadcastsfx.events.PlaybackChangeEvent
import online.hudacek.broadcastsfx.events.PlayerType
import online.hudacek.broadcastsfx.events.PlayerTypeChange
import online.hudacek.broadcastsfx.events.PlayingStatus
import online.hudacek.broadcastsfx.model.CurrentStation
import online.hudacek.broadcastsfx.model.StationHistoryViewModel
import online.hudacek.broadcastsfx.model.StationViewModel
import online.hudacek.broadcastsfx.utils.Utils
import tornadofx.*
import java.util.*

class MenuBarView : View() {

    private val controller: MenuBarController by inject()

    private val currentStation: StationViewModel by inject()
    private val stationHistory: StationHistoryViewModel by inject()

    private var playerPlay: MenuItem by singleAssign()
    private var playerStop: MenuItem by singleAssign()
    private var playerCheck: CheckMenuItem by singleAssign()

    private val keyCodePlay = KeyCodeCombination(KeyCode.P, KeyCombination.CONTROL_DOWN)
    private val keyCodeStop = KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN)
    private val keyCodeInfo = KeyCodeCombination(KeyCode.I, KeyCombination.CONTROL_DOWN)

    private var currentPlayerType = controller.mediaPlayer.playerType

    private val historyMenu = Menu(messages["menu.history"]).apply {
        items.bind(stationHistory.stations.value) {
            MenuItem(it.name).apply {
                action {
                    currentStation.item = CurrentStation(it)
                }
            }
        }
    }

    private val stationMenu = Menu(messages["menu.station"]).apply {
        visibleProperty().bind(booleanBinding(currentStation.station) {
            value != null
        })

        item(messages["menu.station.info"], keyCodeInfo).action {
            controller.openStationInfo()
        }

        item(messages["menu.station.report"]) {
            isDisable = true
        }
    }

    private val viewMenu = Menu(messages["menu.view"]).apply {
        item(messages["menu.view.stats"]).action {
            controller.openStats()
        }
    }

    private val playerMenu = Menu(messages["menu.player.controls"]).apply {
        playerPlay = item(messages["menu.player.start"], keyCodePlay) {
            visibleProperty().bind(booleanBinding(currentStation.station) {
                value != null
            })

            action {
                fire(PlaybackChangeEvent(PlayingStatus.Playing))
            }
        }

        playerStop = item(messages["menu.player.stop"], keyCodeStop) {
            visibleProperty().bind(booleanBinding(currentStation.station) {
                value != null
            })
            action {
                fire(PlaybackChangeEvent(PlayingStatus.Stopped))
            }
        }

        playerCheck = checkmenuitem(messages["menu.player.switch"]) {
            isSelected = currentPlayerType == PlayerType.Native
            action {
                if (currentPlayerType == PlayerType.Native) {
                    fire(PlayerTypeChange(PlayerType.VLC))
                } else {
                    fire(PlayerTypeChange(PlayerType.Native))
                }
                fire(PlaybackChangeEvent(PlayingStatus.Stopped))
            }
        }
    }

    init {
        subscribe<PlayerTypeChange> { event ->
            with(event) {
                currentPlayerType = changedPlayerType
                playerCheck.isSelected = changedPlayerType == PlayerType.Native
            }
        }
    }

    override val root = if (Utils.isMacOs) {
        platformMenuBar()
    } else {
        defaultMenuBar()
    }

    private fun defaultMenuBar() = menubar {
        menu(About.appName) {
            item(messages["menu.app.about"]).action {
                controller.openAbout()
            }
            item(messages["menu.app.server"]).action {
                controller.openServerSelect()
            }
            item(messages["menu.app.attributions"]).action {
                controller.openAttributions()
            }
            item(messages["menu.app.quit"]).action {
                controller.closeApp(currentStage)
            }
        }
        menus.addAll(stationMenu, playerMenu, historyMenu, viewMenu)
    }

    /**
     * Platform specific menu bar working on OSX
     * used instead of in-app menubar
     */
    private fun platformMenuBar() = menubar {
        val tk = MenuToolkit.toolkit(Locale.getDefault())
        tk.setApplicationMenu(tk.createDefaultApplicationMenu(About.appName))

        useSystemMenuBarProperty().set(true)

        val appMenu = Menu(About.appName).apply {
            item(messages["menu.app.about"]).action {
                controller.openAbout()
            }
            separator()
            item(messages["menu.app.server"]).action {
                controller.openServerSelect()
            }
            item(messages["menu.app.attributions"]).action {
                controller.openAttributions()
            }
            separator()
            items.addAll(
                    tk.createHideMenuItem(About.appName), tk.createHideOthersMenuItem(), tk.createUnhideAllMenuItem(),
                    SeparatorMenuItem(), tk.createQuitMenuItem(About.appName))
        }

        val windowMenu = Menu("Window").apply {
            items.addAll(
                    tk.createMinimizeMenuItem(),
                    tk.createZoomMenuItem(),
                    tk.createCycleWindowsItem(),
                    SeparatorMenuItem(),
                    tk.createBringAllToFrontItem())
        }

        menus.addAll(stationMenu, playerMenu, historyMenu, viewMenu, windowMenu)

        tk.setApplicationMenu(appMenu)
        tk.autoAddWindowMenuItems(windowMenu)
        tk.setMenuBar(this)
    }
}