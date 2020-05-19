package xemin

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.cbor.Cbor
import xemin.players.FullState
import xemin.players.Inputs
import xemin.players.PlayerState
import xemin.players.XeminPoint
import java.io.DataOutputStream
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.ServerSocket
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.coroutines.CoroutineContext

object Server: CoroutineScope{
   override val coroutineContext: CoroutineContext
      get() = Dispatchers.Default

   val cbor = Cbor()
   var entityList = ConcurrentLinkedQueue<PlayerState>()
   val testState = PlayerState(XeminPoint(0.0, 0.0), XeminPoint(0.0, 0.0),20, System.currentTimeMillis(), Inputs(false, true, false, false), false)

   fun connectionEstablisher() {
      while (true) {
         val server = ServerSocket(9999)
         val connection = server.accept()
         val udpSocket = DatagramSocket()
         println("Port is ${udpSocket.localPort} (server)")
         DataOutputStream(connection.getOutputStream()).writeInt(udpSocket.localPort)
         launch {
            run(udpSocket)
         }
         server.close()
         Thread.sleep(4)
      }
   }

   fun run(socket: DatagramSocket) {
//      entityList.add(testState)
      while (true) {
         val recPacket = receivePacket(socket)
         val clientPlayerState = cbor.load(PlayerState.serializer(), recPacket.data)
         var found = false
         entityList.forEach {serverPlayerState ->
            if (serverPlayerState.uniqueIdentifier == clientPlayerState.uniqueIdentifier) {
                found = true
                serverPlayerState.clone(clientPlayerState)
            }
         }
         if (!found) {
            entityList.add(clientPlayerState)
         }

         sendFullState(recPacket, socket)
      }
   }

   private fun receivePacket(socket: DatagramSocket): DatagramPacket {
      val buffer = ByteArray(1400)
      val recPacket = DatagramPacket(buffer, buffer.size)
      socket.receive(recPacket)
      return recPacket
   }

   private fun sendFullState(recPacket: DatagramPacket, socket: DatagramSocket) {
      val address = recPacket.address
      val port = recPacket.port
//      val totalState = (FullState(arrayOf(PlayerState(XeminPoint(0.0, 0.0), 20, System.currentTimeMillis(), Inputs(false, true, false, false), false))))
      val totalState = FullState(entityList.toTypedArray())
      val serializedTotalState = cbor.dump(FullState.serializer(), totalState)
      val sendPacket = DatagramPacket(serializedTotalState, serializedTotalState.size, address, port)
      socket.send(sendPacket)
   }

}