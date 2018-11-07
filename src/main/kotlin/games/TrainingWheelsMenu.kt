package games

import javafx.geometry.Pos
import javafx.scene.image.Image
import javafx.scene.input.KeyCombination
import javafx.scene.layout.VBox
import javafx.stage.FileChooser
import javafx.stage.Stage
import tornadofx.*
import widgets.PercentInputField
import VirtualKeyboard
import javafx.scene.media.Media

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
        stage.isFullScreen = true
        stage.fullScreenExitKeyCombination = KeyCombination.NO_MATCH
        stage.fullScreenExitHint = ""
    }
}

class TrainingWheelsMenu : View() {

    private val trainingWheelsScope = Scope()
    private val viewModel = TrainingWheelsViewModel()

    // used to select an icon file from a directory
    private val imageFileChooser = FileChooser()
    private val audioFileChooser = FileChooser()



    override val root = VBox()

    init {

        setInScope(viewModel, trainingWheelsScope)

        with(root) {
            alignment = Pos.CENTER

            hbox {
                alignment = Pos.CENTER

                label("Testee Name")

                textfield {
                    alignment = Pos.CENTER_RIGHT

                    textProperty().onChange {
                        if (it != null) {
                            viewModel.testeeName.set(it)
                        } else {
                            viewModel.testeeName.set("")
                        }
                    }
                }
            }

            hbox {

                alignment = Pos.CENTER

                vbox {
                    button("Select Icon") {

                        val imageFilter = FileChooser.ExtensionFilter(
                                "Select Icon",
                                listOf("*.png", "*.jpg", "*.jpeg", "*.bmp", "*.gif")
                        )

                        imageFileChooser.extensionFilters += imageFilter

                        setOnAction {
                            /**
                             * when clicked/touched, add an image file extension filter to the
                             * file chooser and use it to select a valid file to be used as the icon
                             *
                             * the file is then converted into an image, and that image is stored in the
                             * simple image property
                             */


                            val file = imageFileChooser.showOpenDialog(null)
                            if (file != null) {
                                viewModel.selectedIconPreview.value = Image(file.toURI().toString())
                            }
                        }
                    }

                    imageview {
                        // the imageview image will always be what is stored in the simple image property
                        imageProperty().bind(viewModel.selectedIconPreview)

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
                                    viewModel.iconClicksToShrink.set(1)
                                } else {
                                    it.trim()
                                    if (it.isInt() && it.toInt() >= 1) {
                                        viewModel.iconClicksToShrink.set(it.toInt())
                                    } else {
                                        viewModel.iconClicksToShrink.set(1)
                                    }
                                }
                            }

                            focusedProperty().onChange {
                                if (!it) {
                                    text = viewModel.iconClicksToShrink.value.toString()
                                }
                            }
                        }
                    }

                    this += PercentInputField(
                            "Starting Icon Size relative to Screen",
                            viewModel.iconStartSize,
                            viewModel.iconStartSize.value,
                            .1
                    )
                    this += PercentInputField(
                            "Smallest Icon Size relative to Screen",
                            viewModel.iconShrinkLimit,
                            viewModel.iconShrinkLimit.value,
                            .1
                    )
                }

            }

            hbox {

                alignment = Pos.CENTER

                val audioFilter = FileChooser.ExtensionFilter(
                        "Select Audio File",
                        listOf("*.mp3", "*.wav")
                )

                audioFileChooser.extensionFilters += audioFilter

                button ("Select Success Audio"){

                    setOnAction {
                        val file = audioFileChooser.showOpenDialog(null)
                        if (file != null) {
                            viewModel.successAudio.value = Media(file.toURI().toString())
                        }
                    }
                }

                button (" Select Fail Audio "){

                    setOnAction {
                        val file = audioFileChooser.showOpenDialog(null)
                        if (file != null) {
                            viewModel.failAudio.value = Media(file.toURI().toString())
                        }
                    }
                }
            }

            button("START") {
                disableProperty().bind(!viewModel.readyToStart)
                action {
                    replaceWith(find<TrainingWheels>(trainingWheelsScope))
                }
            }

            this += VirtualKeyboard().view()
        }
    }
}
