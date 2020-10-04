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

package online.hudacek.fxradio.utils

import javafx.beans.property.Property
import javafx.scene.control.Menu
import javafx.scene.control.MenuItem
import online.hudacek.fxradio.api.model.Station
import tornadofx.*

/**
 * Convenience methods for boolean bindings
 */
internal fun MenuItem.shouldBeVisible(station: Property<Station>) {
    visibleWhen(station.booleanBinding {
        it != null && it.isValid()
    })
}

internal fun MenuItem.shouldBeDisabled(station: Property<Station>) {
    disableWhen(station.booleanBinding {
        it == null || !it.isValid()
    })
}

internal fun menu(name: String, op: Menu.() -> Unit = {}): Menu {
    return Menu(name).apply {
        op.invoke(this)
    }
}
