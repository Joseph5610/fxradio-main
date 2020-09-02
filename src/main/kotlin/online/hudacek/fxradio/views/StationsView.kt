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

package online.hudacek.fxradio.views

import io.reactivex.subjects.BehaviorSubject
import javafx.scene.layout.Priority
import online.hudacek.fxradio.controllers.StationsController
import online.hudacek.fxradio.events.LibraryRefreshConditionalEvent
import online.hudacek.fxradio.events.LibraryRefreshEvent
import online.hudacek.fxradio.events.LibraryType
import online.hudacek.fxradio.styles.Styles
import tornadofx.*

/**
 * Main view displaying grid of stations
 */
class StationsView : View() {

    private val controller: StationsController by inject()

    private val dataGridView: StationsDataGridView by inject()
    private val headerView: StationsHeaderView by inject()
    private val infoErrorView: StationsInfoErrorView by inject()

    private val currentLibType = BehaviorSubject.create<LibraryType>()
    private val currentLibParams = BehaviorSubject.create<String>()

    init {
        showLoading()

        currentLibType.startWith(LibraryType.TopStations)
        currentLibParams.startWith("")

        //Handle change of stations library
        subscribe<LibraryRefreshEvent> {
            currentLibParams.onNext(it.params)
            currentLibType.onNext(it.type)
            showLibraryType(it.type, it.params)
        }

        subscribe<LibraryRefreshConditionalEvent> {
            currentLibType.value?.let { value ->
                if (it.onlyWhen == value) {
                    showLibraryType(value, currentLibParams.value ?: "")
                }
            }
        }
    }

    override val root = vbox {
        addClass(Styles.backgroundWhite)
        vgrow = Priority.ALWAYS
        add(infoErrorView)
        add(headerView)
        add(dataGridView)
        dataGridView.root.fitToParentHeight()
    }

    fun showNoResults(queryString: String? = null) {
        headerView.hide()
        dataGridView.hide()
        infoErrorView.showNoResultsInfo(queryString)
    }

    fun showError() {
        headerView.hide()
        dataGridView.hide()
        infoErrorView.showError()
    }

    fun showStations() {
        infoErrorView.hide()
        headerView.show()
        dataGridView.show()
    }

    private fun handleSearch(query: String) {
        if (query.length > 2)
            controller.searchStations(query)
        else {
            headerView.hide()
            dataGridView.hide()
            infoErrorView.showShortSearchInfo()
        }
    }

    private fun showLoading() {
        headerView.hide()
        dataGridView.hide()
        infoErrorView.showLoading()
    }

    private fun showLibraryType(libraryType: LibraryType, params: String) {
        showLoading()
        when (libraryType) {
            LibraryType.Country -> controller.getStationsByCountry(params)
            LibraryType.Favourites -> controller.getFavourites()
            LibraryType.History -> controller.getHistory()
            LibraryType.Search -> handleSearch(params)
            else -> controller.getTopStations()
        }
    }
}