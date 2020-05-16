package xemin

import javafx.geometry.Point2D
import kotlinx.serialization.Serializable

@Serializable
data class PlayerState(var position: SerializePoint, var time: Long)

@Serializable
data class SerializePoint(var x: Double, var y: Double)

