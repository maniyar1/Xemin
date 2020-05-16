package xemin

import com.almasb.fxgl.dsl.getGameState
import com.almasb.fxgl.dsl.getGameWorld
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import java.net.DatagramSocket

object Server  {
   val socket = DatagramSocket(4241)

   fun run(playerState: PlayerState) {
      val cbor = Cbor()
      while (true) {
         val encodedData = cbor.dump(PlayerState.serializer(), playerState)
         val decodedData = cbor.load(PlayerState.serializer(), encodedData)
         println(encodedData)
      }
   }
}