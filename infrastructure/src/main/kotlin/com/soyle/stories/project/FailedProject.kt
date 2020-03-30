package com.soyle.stories.project

import com.soyle.stories.di.modules.ApplicationComponent
import com.soyle.stories.project.projectList.ProjectIssueViewModel
import com.soyle.stories.project.projectList.ProjectListViewListener
import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.geometry.Pos
import javafx.scene.Node
import tornadofx.*

/**
 * Created by Brendan
 * Date: 2/16/2020
 * Time: 12:07 PM
 */
class FailedProject : ListCellFragment<ProjectIssueViewModel>() {

    private val projectListViewListener: ProjectListViewListener by lazy {
        find<ApplicationComponent>().projectListViewListener
    }

    override val root = vbox {
        hbox {
            var icon: Node = pane()
            itemProperty.onChange {
                val temp = when {
                    it == null -> pane()
                    //it.severity == "Error" -> MaterialIconView(MaterialIcon.ERROR_OUTLINE, "32")
                    //it.severity == "Warning" -> MaterialIconView(MaterialIcon.WARNING, "32")
                    else -> MaterialIconView(MaterialIcon.ERROR, "32")
                }
                icon.replaceWith(temp)
                icon = temp
            }
            vbox {
                label {
                    itemProperty.onChange { vm ->
                        text = if (vm == null) ""
                        else "${vm.name} [${vm.location}]"
                    }
                }
                label {
                    itemProperty.onChange { vm ->
                        text = if (vm == null) ""
                        else vm.additionalInformation
                    }
                }
            }
        }
        hbox(alignment = Pos.CENTER_RIGHT) {
            spacing = 5.0
            button("Ignore") {
                action {
                    //projectListViewListener.ignoreFailure(item.location)
                }
            }
            button("Relocate") {
                action {
                    //itemProperty.get()?.first?.let(controller::relocate)
                }
            }
            button("Retry") {
                action {
                    //itemProperty.get()?.first?.let(controller::retry)
                }
            }
        }
    }
}