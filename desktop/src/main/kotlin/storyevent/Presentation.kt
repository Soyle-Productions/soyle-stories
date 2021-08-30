package com.soyle.stories.desktop.config.storyevent

import com.soyle.stories.di.DI
import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.domain.project.Project
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.storyevent.create.*
import com.soyle.stories.storyevent.list.StoryEventListTool
import com.soyle.stories.storyevent.list.StoryEventListToolView
import com.soyle.stories.storyevent.rename.RenameStoryEventDialog
import com.soyle.stories.storyevent.rename.RenameStoryEventDialogView
import com.soyle.stories.storyevent.rename.StoryEventRenamedNotifier
import tornadofx.UIComponent

object Presentation {

    init {
        scoped<ProjectScope> {
            provide<StoryEventListTool> {
                object : StoryEventListTool {
                    override fun invoke(projectId: Project.Id): UIComponent {
                        val view = StoryEventListToolView(
                            projectId = projectId,
                            createStoryEventDialog = get(),
                            renameStoryEventDialog = get(),
                            listStoryEventsInProject = get(),
                            storyEventCreated = get<StoryEventCreatedNotifier>(),
                            storyEventRenamed = get<StoryEventRenamedNotifier>()
                        )
                        if (projectId.uuid == this@provide.projectId) {
                            DI.getRegisteredTypes(this@provide)[StoryEventListToolView::class] = view
                        }
                        return view
                    }
                }
            }

            provide<CreateStoryEventDialog> {
                object : CreateStoryEventDialog {
                    override fun invoke(props: CreateStoryEventDialog.Props): UIComponent {
                        return CreateStoryEventDialogView(
                            props,
                            get<CreateStoryEventController>()
                        )
                    }
                }
            }

            provide<RenameStoryEventDialog> {
                object : RenameStoryEventDialog {
                    override fun invoke(props: RenameStoryEventDialog.Props): UIComponent {
                        return RenameStoryEventDialogView(
                            props,
                            get()
                        )
                    }
                }
            }
        }
    }

}