package games

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.image.Image
import tornadofx.*

class TrainingWheelsViewModel : ViewModel() {
    // used to store selected image
    val selectedIconPreview = SimpleObjectProperty<Image>()

    val iconClicksToShrink = SimpleIntegerProperty(1)
    val iconShrinkLimit = SimpleDoubleProperty(.1)
    val iconStartSize = SimpleDoubleProperty(.9)
    val iconShrinkRatio = SimpleDoubleProperty(.9)

    val readyToStart = SimpleBooleanProperty(false)

    init {
        iconShrinkLimit.onChange {
            if (it > iconStartSize.value) {
                readyToStart.set(false)
            } else {
                readyToStart.set(true)
            }
        }

        iconStartSize.onChange {
            if (it < iconShrinkLimit.value) {
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