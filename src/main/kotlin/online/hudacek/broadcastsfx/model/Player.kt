package online.hudacek.broadcastsfx.model

import online.hudacek.broadcastsfx.Config
import online.hudacek.broadcastsfx.events.PlayerType
import online.hudacek.broadcastsfx.model.rest.Station
import tornadofx.ItemViewModel
import tornadofx.onChange
import tornadofx.property

class Player(animate: Boolean = true, station: Station? = null,
             playerType: PlayerType) {

    var animate: Boolean by property(animate)
    var actualStation: Station by property(station)
    var playerType: PlayerType by property(playerType)
}

class PlayerModel : ItemViewModel<Player>() {

    private val stationHistory: StationHistoryModel by inject()

    val animate = bind(Player::animate)
    val station = bind(Player::actualStation)
    val playerType = bind(Player::playerType)

    init {
        station.onChange {
            if (it != null) {
                stationHistory.add(it)
            }
        }
    }

    override fun onCommit() {
        //Save API server
        with(app.config) {
            set(Config.playerAnimate to animate.value)
            set(Config.playerType to playerType.value)
            save()
        }
    }
}
