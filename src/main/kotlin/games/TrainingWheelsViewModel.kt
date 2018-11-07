package games

import javafx.beans.property.*
import javafx.scene.image.Image
import javafx.scene.media.Media
import tornadofx.*

class TrainingWheelsViewModel : ViewModel() {

    val testeeName = SimpleStringProperty()

    val selectedIconPreview = SimpleObjectProperty<Image>()

    val iconClicksToShrink = SimpleIntegerProperty(1)
    val iconShrinkLimit = SimpleDoubleProperty(.3)
    val iconStartSize = SimpleDoubleProperty(.9)
    val iconShrinkRatio = SimpleDoubleProperty(.9)

    val readyToStart = SimpleBooleanProperty(false)

    val successAudio = SimpleObjectProperty<Media>()
    val failAudio = SimpleObjectProperty<Media>()

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
        return !(testeeName.value == "" || selectedIconPreview.value == null || successAudio.value == null
                || failAudio.value == null || iconStartSize.value < iconShrinkLimit.value)
    }
}