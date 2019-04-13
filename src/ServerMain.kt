import com.sun.security.ntlm.Server
import java.net.ServerSocket
import java.net.Socket

const val TIMEOUT = 3000
const val ROOT = "C:\\PictureData"

fun main(args: Array<String>) {

    var socket: Socket?
    var serverSocket : ServerSocket

    val serverLayout = ServerLayout()


    var path : String?
    var readDate : String
    var oldDate : String
    
    var date: Long
    var currentYear: Int
    var currentMonth: Int
    var previousYear: Int
    var previousMonth: Int
    val receivePort = 1255
    val connectPort = 1256

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