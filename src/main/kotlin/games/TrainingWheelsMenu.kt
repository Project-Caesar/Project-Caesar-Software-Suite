package games

import javafx.geometry.Pos
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.scene.image.Image
import javafx.scene.input.InputEvent
import javafx.scene.input.KeyCombination
import javafx.scene.input.MouseEvent
import javafx.scene.layout.VBox
import javafx.stage.FileChooser
import javafx.stage.Stage
import tornadofx.*
import widgets.PercentInputField
import usecases.VirtualKeyboard
import javafx.scene.media.Media
import javafx.stage.DirectoryChooser

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
    private val viewModel = TrainingWheelsViewModel(app.parameters.raw[0])

    // used to select an icon file from a directory
    private val imageFileChooser = FileChooser()
    private val audioFileChooser = FileChooser()

    private val directoryChooser = DirectoryChooser()

    private val exitDialog = Alert(Alert.AlertType.CONFIRMATION).apply {
        // set up exitDialog text
        this.headerText = "End Project Caesar Program"
        this.contentText = "Select OK to end program or cancel to continue"
    }


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


                        // the imageview image will always be what is stored in the simple image property
                        imageProperty().bind(viewModel.selectedIconPreview)
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

                    vbox {
                        label {
                            text = "Delay Between Trials in Seconds"
                        }

                        textfield {
                            alignment = Pos.CENTER_RIGHT
                            text = "0"

                            textProperty().onChange {
                                if (it == null) {
                                    viewModel.delayBetweenTrials.set(0)
                                } else {
                                    it.trim()
                                    if (it.isInt() && it.toInt() >= 0) {
                                        viewModel.delayBetweenTrials.set(it.toInt())
                                    } else {
                                        viewModel.delayBetweenTrials.set(0)
                                    }
                                }
                            }

                            focusedProperty().onChange {
                                if (!it) {
                                    text = viewModel.delayBetweenTrials.value.toString()
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
                        listOf("*.wav")
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

            hbox {
                alignment = Pos.CENTER
                label("Delay at Start?")
                checkbox {
                    fire()
                    selectedProperty().onChange {
                        if (it) {
                            viewModel.delayAtStart.set(30000)
                        } else {
                            viewModel.delayAtStart.set(0)
                        }
                    }
                }
            }

            button("Select Folder to Save Data") {
                setOnAction {
                    val selectedDirectory = directoryChooser.showDialog(null)

                    if (selectedDirectory == null) {
                        viewModel.dataFileDirectory.value = System.getProperty("user.dir") + "/TrainingWheelsLogs"
                    } else {
                        viewModel.dataFileDirectory.value = selectedDirectory.absolutePath
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


            setOnMouseDragged {
                // check and see if the point is close to a corner and set tht value as true
                // in exitTest
                if (it.sceneX < 50 && it.sceneY < 50) {
                    viewModel.passExitTestAt(0)
                } else if (it.sceneX < 50 && it.sceneY > root.height - 50) {
                    viewModel.passExitTestAt(1)
                } else if (it.sceneX > root.width - 50 && it.sceneY < 50) {
                    viewModel.passExitTestAt(2)
                } else if (it.sceneX > root.width - 50 && it.sceneY > root.height - 50) {
                    viewModel.passExitTestAt(3)
                }
            }

            addEventFilter(InputEvent.ANY) {
                if (it.eventType == MouseEvent.MOUSE_RELEASED) {
                    if (viewModel.checkExitTestPass()) {

                        if (exitDialog.showAndWait().get() == ButtonType.OK) {
                            println("Menu Exit Dialog Triggered")
                            System.exit(0)
                        } else {
                            viewModel.resetExitTest()
                        }
                    }
                }
            }
        }
    }

}
