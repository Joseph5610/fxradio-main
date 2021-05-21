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

package online.hudacek.fxradio.usecase

import io.reactivex.disposables.Disposable
import javafx.beans.property.ListProperty
import mu.KotlinLogging
import online.hudacek.fxradio.api.stations.model.CountriesBody
import online.hudacek.fxradio.api.stations.model.Country
import online.hudacek.fxradio.api.stations.model.isValid

private val logger = KotlinLogging.logger {}

/**
 * Gets list of valid country names and count of stations in it
 */
class GetCountriesUseCase : BaseUseCase<ListProperty<Country>, Disposable>() {

    override fun execute(input: ListProperty<Country>): Disposable = apiService
            .getCountries(CountriesBody())
            .compose(applySchedulers())
            .flattenAsObservable { it }
            .filter { it.isValid }
            .doOnError { logger.error(it) { "Exception while getting Countries!" } }
            .subscribe {
                input.add(it)
            }
}