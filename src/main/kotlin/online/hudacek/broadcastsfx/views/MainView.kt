package online.hudacek.broadcastsfx.views

import javafx.geometry.Orientation
import javafx.scene.image.Image
import javafx.scene.layout.Priority
import online.hudacek.broadcastsfx.About
import online.hudacek.broadcastsfx.controllers.MainController
import online.hudacek.broadcastsfx.views.leftpane.LeftPaneView
import online.hudacek.broadcastsfx.views.rightpane.PlayerView
import online.hudacek.broadcastsfx.views.rightpane.StationsView
import org.controlsfx.control.NotificationPane
import tornadofx.*
import tornadofx.controlsfx.content
import tornadofx.controlsfx.notificationPane

class MainView : View() {

    private val controller: MainController by inject()

    private val playerView: PlayerView by inject()
    private val leftPaneView: LeftPaneView by inject()
    private val stationsView: StationsView by inject()

    var notification: NotificationPane by singleAssign()

    init {
        title = About.appName
        setStageIcon(Image(About.appIcon))
    }

    private val rightPane = vbox {
        add(playerView)
        add(stationsView)
    }

    override fun onDock() {
        currentWindow?.setOnCloseRequest {
            controller.cancelMediaPlaying()
        }
    }

    override val root = vbox {
        setPrefSize(800.0, 600.0)
        vgrow = Priority.ALWAYS
        add(MenuBarView::class)
        notificationPane {
            notification = this
            isShowFromTop = true

            content {
                splitpane(Orientation.HORIZONTAL, leftPaneView.root, rightPane) {
                    prefWidthProperty().bind(this@vbox.widthProperty())
                    prefHeightProperty().bind(this@vbox.heightProperty())
                    setDividerPositions(0.3)
                }
            }
        }
    }
}