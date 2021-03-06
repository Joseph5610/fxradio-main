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

package online.hudacek.fxradio.viewmodel

import javafx.beans.property.ListProperty
import javafx.beans.property.StringProperty
import javafx.collections.ObservableList
import online.hudacek.fxradio.Config
import online.hudacek.fxradio.usecase.GetServersUseCase
import online.hudacek.fxradio.utils.Properties
import online.hudacek.fxradio.utils.save
import tornadofx.fail
import tornadofx.observableListOf
import tornadofx.property
import tornadofx.success

sealed class ServersState(val key: String = "") {
    object Loading : ServersState("loading")
    data class Fetched(val servers: ObservableList<String>) : ServersState()
    object NoServersAvailable : ServersState("servers.notAvailable")
    data class Error(val cause: String) : ServersState("servers.error")
}

class Servers(selectedServer: String = Config.API.fallbackApiServerURL,
              availableServers: ObservableList<String> = observableListOf()) {
    var selected: String by property(selectedServer)
    var servers: ObservableList<String> by property(availableServers)
}

/**
 * Holds available and selected API servers
 * Item is set in [online.hudacek.fxradio.api.StationsApi.Companion]
 *
 * Search for available servers is performed only on first start of the app or when opening
 * [online.hudacek.fxradio.ui.modal.ServersFragment]
 */
class ServersViewModel : BaseStateViewModel<Servers, ServersState>(Servers()) {

    private val getServersUseCase: GetServersUseCase by inject()

    val serversProperty = bind(Servers::servers) as ListProperty<String>
    val selectedProperty = bind(Servers::selected) as StringProperty

    /**
     * Perform async DNS lookup to find working API servers
     */
    fun fetchServers() {
        stateProperty.value = ServersState.Loading
        getServersUseCase.execute(Unit) success {
            if (it.isNullOrEmpty()) {
                stateProperty.value = ServersState.NoServersAvailable
            } else {
                stateProperty.value = ServersState.Fetched(it)
            }
        } fail {
            stateProperty.value = ServersState.Error(it.localizedMessage)
        }
    }

    override fun onNewState(newState: ServersState) {
        if (newState is ServersState.Fetched) {
            item = Servers(availableServers = newState.servers)
        }
    }

    //Save selected server to app.properties on commit
    override fun onCommit() = Properties.ApiServer.save(selectedProperty.value)
}