package games

import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Pos
import javafx.scene.image.Image
import javafx.scene.input.KeyCombination
import javafx.scene.layout.HBox
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import javafx.stage.FileChooser
import javafx.stage.Stage
import tornadofx.*

// main and app used to test the menu
// will be removed when menu is complete
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

    // used to select an icon file from a directory
    private val fileChooser = FileChooser()
    // used to store selected image
    private val selectedIconPreview = SimpleObjectProperty<Image>()

    private val iconShrinkRate = SimpleIntegerProperty(1)
    private val iconShrinkLimit = SimpleDoubleProperty(.10)

    override val root = HBox()

    init {
        with(root) {
            alignment = Pos.CENTER

            vbox {
                button("Select Icon") {
                    setOnAction {
                        /**
                         * when clicked/touched, add an image file extension filter to the
                         * file chooser and use it to select a valid file to be used as the icon
                         *
                         * the file is then converted into an image, and that image is stored in the
                         * simple image property
                         */

                        val imageFilter = FileChooser.ExtensionFilter(
                                "Select Icon",
                                listOf("*.png", "*.jpg", "*.jpeg", "*.bmp", "*.gif")
                        )

                        fileChooser.extensionFilters += imageFilter
                        val file = fileChooser.showOpenDialog(null)
                        if (file != null) {
                            println(file)
                            selectedIconPreview.value = Image(file.toURI().toString())
                        }
                    }
                }

                imageview {
                    // the imageview image will always be what is stored in the simple image property
                    imageProperty().bind(selectedIconPreview)

                    // set image dimensions when one is selected
                    imageProperty().onChange {
                        if (image.width >= image.height) {
                            fitWidth = 100.0
                            fitHeight = (fitWidth / image.width) * image.height
                        } else {
                            fitHeight = 100.0
                            fitWidth = (fitHeight / image.height) * image.width
                        }
                    }
                }
            }

            vbox {
                /**
                 * Need to be able to set number of successes until shrink, and at which size it starts moving
                 */

                textfield {
                    alignment = Pos.CENTER_RIGHT
                    text = "1"

                    textProperty().onChange {
                        if (it == null) {
                            iconShrinkRate.set(1)
                        } else {
                            it.trim()
                            if (it.isInt() && it.toInt() >= 1) {
                                iconShrinkRate.set(it.toInt())
                            } else {
                                iconShrinkRate.set(1)
                            }
                        }
                    }

                    focusedProperty().onChange {
                        if (!it) {
                            text = iconShrinkRate.value.toString()
                        }
                    }
                }

                hbox {
                    textfield {
                        alignment = Pos.CENTER_RIGHT
                        text = "10"
                        textProperty().onChange {
                            if (it == null) {
                                iconShrinkLimit.set(0.1)
                            } else {
                                it.trim()
                                if (it.isDouble() && it.toDouble() > 10) {
                                    if (it.toDouble() > 75) {
                                        iconShrinkLimit.set(.75)
                                    } else {
                                        iconShrinkLimit.set(it.toDouble() / 100)
                                    }
                                } else {
                                    iconShrinkLimit.set(0.1)
                                }
                            }
                        }

                        focusedProperty().onChange {
                            if (!it) {
                                text = (iconShrinkLimit.value * 100).toString()
                            }
                        }
                    }

                    text {
                        text = "%"
                        font = Font.font(font.family, 24.0)
                    }
                }
            }
        }
    }
}