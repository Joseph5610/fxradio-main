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

import javafx.beans.property.ObjectProperty
import online.hudacek.fxradio.utils.Properties
import online.hudacek.fxradio.utils.Property
import online.hudacek.fxradio.utils.property
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.core.config.Configurator
import tornadofx.ItemViewModel
import tornadofx.property

class Log(level: Level = Level.valueOf(property(Properties.LOG_LEVEL, "INFO"))) {
    var level: Level by property(level)
}

/**
 * Keeps information about current logging level chosen in UI
 * Used in [online.hudacek.fxradio.ui.view.menu.MenuBarView]
 */
class LogViewModel : ItemViewModel<Log>(Log()) {
    val levelProperty = bind(Log::level) as ObjectProperty

    override fun onCommit() {
        //Set Current Logger Level
        Configurator.setAllLevels(LogManager.getRootLogger().name, levelProperty.value)

        //Save it
        Property(Properties.LOG_LEVEL).save(levelProperty.value)
    }
}