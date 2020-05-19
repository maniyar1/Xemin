package xemin.players

import kotlinx.serialization.Serializable

@Serializable
data class PlayerState(var position: XeminPoint, var velocity: XeminPoint, val uniqueIdentifier: Int, var time: Long, var inputs: Inputs, var serverReset: Boolean) {
    fun clone(newPlayerState: PlayerState) {
        this.position = newPlayerState.position
        this.velocity = newPlayerState.velocity
        this.time = newPlayerState.time
        this.inputs = newPlayerState.inputs
        this.serverReset = newPlayerState.serverReset
    }

}

@Serializable
data class XeminPoint(var x: Double, var y: Double)

@Serializable
data class Inputs(var isUpPressed: Boolean, var isDownPressed: Boolean, var isRightPressed: Boolean, var isLeftPressed: Boolean)

@Serializable
data class FullState(var players: Array<PlayerState>) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FullState

        if (!players.contentEquals(other.players)) return false

        return true
    }

    override fun hashCode(): Int {
        return players.contentHashCode()
    }
}

