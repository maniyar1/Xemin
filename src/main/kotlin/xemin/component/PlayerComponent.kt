package xemin.component

import com.almasb.fxgl.entity.component.Component
import com.almasb.fxgl.entity.components.TransformComponent
import javafx.geometry.Point2D
import xemin.Inputs
import xemin.PlayerState
import xemin.XeminPoint

class PlayerComponent(initialPosition: Point2D, var uniqueIdentifier: Int): Component() {
    val inputs = Inputs(isUpPressed = false, isDownPressed = false, isRightPressed = false, isLeftPressed = false)
    val playerState = PlayerState(XeminPoint(initialPosition.x, initialPosition.y), uniqueIdentifier, System.currentTimeMillis(), inputs, false)

    lateinit var position: TransformComponent
    val speed = 10.0;

    fun moveLeft() {
        position.translateX(-speed)
        playerState.position = XeminPoint(position.position.x, position.position.y)
        playerState.inputs.isLeftPressed = true
    }

    fun moveRight() {
        position.translateX(speed)
        playerState.position = XeminPoint(position.position.x, position.position.y)
        playerState.inputs.isRightPressed = true
    }

    fun moveUp() {
        position.translateY(-speed)
        playerState.position = XeminPoint(position.position.x, position.position.y)
        playerState.inputs.isUpPressed = true
    }

    fun moveDown() {
        position.translateY(speed)
        playerState.position = XeminPoint(position.position.x, position.position.y)
        playerState.inputs.isDownPressed = true
    }
}