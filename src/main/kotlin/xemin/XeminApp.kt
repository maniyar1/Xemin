package xemin

import com.almasb.fxgl.app.GameApplication
import com.almasb.fxgl.app.GameSettings
import com.almasb.fxgl.dsl.*
import com.almasb.fxgl.entity.Entity
import com.almasb.fxgl.entity.SpawnData
import javafx.scene.input.KeyCode
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import xemin.component.PlayerComponent

class XeminApp : GameApplication() {
    private lateinit var mainPlayer: Entity
    private lateinit var playerComponent: PlayerComponent

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(XeminApp::class.java, args)
        }
    }

    override fun initSettings(settings: GameSettings) {
        with(settings) {
            width = 1920
            height = 1080
            title = "Basic Game App"
            version = "0.1"
            // other settings
        }
    }

    override fun initGame() {
        getGameWorld().addEntityFactory(PlayerFactory)

        initScreenBounds()
        initPlayer()
        GlobalScope.launch {
            Server.run()
        }
        GlobalScope.launch {
            Client.run(playerComponent.playerState, playerComponent.uniqueIdentifier)
        }
    }

    private fun initScreenBounds() {
        entityBuilder().buildScreenBoundsAndAttach(100.0)
    }

    private fun initPlayer() {
        mainPlayer = spawn("player", SpawnData(50.0, getAppHeight() - 300.0).put("id", random(0, 100000)) )
        playerComponent = mainPlayer.getComponent(PlayerComponent::class.java)
    }

    override fun onUpdate(tpf: Double) {
        Client.entitiesToAddState.iterator().forEach {
            getGameWorld().addEntity(it)
        }
        Client.entitiesToAddState.clear()
    }

    override fun initInput() {
        onKey(KeyCode.W, { mainPlayer.getComponent(PlayerComponent::class.java).moveUp() })
        onKey(KeyCode.A, { mainPlayer.getComponent(PlayerComponent::class.java).moveLeft() })
        onKey(KeyCode.S, { mainPlayer.getComponent(PlayerComponent::class.java).moveDown() })
        onKey(KeyCode.D, { mainPlayer.getComponent(PlayerComponent::class.java).moveRight() })
    }
}

