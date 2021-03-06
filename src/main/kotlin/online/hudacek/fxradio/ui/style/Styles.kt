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

package online.hudacek.fxradio.ui.style

import javafx.scene.paint.Color
import javafx.scene.text.FontSmoothingType
import javafx.scene.text.FontWeight
import tornadofx.*

/**
 * Type-safe CSS classes used around the app
 */
class Styles : Stylesheet() {

    private val colors = Colors.values

    companion object {

        val grayLabel by cssclass()

        val playerMainBox by cssclass()
        val playerStationBox by cssclass()

        val playerControls by cssclass()

        val libraryListView by cssclass()
        val libraryListItem by cssclass()
        val historyListView by cssclass()
        val historyListItem by cssclass()
        val libraryListItemTag by cssclass()

        val primaryButton by cssclass()
        val coloredButton by cssclass()

        val header by cssclass()
        val subheader by cssclass()

        val tag by cssclass()
        val searchBoxLabel by cssclass()
        val primaryTextColor by cssclass()
        val boldText by cssclass()

        val backgroundWhite by cssclass()
        val backgroundWhiteSmoke by cssclass()
        val statusBar by cssclass()

        //For Text() elements
        val defaultTextColor by cssclass()

        val grayTextColor by cssclass()

        val noBorder by cssclass()
    }

