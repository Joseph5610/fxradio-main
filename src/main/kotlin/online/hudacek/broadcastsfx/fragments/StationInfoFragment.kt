package online.hudacek.broadcastsfx.fragments

import com.sun.deploy.uitoolkit.impl.fx.HostServicesFactory
import javafx.geometry.Pos
import javafx.scene.effect.DropShadow
import javafx.scene.paint.Color
import online.hudacek.broadcastsfx.model.CurrentStationModel
import online.hudacek.broadcastsfx.model.rest.Station
import online.hudacek.broadcastsfx.styles.Styles
import online.hudacek.broadcastsfx.ui.createImage
import tornadofx.*
import tornadofx.controlsfx.statusbar

class StationInfoFragment(val station: Station? = null, showImage: Boolean = true) : Fragment() {

    private val currentStation: CurrentStationModel by inject()

    private val showStation: Station = station ?: currentStation.station.value

    override fun onBeforeShow() {
        currentStage?.opacity = 0.85
    }

    override val root = vbox {
        setPrefSize(300.0, 300.0)
        showStation.let {
            title = it.name

            val tagsList = it.tags.split(",")
                    .map { tag -> tag.trim() }
                    .filter { tag -> tag.isNotEmpty() }

            val codecBitrateInfo = it.codec + " (" + it.bitrate + ")"

            val list = observableListOf(
                    codecBitrateInfo,
                    it.country,
                    it.language)

            if (showImage) {
                vbox(alignment = Pos.CENTER) {
                    paddingAll = 10.0
                    imageview {
                        createImage(it)
                        effect = DropShadow(30.0, Color.LIGHTGRAY)
                        fitHeight = 100.0
                        fitHeight = 100.0
                        isPreserveRatio = true
                    }

                    /*
                    rating(0, 5) {
                        paddingTop = 10.0
                        maxHeight = 15.0
                    }*/
                }
            }

            if (tagsList.isNotEmpty()) {
                flowpane {
                    hgap = 5.0
                    vgap = 5.0
                    alignment = Pos.CENTER
                    paddingAll = 5.0
                    tagsList.forEach { tag ->
                        label(tag) {
                            addClass(Styles.tag)
                            addClass(Styles.grayLabel)
                        }
                    }
                }
            }

            listview(list)
            statusbar {
                rightItems.add(
                        hyperlink(it.homepage) {
                            action {
                                val hostServices = HostServicesFactory.getInstance(app)
                                hostServices.showDocument(it.homepage)
                            }
                        }
                )
            }
        }
    }
}