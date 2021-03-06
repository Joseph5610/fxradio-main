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

package online.hudacek.fxradio

import javafx.stage.Stage
import online.hudacek.fxradio.api.HttpClient
import online.hudacek.fxradio.api.StationsApi
import online.hudacek.fxradio.ui.CustomErrorHandler
import online.hudacek.fxradio.ui.style.Styles
import online.hudacek.fxradio.ui.style.StylesDark
import online.hudacek.fxradio.ui.view.MainView
import online.hudacek.fxradio.utils.Properties
import online.hudacek.fxradio.utils.macos.MacUtils
import online.hudacek.fxradio.utils.saveProperties
import online.hudacek.fxradio.viewmodel.PlayerViewModel
import tornadofx.*
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.reflect.KClass

/**
 * Load app in Dark Mode
 */
class FxRadioDark(override var useDarkModeStyle: Boolean = true) : FxRadio(StylesDark::class)

/**
 * Load app in Light Mode
 */
class FxRadioLight(override var useDarkModeStyle: Boolean = false) : FxRadio(Styles::class)

/**
 * Load the app with provided [stylesheet] class
 */
open class FxRadio(stylesheet: KClass<out Stylesheet>) : App(MainView::class, stylesheet) {

    /**
     * override app.config path to $user.home/fxradio
     */
    override val configBasePath: Path = Paths.get(Config.Paths.confDirPath)

    open var useDarkModeStyle: Boolean by singleAssign()

    override fun start(stage: Stage) {
        Thread.setDefaultUncaughtExceptionHandler(CustomErrorHandler())
        with(stage) {
            minWidth = 600.0
            minHeight = 400.0

            //Setup window location on screen
            config.double(Properties.WindowWidth.key)?.let {
                width = it
            }
            config.double(Properties.WindowHeight.key)?.let {
                height = it
            }
            config.double(Properties.WindowX.key)?.let {
                x = it
            }
            config.double(Properties.WindowY.key)?.let {
                y = it
            }
            super.start(this)
        }
    }

    override fun stop() {
        //Save last used window width/height on close of the app to use it on next start
        saveProperties(mapOf(
                Properties.WindowWidth to primaryStage.width,
                Properties.WindowHeight to primaryStage.height,
                Properties.WindowX to primaryStage.x,
                Properties.WindowY to primaryStage.y
        ))
        super.stop()
    }

    /**
     * Basic info about the app
     */
    companion object : Component() {

        private val playerViewModel: PlayerViewModel by inject()

        const val appName = "FXRadio"
        const val appDesc = "Internet radio directory"
        const val appUrl = "https://hudacek.online/fxradio/"
        const val author = "hudacek.online"
        const val copyright = "Copyright (c) 2020"

        val isAppInDarkMode by lazy { (app as FxRadio).useDarkModeStyle }

        /**
         * Gets version from jar MANIFEST.MF file
         * On failure (e.g if app is not run from the jar file), returns the "0.0-DEVELOPMENT" value
         */
        val version: String by lazy {
            FxRadio::class.java.getPackage().implementationVersion ?: "0.0-DEVELOPMENT"
        }

        //Should be called when on every place that is closing the app
        fun shutdownApp() {
            playerViewModel.releasePlayer()
            StationsApi.client.shutdown()
            HttpClient.shutdown()
        }
    }
}

private val isSystemDarkMode: Boolean = if (MacUtils.isMac) {
    MacUtils.isSystemDarkMode
} else {
    false
}

/**
 * Main starting method for the App
 */
fun main(args: Array<String>) {
    if (Config.Flags.darkStylesEnabled && isSystemDarkMode) {
        launch<FxRadioDark>(args)
    } else {
        launch<FxRadioLight>(args)
    }
}
