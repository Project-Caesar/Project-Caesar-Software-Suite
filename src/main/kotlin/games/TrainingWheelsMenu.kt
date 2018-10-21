package games

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Pos
import javafx.scene.image.Image
import javafx.scene.input.KeyCombination
import javafx.scene.layout.VBox
import javafx.stage.FileChooser
import javafx.stage.Stage
import tornadofx.*
import widgets.PercentInputField

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

    private val iconClicksToShrink = SimpleIntegerProperty(1)
    private val iconShrinkLimit = SimpleDoubleProperty(.1)
    private val iconStartSize = SimpleDoubleProperty(.9)


    //private val iconShrinkRatio = SimpleDoubleProperty(.9)

    private val readyToStart = SimpleBooleanProperty(true)

    override val root = VBox()

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

        with(root) {
            alignment = Pos.CENTER

            hbox {

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

                        // basic image dimensions when one is selected
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

                    vbox {
                        label {
                            text = "Number of clicks to shrink Icon"
                        }

                        textfield {
                            alignment = Pos.CENTER_RIGHT
                            text = "1"

                            textProperty().onChange {
                                if (it == null) {
                                    iconClicksToShrink.set(1)
                                } else {
                                    it.trim()
                                    if (it.isInt() && it.toInt() >= 1) {
                                        iconClicksToShrink.set(it.toInt())
                                    } else {
                                        iconClicksToShrink.set(1)
                                    }
                                }
                            }

                            focusedProperty().onChange {
                                if (!it) {
                                    text = iconClicksToShrink.value.toString()
                                }
                            }
                        }
                    }

                    this += PercentInputField(
                            "Starting Icon Size relative to Screen",
                            iconStartSize,
                            iconStartSize.value,
                            .1
                    )
                    this += PercentInputField(
                            "Smallest Icon Size relative to Screen",
                            iconShrinkLimit,
                            iconShrinkLimit.value,
                            .1
                    )
                }

            }

            button("START") {
                disableProperty().bind(!readyToStart)
            }
        }
    }
}
