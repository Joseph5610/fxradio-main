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

package online.hudacek.fxradio.ui.modal

import griffon.javafx.support.flagicons.FlagIcon
import javafx.geometry.Pos
import javafx.scene.text.TextAlignment
import online.hudacek.fxradio.events.AppEvent
import online.hudacek.fxradio.events.data.AppNotification
import online.hudacek.fxradio.ui.showWhen
import online.hudacek.fxradio.ui.style.Styles
import online.hudacek.fxradio.ui.viewmodel.ServersViewModel
import online.hudacek.fxradio.ui.viewmodel.ServersViewState
import org.controlsfx.glyphfont.FontAwesome
import tornadofx.*

/**
 * Fragment that shows UI for selection of radio-browser API server
 * User is allowed to select a different server from the list.
 *
 * Selected server is then saved into the app.config property file
 * and used on the next start of the app
 */
class AvailableServersFragment : Fragment() {
    private val appEvent: AppEvent by inject()
    private val viewModel: ServersViewModel by inject()

    private val labelTextProperty = viewModel.viewStateProperty.stringBinding {
        when (it) {
            ServersViewState.Loading -> {
                messages["loading"]
            }
            ServersViewState.Error -> {
                messages["servers.notAvailable"]
            }
            else -> {
                ""
            }
        }
    }

    override fun onDock() {
        viewModel.viewStateProperty.value = ServersViewState.Loading
    }

    override fun onUndock() {
        if (viewModel.selectedProperty.isDirty) {
            viewModel.rollback(viewModel.selectedProperty)
        }
    }

    override val root = vbox {
        title = messages["menu.app.server"]
        paddingAll = 10.0
        setPrefSize(300.0, 250.0)

        vbox {
            vbox(alignment = Pos.CENTER) {
                label(messages["servers.title"]) {
                    paddingBottom = 15.0
                    addClass(Styles.header)
                }

                text(messages["servers.restartNeeded"]) {
                    paddingAll = 5.0
                    wrappingWidth = 270.0
                    textAlignment = TextAlignment.CENTER
                }
            }

            vbox {
                prefHeight = 120.0
                alignment = Pos.BASELINE_CENTER
                label(labelTextProperty) {
                    paddingAll = 15.0
                }

                showWhen {
                    viewModel.viewStateProperty.isNotEqualTo(ServersViewState.Loaded)
                }
            }

            listview(viewModel.serversProperty) {
                bindSelected(viewModel.selectedProperty)
                cellFormat {
                    graphic = hbox(5) {
                        prefHeight = 19.0
                        alignment = Pos.CENTER_LEFT

                        imageview {
                            image = FlagIcon(it.substring(0, 2))
                        }

                        label(messages["servers.selected"]) {
                            showWhen {
                                //look for the value of backing field
                                booleanBinding(viewModel.item.selected) {
                                    this == it
                                }
                            }
                            addClass(Styles.libraryListItemTag)
                        }
                    }
                    text = it
                    addClass(Styles.libraryListItem)
                }

                showWhen {
                    viewModel.viewStateProperty.isEqualTo(ServersViewState.Loaded)
                }

                addClass(Styles.libraryListView)
            }
        }

        hbox(10) {
            alignment = Pos.CENTER_RIGHT
            paddingAll = 10.0

            button(messages["servers.reload"]) {
                action {
                    viewModel.viewStateProperty.value = ServersViewState.Loading
                }
            }
            button(messages["save"]) {
                enableWhen(viewModel.selectedProperty.isNotNull)
                isDefaultButton = true
                action {
                    //Save the server in the app.config property file
                    //Close the fragment after successful save
                    viewModel.commit {
                        appEvent.appNotification.onNext(AppNotification(messages["server.save.ok"], FontAwesome.Glyph.CHECK))
                        close()
                    }
                }
                addClass(Styles.primaryButton)
            }
        }
        addClass(Styles.backgroundWhiteSmoke)
    }
}