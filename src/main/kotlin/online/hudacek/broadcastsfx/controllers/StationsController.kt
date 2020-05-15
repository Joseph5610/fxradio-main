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

import com.github.thomasnield.rxkotlinfx.observeOnFx
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import online.hudacek.broadcastsfx.StationsApi
import online.hudacek.broadcastsfx.events.LibraryType
import online.hudacek.broadcastsfx.model.StationHistoryModel
import online.hudacek.broadcastsfx.model.rest.CountriesBody
import online.hudacek.broadcastsfx.model.rest.SearchBody
import online.hudacek.broadcastsfx.model.rest.Station
import online.hudacek.broadcastsfx.views.StationsView
import tornadofx.*

class StationsController : Controller() {

    private val stationsView: StationsView by inject()
    private val stationsHistory: StationHistoryModel by inject()

    private val stationsApi: StationsApi
        get() {
            return StationsApi.client
        }

    fun getFavourites() {
        Station.favourites()
                .observeOnFx()
                .subscribeOn(Schedulers.io())
                .subscribe({
                    if (it.isEmpty()) {
                        stationsView.showNoResults()
                    } else {
                        stationsView.showDataGrid(it)
                    }
                }, {
                    stationsView.showError(it)
                })
        stationsView.setContentName(LibraryType.Favourites)
    }

    fun getStationsByCountry(country: String): Disposable = stationsApi
            .getStationsByCountry(CountriesBody(), country)
            .subscribeOn(Schedulers.io())
            .observeOnFx()
            .subscribe({ result ->
                stationsView.setContentName(LibraryType.Country, country)
                stationsView.showDataGrid(result)
            }, {
                stationsView.showError(it)
            })

    fun searchStations(name: String): Disposable = stationsApi
            .searchStationByName(SearchBody(name))
            .subscribeOn(Schedulers.io())
            .observeOnFx()
            .subscribe({ result ->
                if (result.isEmpty()) {
                    stationsView.showNoResults(name)
                } else {
                    stationsView.setContentName(LibraryType.Search, name)
                    stationsView.showDataGrid(result)
                }
            }, {
                stationsView.showError(it)
            })

    fun getHistory() {
        val historyList = stationsHistory.stations.value.distinct()
        if (historyList.isEmpty()) {
            stationsView.showNoResults()
        } else {
            stationsView.showDataGrid(historyList)
        }
        stationsView.setContentName(LibraryType.History)
    }

    fun getTopStations(): Disposable = stationsApi
            .getTopStations()
            .subscribeOn(Schedulers.io())
            .observeOnFx()
            .subscribe({ result ->
                stationsView.setContentName(LibraryType.TopStations)
                stationsView.showDataGrid(result)
            }, {
                stationsView.showError(it)
            })
}