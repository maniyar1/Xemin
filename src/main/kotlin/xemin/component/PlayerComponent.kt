package xemin.component

import com.almasb.fxgl.entity.component.Component
import com.almasb.fxgl.entity.components.TransformComponent
import javafx.geometry.Point2D
import xemin.PlayerState
import xemin.SerializePoint

class PlayerComponent(initialPosition: Point2D): Component() {
    val playerState = PlayerState(SerializePoint(initialPosition.x, initialPosition.y), System.currentTimeMillis())

    lateinit var position: TransformComponent
    val speed = 10.0;

    fun moveLeft() {
        position.translateX(-speed)
        playerState.position = SerializePoint(position.position.x, position.position.y)
    }

    fun moveRight() {
        position.translateX(speed)
        playerState.position = SerializePoint(position.position.x, position.position.y)
    }

    fun moveUp() {
        position.translateY(-speed)
        playerState.position = SerializePoint(position.position.x, position.position.y)
    }

    fun moveDown() {
        position.translateY(speed)
        playerState.position = SerializePoint(position.position.x, position.position.y)
    }
}