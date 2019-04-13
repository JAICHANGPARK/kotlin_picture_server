import java.awt.Dimension
import java.awt.Font
import java.util.*
import javax.swing.*

class ServerLayout : JFrame() {
//    private val mFilePath: JLabel
//    private val mFileName : JLabel
//    private val mPcName : JLabel
//    private val mIPInfo : JLabel
//    private val mConnectInfo : JLabel
//    private val mRemainFile : JLabel

    init {

        title = "Picture Server"
        isVisible = true
        size = Dimension(655, 365)
        layout = null
        defaultCloseOperation = EXIT_ON_CLOSE

        val infoTab = JTabbedPane(SwingConstants.TOP)
        infoTab.font = Font(null, Font.PLAIN, 14)
        infoTab.setBounds(0,0,635,323)
        add(infoTab)

        val sendInformation = JPanel()
        infoTab.addTab("전송정보", null, sendInformation, null)
        sendInformation.locale = Locale.KOREA
        sendInformation.layout = null

        val connectionState = JLabel("연결 상태")
        connectionState.font = Font(null, Font.PLAIN, 14)
        connectionState.setBounds(12,10,79,34)
        sendInformation.add(connectionState)



    }

}