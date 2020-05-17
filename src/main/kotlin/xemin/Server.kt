package xemin

import com.almasb.fxgl.dsl.getGameState
import com.almasb.fxgl.dsl.getGameWorld
import kotlinx.coroutines.delay
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import java.net.DatagramPacket
import java.net.DatagramSocket
import javax.xml.crypto.Data

object Server  {
   val socket = DatagramSocket(4241)
   val cbor = Cbor()

   fun run() {
      while (true) {
         val buffer = ByteArray(512)
         val recPacket = DatagramPacket(buffer, buffer.size)
         socket.receive(recPacket)
         val individualPlayerState = cbor.load(PlayerState.serializer(), recPacket.data)

         val address = recPacket.address
         val port = recPacket.port
         val totalState = FullState(arrayOf(individualPlayerState, PlayerState(XeminPoint(0.0, 0.0), 20, individualPlayerState.time, Inputs(false, true, false, false), false)))
         val serializedTotalState = cbor.dump(FullState.serializer(), totalState)
         val sendPacket = DatagramPacket(serializedTotalState, serializedTotalState.size, address, port)
         socket.send(sendPacket)
      }
   }
}