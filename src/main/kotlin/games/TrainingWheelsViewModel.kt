package games

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.image.Image
import javafx.scene.media.Media
import tornadofx.*

class TrainingWheelsViewModel : ViewModel() {
    // used to store selected image
    val selectedIconPreview = SimpleObjectProperty<Image>()

    val iconClicksToShrink = SimpleIntegerProperty(1)
    val iconShrinkLimit = SimpleDoubleProperty(.3)
    val iconStartSize = SimpleDoubleProperty(.9)
    val iconShrinkRatio = SimpleDoubleProperty(.9)

    val readyToStart = SimpleBooleanProperty(false)

    val successAudio = SimpleObjectProperty<Media>()
    val failAudio = SimpleObjectProperty<Media>()

    init {
        iconShrinkLimit.onChange {
            if (it > iconStartSize.value || selectedIconPreview.value == null) {
                readyToStart.set(false)
            } else {
                readyToStart.set(true)
            }
        }

        iconStartSize.onChange {
            if (it < iconShrinkLimit.value || selectedIconPreview.value == null) {
                readyToStart.set(false)
            } else {
                readyToStart.set(true)
            }
        }

        selectedIconPreview.onChange {
            if (it != null) {
                readyToStart.set(true)
            } else {
                readyToStart.set(false)
            }
        }
    }

}