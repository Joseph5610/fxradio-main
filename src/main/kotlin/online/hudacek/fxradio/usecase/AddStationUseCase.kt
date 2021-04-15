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

import io.reactivex.Single
import mu.KotlinLogging
import online.hudacek.fxradio.api.model.AddStation
import online.hudacek.fxradio.api.model.AddedStation
import online.hudacek.fxradio.utils.applySchedulers

private val logger = KotlinLogging.logger {}

class AddStationUseCase : UseCase<AddStation, Single<AddedStation>>() {

    override fun execute(input: AddStation): Single<AddedStation> = apiService
            .add(input)
            .compose(applySchedulers())
            .onErrorResumeNext { Single.just(AddedStation(false, it.localizedMessage, "0")) }
            .doOnError { logger.error { "Error while adding station: ${it.message} " } }
}