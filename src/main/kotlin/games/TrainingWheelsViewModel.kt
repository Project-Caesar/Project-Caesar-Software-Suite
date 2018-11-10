package games

import javafx.beans.property.*
import javafx.scene.image.Image
import javafx.scene.media.Media
import tornadofx.*
import java.io.File

class TrainingWheelsViewModel : ViewModel() {

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

    val selectedIconPreview = SimpleObjectProperty<Image>()

    val iconClicksToShrink = SimpleIntegerProperty(1)
    val iconShrinkLimit = SimpleDoubleProperty(.3)
    val iconStartSize = SimpleDoubleProperty(.9)
    val iconShrinkRatio = SimpleDoubleProperty(.9)

    val readyToStart = SimpleBooleanProperty(false)

    val successAudio = SimpleObjectProperty<Media>()
    val failAudio = SimpleObjectProperty<Media>()

    val dataFileDirectory = SimpleStringProperty(System.getProperty("user.dir") + "/TrainingWheelsLogs")

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
}