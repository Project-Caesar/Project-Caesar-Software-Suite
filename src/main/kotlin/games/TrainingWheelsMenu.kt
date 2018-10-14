package games

import javafx.beans.property.SimpleObjectProperty
import javafx.scene.image.Image
import javafx.scene.input.KeyCombination
import javafx.scene.layout.HBox
import javafx.stage.FileChooser
import javafx.stage.Stage
import tornadofx.*
import java.io.File

fun main(args: Array<String>) {
    launch<TrainingWheelsApp>(args)
}

// create app class; contains main starting view and any stylesheets (not used here)
class TrainingWheelsApp : App(TrainingWheelsMenu::class) {

    // Start application at fullscreen
    override fun start(stage: Stage) {
        super.start(stage)
        //stage.isFullScreen = true
        stage.fullScreenExitHint = ""
        stage.fullScreenExitKeyCombination = KeyCombination.NO_MATCH
    }
}
class TrainingWheelsMenu : View() {

    private val selectedIconPreview = SimpleObjectProperty<Image>()

    override val root = HBox()

    init {
        with(root) {
            vbox {
                button("Select Icon") {
                    setOnAction {
                        val fileChooser = FileChooser()

                        val imageFilter = FileChooser.ExtensionFilter(
                                "Select Icon",
                                listOf("*.png", "*.jpg", "*.jpeg", "*.bmp", "*.gif")
                        )

                        fileChooser.extensionFilters += imageFilter
                        val file = fileChooser.showOpenDialog(null)
                        println(file)
                        selectedIconPreview.value = Image(file.toURI().toString())
                    }
                }

                imageview {
                    imageProperty().bind(selectedIconPreview)
                }
            }
        }
    }
}