    init {
        label {
            textFill = c(colors.label)
            fontSmoothingType = FontSmoothingType.GRAY
        }

        searchBoxLabel {
            padding = box(0.px, 2.px, 0.px, 7.px)
        }

        playerMainBox {
            padding = box(10.0.px, 0.0.px)
            borderColor += box(c(colors.transparent), c(colors.transparent), c(colors.backgroundBorder), c(colors.transparent))
        }

        playerStationBox {
            padding = box(3.0.px, 10.0.px)
            backgroundRadius += box(3.px)
            borderRadius += box(3.px)
            backgroundColor += c(colors.background)
            borderColor += box(c(colors.backgroundBorder))
            maxWidth = 260.px
            prefWidth = 260.px
        }

        tag {
            textFill = c(colors.label)
            padding = box(3.0.px, 10.0.px)
            backgroundRadius += box(6.px)
            borderRadius += box(6.px)
            backgroundColor += c(colors.background)
            borderColor += box(c(colors.backgroundBorder))
        }


        header {
            wrapText = true
            fontSize = 20.px
        }

        subheader {
            wrapText = true
            fontSize = 15.px
        }

        defaultTextColor {
            fill = c(colors.label)
        }

        grayTextColor {
            textFill = c(colors.grayLabel)
        }

        coloredButton {

        }

        playerControls {
            unsafe("-fx-padding", raw("0"))
            unsafe("-fx-background-insets", raw("0"))
            unsafe("-fx-background-color", raw("-fx-background"))
            unsafe("-fx-border-color", raw("transparent"))
            unsafe("-fx-faint-focus-color", raw("transparent"))
        }

        grayLabel {
            fontSize = 11.px
            textFill = c(colors.grayLabel)
        }

        primaryButton {
            backgroundColor += c(colors.primary)
            textFill = Color.WHITESMOKE
            and(hover) {
                backgroundColor += c(colors.primaryHover)
            }
        }

        primaryTextColor {
            textFill = c(colors.primary)
        }

        libraryListView {
            backgroundColor += Color.WHITESMOKE
            borderColor += box(Color.WHITESMOKE)
            unsafe("-fx-control-inner-background", Color.TRANSPARENT)
        }

        libraryListItem {
            fontSize = 12.px
            textFill = c(colors.label)
            backgroundColor += Color.WHITESMOKE
            backgroundRadius += box(6.px)
            borderRadius += box(6.px)
            and(selected) {
                backgroundColor += c(colors.background)
                borderColor += box(c(colors.backgroundBorder))
            }
            padding = box(5.px, 10.px, 5.px, 15.px)
        }

        libraryListItemTag {
            padding = box(2.px)
            textFill = c(colors.label)
            backgroundRadius += box(6.px)
            borderRadius += box(6.px)
            backgroundColor += c(colors.background)
            borderColor += box(c(colors.backgroundBorder))
        }

        historyListView {
            backgroundColor += Color.WHITE
            borderColor += box(Color.WHITE)
            unsafe("-fx-control-inner-background", Color.TRANSPARENT)
        }

        historyListItem {
            fontSize = 12.px
            textFill = c(colors.label)
            backgroundColor += Color.WHITE
            backgroundRadius += box(6.px)
            borderRadius += box(6.px)
            and(hover) {
                backgroundColor += c(colors.background)
                borderColor += box(c(colors.backgroundBorder))
            }
            and(selected) {
                backgroundColor += c(colors.background)
                borderColor += box(c(colors.backgroundBorder))
            }
            padding = box(5.px, 10.px, 5.px, 15.px)
        }

        boldText {
            fontWeight = FontWeight.BOLD
        }

        backgroundWhiteSmoke {
            backgroundColor += Color.WHITESMOKE
        }

        backgroundWhite {
            backgroundColor += Color.WHITE
        }

        // ===================================================================
        // Restyled default compontents
        // ===================================================================

        scrollBar {
            backgroundColor += c(colors.transparent)
            borderColor += box(c(colors.transparent))

            incrementButton {
                backgroundColor += c(colors.transparent)
                backgroundRadius += box(0.em)
                padding = box(0.px, 10.px, 0.px, 0.px)
            }

            decrementButton {
                backgroundColor += c(colors.transparent)
                backgroundRadius += box(0.em)
                padding = box(0.px, 10.px, 0.px, 0.px)
            }

            incrementArrow {
                shape = " "
                padding = box(0.px, 0.08.em)
            }

            decrementArrow {
                shape = " "
                padding = box(0.px, 0.08.em)
            }

            track {
                backgroundColor += c(colors.transparent)
                borderColor += box(c(colors.transparent))
                unsafe("-fx-background-radius", raw("0.0em"))
                unsafe("-fx-border-radius", raw("2.0em"))
            }

            thumb {
                unsafe("-fx-background-color", raw("derive(black,90.0%)"))
                unsafe("-fx-background-insets", raw("2.0, 0.0, 0.0"))
                unsafe("-fx-background-radius", raw("2.0em"))

                and(hover) {
                    unsafe("-fx-background-color", raw("derive(#4D4C4F,10.0%)"))
                    unsafe("-fx-background-insets", raw("2.0, 0.0, 0.0"))
                    unsafe("-fx-background-radius", raw("2.0em"))
                }
            }
        }

        splitPaneDivider {
            padding = box(0.px, 0.001.em)
            backgroundColor += Color.TRANSPARENT
            borderColor += box(c(colors.backgroundBorder))
        }

        datagrid {
            padding = box(0.px)
        }

        datagridCell {
            padding = box(0.px, 5.px, 5.px, 5.px)
            backgroundColor += c(colors.transparent)
            borderColor += box(c(colors.transparent))
            backgroundRadius += box(6.px)
            borderRadius += box(6.px)

            and(hover) {
                backgroundColor += c(colors.background)
                borderColor += box(c(colors.backgroundBorder))
            }

            and(selected) {
                backgroundColor += c(colors.background)
                borderColor += box(c(colors.backgroundBorder))
            }
        }

        textArea {
            fontFamily = "monospace"
            backgroundColor += c(colors.background)
            content {
                backgroundColor += c(colors.background)
            }
        }

        slider {
            track {
                prefHeight = 5.px
            }

            thumb {
                prefHeight = 14.px
                prefWidth = 14.px
            }
            and(focused) {
                thumb {
                    unsafe("-fx-background-color", raw("-fx-outer-border, -fx-inner-border, -fx-body-color"))
                }
            }
            and(selected) {
                thumb {
                    unsafe("-fx-color", raw("lightgray"))
                }
            }

            and(hover) {
                thumb {
                    unsafe("-fx-color", raw("lightgray"))
                }
            }
        }

        contextMenu {
            backgroundColor += Color.WHITESMOKE
            borderColor += box(c(colors.backgroundBorder))
            backgroundRadius += box(6.px)
            borderRadius += box(6.px)

            menuItem {
                backgroundRadius += box(6.px)
                borderRadius += box(6.px)

                and(hover) {
                    backgroundColor += c(colors.background)
                }
                and(focused) {
                    backgroundColor += c(colors.background)
                }
                and(selected) {
                    backgroundColor += c(colors.background)
                }
            }
        }

        menuBar {
            backgroundColor += c(colors.backgroundBorder)

            menu {
                backgroundColor += c(colors.background)

                and(hover) {
                    backgroundColor += c(colors.background)
                }
                and(focused) {
                    backgroundColor += c(colors.background)
                }
                and(selected) {
                    backgroundColor += c(colors.background)
                }
            }
        }

        progressIndicator {
            progressColor = c(colors.grayLabel)
        }

        statusBar {
            padding = box(5.px)
            borderRadius += box(0.px)
            borderWidth += box(1.px, 0.px, 0.px, 0.px)
            borderColor += box(c(colors.backgroundBorder), c(colors.transparent), c(colors.transparent), c(colors.transparent))
            unsafe("-fx-control-inner-background", Color.TRANSPARENT)
        }

        textField {
            backgroundRadius += box(6.px)
            borderRadius += box(6.px)
        }

        noBorder {
            backgroundInsets += box(0.px)
            padding = box(0.px)
        }
    }
}