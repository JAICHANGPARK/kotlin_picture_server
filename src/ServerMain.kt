import com.sun.security.ntlm.Server
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File
import java.io.FileInputStream
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketException
import java.util.*

const val TIMEOUT = 3000
const val ROOT = "C:\\PictureData"

fun main(args: Array<String>) {

    var socket: Socket?
    var serverSocket: ServerSocket
    var dis: DataInputStream
    var dos: DataOutputStream
    var fis: FileInputStream
    var filePath: File
    var scanner: Scanner


    var path: String?
    var readDate: String
    var oldDate: String

    var date: Long
    var currentYear: Int
    var currentMonth: Int
    var previousYear: Int
    var previousMonth: Int
    val receivePort = 1255
    val connectPort = 1256

    val serverLayout = ServerLayout()
    serverLayout.setPCName()
    serverLayout.setIP()

    loop@ while (true) {

        path = ROOT
        filePath = File(path)
        if (!filePath.exists()) {
            filePath.mkdirs()
        }

        serverLayout.mFilePath.text = filePath.absolutePath + "\\"

        path += "\\LASTDATE"
        filePath = File(path)

        if (!filePath.exists()) {
            filePath.createNewFile()
            readDate = "0"
            serverLayout.mLastDate.text = "없음"
        } else {
            fis = FileInputStream(filePath)
            scanner = Scanner(fis)
            if (scanner.hasNext()) {
                readDate = scanner.next()
            } else {
                readDate = "0"
            }

            date = java.lang.Long.parseLong(readDate)
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = date
            currentYear = calendar.get(Calendar.YEAR)
            currentMonth = calendar.get(Calendar.MONTH)
            serverLayout.mLastDate.text =
                currentYear.toString() + "-" + currentMonth + "-" + calendar.get(Calendar.DATE)
        }

        oldDate = readDate

        serverSocket = ServerSocket(receivePort)
        println("서버가 시작되었습니다.")
        serverLayout.mConnectInfo.text = "연결 대기 중.."
        socket = serverSocket.accept()
        println("클라이언트와 연결 완료 ")
        serverLayout.mProgressBar.value = 0
        serverLayout.mRemainFile.text = ""
        serverLayout.mFileName.text = ""

        dis = DataInputStream(socket.getInputStream())
        var clientIp: String? = null
        try {
            clientIp = dis.readUTF()
        } catch (e: SocketException) {
            println("Ping Timeout")
            socket.close()
            serverSocket.close()
            dis.close()
            continue
        }
        println("Client IP $clientIp received" )
        socket.close()
        serverSocket.close()
        dis.close()

        socket = Socket(clientIp, connectPort)
        serverLayout.mConnectInfo.text ="클라이언트 연결 완료"
        dos = DataOutputStream(socket.getOutputStream())
        dos.writeUTF(readDate)
        socket.close()
        println("동기화 날짜 전송완료")

        socket.close()
        serverSocket.close()
        dis.close()
        dos.close()


    }
}