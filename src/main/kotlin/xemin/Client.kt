package xemin

import com.almasb.fxgl.dsl.getGameWorld
import com.almasb.fxgl.dsl.play
import com.almasb.fxgl.entity.Entity
import com.almasb.fxgl.entity.SpawnData
import javafx.geometry.Point2D
import kotlinx.coroutines.*
import kotlinx.serialization.cbor.Cbor
import xemin.component.PlayerComponent
import xemin.players.FullState
import xemin.players.PlayerFactory
import xemin.players.PlayerState
import java.io.DataInputStream
import java.lang.Math.abs
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.Socket
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.coroutines.CoroutineContext

object Client : CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default

    val entitiesToAddState = ConcurrentLinkedQueue<Entity>()

    val socket = DatagramSocket()
    val address = InetAddress.getByName("localhost")
    val cbor = Cbor()

    fun run(playerState: PlayerState, mainCharacterId: Int) {

        val client = Socket(Constants.address, 9999)
        val port = DataInputStream(client.getInputStream()).readInt()
        println("Port is $port (client)")
        client.close()
        while (true) {
            val encodedPlayerData = cbor.dump(PlayerState.serializer(), playerState)
            val sendPacket = DatagramPacket(encodedPlayerData, encodedPlayerData.size, address, port)
            socket.send(sendPacket)

            val buffer = ByteArray(1400)
            val recPacket = DatagramPacket(buffer, buffer.size, address, port)
            socket.receive(recPacket)
            val playerStates = cbor.load(FullState.serializer(), recPacket.data)
            playerStates.players.forEach { serverState ->
                var found = false
                getGameWorld().entitiesCopy.iterator().forEach { entity ->
                    if (entity.type == EntityType.PLAYER) {
                        val existingEntityComponent = entity.getComponent(PlayerComponent::class.java)
                        if (existingEntityComponent.uniqueIdentifier == serverState.uniqueIdentifier) {
                            found = true
                            launch {
                                controlPlayerFromState(entity, serverState, mainCharacterId)
                            }
                        }
                    }
                }
                if (!found) {
                    launch {
                        val newEntity = PlayerFactory.newPlayer(SpawnData(serverState.position.x, serverState.position.y).put("id", serverState.uniqueIdentifier))
                        entitiesToAddState.add(newEntity)
                    }
                }
            }
        }
    }

    private fun controlPlayerFromState(entity: Entity, serverState: PlayerState, mainCharacterId: Int) {
        val entityComponent = entity.getComponent(PlayerComponent::class.java)
        if (entityComponent.uniqueIdentifier != mainCharacterId) {
            entityComponent.playerState.velocity.x = serverState.velocity.x
            entityComponent.playerState.velocity.y = serverState.velocity.y
            var errorX = serverState.position.x - entityComponent.position.x
            var errorY = serverState.position.y - entityComponent.position.y
            if (serverState.serverReset || errorX > 5 || errorY > 5) {
                while (errorX > 5 || errorY > 5) {
                    errorX = serverState.position.x - entityComponent.position.x
                    errorY = serverState.position.y - entityComponent.position.y
                    entityComponent.playerState.velocity.x = errorX * Constants.speed * 0.1
                    entityComponent.playerState.velocity.y = errorY * Constants.speed * 0.1
                }
//                println("Reset, player position x: ${entityComponent.position.x}\t Server position: ${serverState.position.x}")
//                println("Reset, player position y: ${entityComponent.position.y}\t Server position: ${serverState.position.y}")
            }
        }
    }
}
