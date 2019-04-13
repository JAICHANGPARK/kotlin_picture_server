import com.sun.security.ntlm.Server
import java.net.ServerSocket
import java.net.Socket

fun main(args: Array<String>) {
    val receivePort = 1255
    var socket: Socket?
    var serverSocket : ServerSocket

    val serverLayout = ServerLayout()

    serverLayout.setPCName()
    serverLayout.setIP()

    loop@ while(true){
        serverSocket = ServerSocket(receivePort)
        println("서버가 시작되었습니다.")
        serverLayout.mConnectInfo.text ="연결 대기 중.."
        socket = serverSocket.accept()
        println("클라이언트와 연결 완료 ")
        socket.close()
        serverSocket.close()

    }
}