package com.soyle.stories.common.builders

import javafx.beans.property.StringProperty
import javafx.beans.value.ObservableValue
import javafx.scene.Node
import javafx.scene.layout.BorderPane
import tornadofx.*
//
//inline operator fun String.invoke(): ObservableValue<String> = stringProperty(this)
//inline operator fun Boolean.invoke(): ObservableValue<Boolean> = booleanProperty(this)
//
//inline fun String.always(): ObservableValue<String> = invoke()
//inline fun Boolean.always(): ObservableValue<Boolean> = invoke()
//
//fun BuilderScope<*>.workbench(
//    title: ObservableValue<String>? = null,
//) {
//    window(title = title)
//
//    borderPane(
//        configure = {
//
//        }
//    ) {
//        top = menuBar {
//            menu(text = "File"(), id = "file"()) {
//                menu(text = { "New" }, id = { "file_new" }, accelerator = { "Shortcut+N" }) {
//                    menuItem(text = { "Project" }, id = { "file_new_project" })
//                    menuItem(text = { "Character" }, id = { "file_new_character" })
//                    menuItem(text = { "Location" }, id = { "file_new_location" })
//                    menuItem(text = { "Story Event" }, id = { "file_new_storyevent" })
//                    menuItem(text = { "Scene" }, id = { "file_new_scene" })
//                }
//            }
//        }
//
//        center = vbox {
//            /*
//            changeContentWhen(title) {
//                +("foo", "bar") {
//
//                }
//                +"baz" {
//
//                }
//                +is<Boolean> {
//
//                }
//            }
//
//            changeContentWhen(title) {
//                (eq("foo"), eq("bar"), is<Int>()) {
//
//                }
//                "baz" {
//
//                }
//                is<Boolean> {
//
//                }
//            }
//
//
//             */
//
//        }
//    }
//
//}




fun temp(isLoading: ObservableValue<Boolean>) {
    HBox(configure = {
        addClass("")
    }) {
        vbox {
            hbox {
                val labelAssociation = objectProperty<Node?>()
                val setLabelTarget = labelAssociation::set

                button()

                branchContent(basedOn = isLoading) {
                    if (it) {}
                    else {
                        branchContent(basedOn = booleanProperty()) {

                        }
                        label(
                            text = "".toProperty(),
                            labelFor = labelAssociation,
                            configure = {

                            }
                        )
                        setLabelTarget(button(
                            text = "".toProperty()
                        ) {

                        })
                    }
                }

                branchContent(basedOn = stringProperty()) {

                }

                label()
            }
        }
    }
}