import com.sun.security.ntlm.Server
import java.io.*
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketException
import java.net.SocketTimeoutException
import java.util.*

const val TIMEOUT = 3000
const val ROOT = "C:\\PictureData"

fun main(args: Array<String>) {

    var socket: Socket?
    var serverSocket: ServerSocket
    var dis: DataInputStream
    var dos: DataOutputStream

    var filePath: File
    var scanner: Scanner

    var fis: FileInputStream
    var fos: FileOutputStream
    var bos: BufferedOutputStream

    var fileCount: Int
    var newDate: ByteArray?

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
        println("Client IP $clientIp received")
        socket.close()
        serverSocket.close()
        dis.close()

        socket = Socket(clientIp, connectPort)
        serverLayout.mConnectInfo.text = "클라이언트 연결 완료"
        dos = DataOutputStream(socket.getOutputStream())
        dos.writeUTF(readDate)
        socket.close()
        println("동기화 날짜 전송완료")

        serverSocket = ServerSocket(connectPort)
        socket = serverSocket.accept()
        dis = DataInputStream(socket.getInputStream())
        val mode = dis.readInt()
        serverLayout.mConnectInfo.text = "파일 수신중..."
        when (mode) {
            0 -> {
                previousMonth = 0
                previousYear = 0
                fileCount = dis.readInt()
                println("file count = $fileCount")
                if (fileCount == 0) {
                    break@loop
                }
                serverLayout.mRemainFile.text = fileCount.toString() + "개 남음.."

                newDate = ByteArray(64)
                for (i in 0 until fileCount) {
                    try {
                        socket = serverSocket.accept()
                        serverSocket.soTimeout = TIMEOUT
                        dis = DataInputStream(socket.getInputStream())
                        // 경로 파싱 (날짜 받기)
                        readDate = dis.readUTF() //날짜
                        if (i == 0) {
                            newDate = readDate.toByteArray() //최신사진부터 받기 때문에 처음이 최신날짜
                        }
                    } catch (e: SocketTimeoutException) {
                        println("SocketTimeoutException")
                        newDate = oldDate.toByteArray()
                        break
                    } catch (e: Exception) {
                        println("Exception")
                        newDate = oldDate.toByteArray()
                        break
                    }

                    date = java.lang.Long.parseLong(readDate)
                    val calendar = Calendar.getInstance()
                    calendar.timeInMillis = date
                    currentYear = calendar.get(Calendar.YEAR)
                    currentMonth = calendar.get(Calendar.MONTH) + 1

                    // 경로 확인
                    if (!(previousMonth == currentMonth && previousYear == currentYear)) { // 다음날짜 폴더 만들기
                        path = ROOT + "\\" + currentYear + "\\"
                        if (currentMonth < 10) {
                            path += "0"
                        }
                        path += currentMonth.toString() + "\\"
                        previousYear = currentYear
                        previousMonth = currentMonth
                    }

                    // 폴더 생성
                    filePath = File(path)
                    if (!filePath.exists()) {
                        filePath.mkdirs()
                    }

                    var fName: String? = null
                    fName = dis.readUTF() //파일 이름

                    println("파일명 [ $fName ] 수신 완료")

                    serverLayout.mFileName.text = fName

                    // 파일을 생성하고 파일에 대한 출력 스트림 생성
                    val file = File(path + fName)
                    fos = FileOutputStream(file)
                    bos = BufferedOutputStream(fos)
                    println("수신 시작")
                    // 받은 파일 크기 측정
                    var len: Int
                    val size = 1500
                    val fileData = ByteArray(size)
                    while (true) {
                        len = dis.read(fileData)
                        var isNext = len == -1
                        if (isNext) {
                            break
                        }
                        bos.write(fileData, 0, len)
                    }

                    println("[ $fName ] 파일 저장 완료")
                    println("받은 파일의 크기 : " + file.length() + " 바이트")

//                    imageMirroring.mPath = file.absolutePath
//                    System.out.println("m.path = " + imageMirroring.mPath)

                    // set progress bar & remain files
                    val percent = (i + 1) * 100 / fileCount
                    serverLayout.mProgressBar.value = percent
                    if ((fileCount - i - 1) > 0) {
                        serverLayout.mRemainFile.text = (fileCount - i - 1).toString() + "개 남음.."
                    } else {
                        serverLayout.mRemainFile.text = "수신 완료"
                    }
                    dis.close()
                    bos.close()
                }
                // 동기화 날짜 업데이트
                filePath = File("$ROOT\\LASTDATE")
                fos = FileOutputStream(filePath)
                bos = BufferedOutputStream(fos)
                newDate?.let { bos.write(it, 0, newDate.size) }
                bos.close()

            }
        }

        socket?.close()
        serverSocket.close()
        dis.close()
        dos.close()


    }
}