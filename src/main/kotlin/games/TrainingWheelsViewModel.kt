package games

import javafx.beans.property.*
import javafx.scene.image.Image
import javafx.scene.media.Media
import tornadofx.*

class TrainingWheelsViewModel(val pid_num : String) : ViewModel() {

    private val defaultIcon = Image("/Circle-icon.png")
    val defaultSuccessAudio = Media(TrainingWheelsViewModel::class.java.getResource("/correct.wav").toString())
    val defaultFailAudio = Media(TrainingWheelsViewModel::class.java.getResource("/incorrect.wav").toString())

    val csvHeaders = arrayOf(
            "Testee Name",
            "Icon Name",
            "Height",
            "Width",
            "X Position",
            "Y Position",
            "Number of Failures",
            "Time to Succeed",
            "TimeStamp"
    )


    val testeeName = SimpleStringProperty()

    val selectedIconPreview = SimpleObjectProperty<Image>(defaultIcon)

    val iconClicksToShrink = SimpleIntegerProperty(1)
    val iconShrinkLimit = SimpleDoubleProperty(.3)
    val iconStartSize = SimpleDoubleProperty(.9)
    val iconShrinkRatio = SimpleDoubleProperty(.9)

    val delayAtStart = SimpleIntegerProperty(30000)

    val delayBetweenTrials = SimpleIntegerProperty(0)

    val readyToStart = SimpleBooleanProperty(false)

    val successAudio = SimpleObjectProperty<Media>(defaultSuccessAudio)
    val failAudio = SimpleObjectProperty<Media>(defaultFailAudio)

    val dataFileDirectory = SimpleStringProperty(System.getProperty("user.dir") + "/TrainingWheelsLogs")

    private val exitTest = mutableListOf(false,false,false,false)

    init {

        testeeName.onChange {
            readyToStart.set(checkIfReady())
        }

        selectedIconPreview.onChange {
            readyToStart.set(checkIfReady())
        }

        iconShrinkLimit.onChange {
            readyToStart.set(checkIfReady())
        }

        iconStartSize.onChange {
            readyToStart.set(checkIfReady())
        }

        successAudio.onChange {
            readyToStart.set(checkIfReady())
        }

        failAudio.onChange {
            readyToStart.set(checkIfReady())
        }
    }

    private fun checkIfReady() : Boolean {
        return !(testeeName.value == "" || testeeName.value == null || selectedIconPreview.value == null
                || successAudio.value == null || failAudio.value == null || iconStartSize.value < iconShrinkLimit.value)
    }

    fun resetExitTest() {
        for (i in 0 until exitTest.size) {
            exitTest[i] = false
        }
    }

    fun passExitTestAt(pos : Int) {
        exitTest[pos] = true
    }

    fun checkExitTestPass() : Boolean {
        return !exitTest.contains(false)
    }
}