package xemin

import com.almasb.fxgl.dsl.getGameWorld
import com.almasb.fxgl.entity.Entity
import com.almasb.fxgl.entity.SpawnData
import kotlinx.coroutines.*
import kotlinx.serialization.cbor.Cbor
import xemin.component.PlayerComponent
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.coroutines.CoroutineContext

object Client : CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default

    val entitiesToAddState = ConcurrentLinkedQueue<Entity>()

    val port = 4241
    val socket = DatagramSocket()
    val address = InetAddress.getByName("localhost")
    val cbor = Cbor()

    fun run(playerState: PlayerState, mainCharacterId: Int) {
        while (true) {
            val encodedPlayerData = cbor.dump(PlayerState.serializer(), playerState)
            val sendPacket = DatagramPacket(encodedPlayerData, encodedPlayerData.size, address, port)
            socket.send(sendPacket)

            val buffer = ByteArray(512)
            val recPacket = DatagramPacket(buffer, buffer.size, address, port)
            socket.receive(recPacket)
            val playerStates = cbor.load(FullState.serializer(), recPacket.data)
            playerStates.players.forEach { serverState ->
                var found = false
                getGameWorld().entitiesCopy.iterator().forEach {entity ->
                    if (entity.type == EntityType.PLAYER) {
                        val existingEntityComponent = entity.getComponent(PlayerComponent::class.java)
                        if (existingEntityComponent.uniqueIdentifier == serverState.uniqueIdentifier) {
                            found = true
                            GlobalScope.launch {
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
            Thread.sleep(20)
        }
    }

    private fun controlPlayerFromState(entity: Entity, serverState: PlayerState, mainCharacterId: Int) {
        val entityComponent = entity.getComponent(PlayerComponent::class.java)
        if (serverState.serverReset) {
            entityComponent.position.x = serverState.position.x
            entityComponent.position.y = serverState.position.y
        } else if (entityComponent.uniqueIdentifier != mainCharacterId){
            if (serverState.inputs.isUpPressed) {
                entityComponent.moveUp()
            }
            if (serverState.inputs.isDownPressed) {
                entityComponent.moveDown()
            }
            if (serverState.inputs.isLeftPressed) {
                entityComponent.moveLeft()
            }
            if (serverState.inputs.isRightPressed) {
                entityComponent.moveRight()
            }
        }
    }
}
