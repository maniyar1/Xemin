package xemin.component

import com.almasb.fxgl.dsl.getInput
import com.almasb.fxgl.dsl.play
import com.almasb.fxgl.entity.component.Component
import com.almasb.fxgl.entity.components.TransformComponent
import com.almasb.fxgl.input.UserAction
import javafx.geometry.Point2D
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import xemin.Constants
import xemin.Constants.speed
import xemin.players.Inputs
import xemin.players.PlayerState
import xemin.players.XeminPoint

class PlayerComponent(initialPosition: Point2D, var uniqueIdentifier: Int): Component() {

    val inputs = Inputs(isUpPressed = false, isDownPressed = false, isRightPressed = false, isLeftPressed = false)
    val playerState = PlayerState(XeminPoint(initialPosition.x, initialPosition.y), XeminPoint(0.0, 0.0), uniqueIdentifier, System.currentTimeMillis(), inputs, false)

    lateinit var position: TransformComponent

    fun moveLeft() {
        playerState.velocity.x = -speed
    }

    fun moveRight() {
        playerState.velocity.x = speed
    }

    fun moveUp() {
        playerState.velocity.y = -speed
    }

    fun moveDown() {
        playerState.velocity.y = speed
    }

    override fun onUpdate(tpf: Double) {
        position.translate(playerState.velocity.x, playerState.velocity.y)
        playerState.position = XeminPoint(position.position.x, position.position.y)
        playerState.time = System.currentTimeMillis()
    }
}