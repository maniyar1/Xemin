package xemin

import com.almasb.fxgl.app.GameApplication
import com.almasb.fxgl.app.GameSettings
import com.almasb.fxgl.dsl.*
import com.almasb.fxgl.entity.Entity
import com.almasb.fxgl.entity.SpawnData
import com.almasb.fxgl.input.UserAction
import com.almasb.fxgl.physics.CollisionHandler
import javafx.geometry.Point2D
import javafx.scene.input.KeyCode
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import xemin.component.PlayerComponent
import xemin.players.PlayerFactory
import xemin.players.XeminPoint

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
//        GlobalScope.launch {
//            Server.connectionEstablisher()
//        }
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

    override fun initPhysics() {
        getPhysicsWorld().addCollisionHandler(object : CollisionHandler(EntityType.PLAYER, EntityType.PLAYER) {
            override fun onCollisionBegin(a: Entity?, b: Entity?) {
                if (b != null && a != null) {
                    if (a.getComponent(PlayerComponent::class.java).speed < b.getComponent(PlayerComponent::class.java).speed) {
                        a.getComponent(PlayerComponent::class.java).lastDeathTime = System.currentTimeMillis()
                        a.getComponent(PlayerComponent::class.java).position.position = Point2D(0.0, 0.0)
                        a.getComponent(PlayerComponent::class.java).playerState.position = XeminPoint(0.0, 0.0)
                    } else {
                        b.getComponent(PlayerComponent::class.java).lastDeathTime = System.currentTimeMillis()
                        b.getComponent(PlayerComponent::class.java).position.position = Point2D(0.0, 0.0)
                        b.getComponent(PlayerComponent::class.java).playerState.position = XeminPoint(0.0, 0.0)
                    }
                }
            }
        })
    }

    override fun onUpdate(tpf: Double) {
        Client.entitiesToAddState.iterator().forEach {
            getGameWorld().addEntity(it)
        }
        Client.entitiesToAddState.clear()
    }

    override fun initInput() {
        getInput().addAction(object : UserAction("Move Left") {
            override fun onAction() {
                playerComponent.playerState.inputs.isLeftPressed = true
                playerComponent.moveLeft()
            }

            override fun onActionEnd() {
                playerComponent.playerState.inputs.isLeftPressed = false
                playerComponent.playerState.velocity.x = 0.0
            }
        }, KeyCode.A)
        getInput().addAction(object : UserAction("Move Right") {
            override fun onAction() {
                playerComponent.playerState.inputs.isRightPressed = true
                playerComponent.moveRight()
            }

            override fun onActionEnd() {
                playerComponent.playerState.inputs.isRightPressed = false
                playerComponent.playerState.velocity.x = 0.0
            }
        }, KeyCode.D)
        getInput().addAction(object : UserAction("Move Up") {
            override fun onAction() {
                playerComponent.playerState.inputs.isUpPressed = true
                playerComponent.moveUp()
            }

            override fun onActionEnd() {
                playerComponent.playerState.inputs.isUpPressed = false
                playerComponent.playerState.velocity.y = 0.0
            }
        }, KeyCode.W)
        getInput().addAction(object : UserAction("Move Down") {
            override fun onAction() {
                playerComponent.playerState.inputs.isDownPressed = true
                playerComponent.moveDown()
            }

            override fun onActionEnd() {
                playerComponent.playerState.inputs.isDownPressed = false
                playerComponent.playerState.velocity.y = 0.0
            }
        }, KeyCode.S)
    }
}

