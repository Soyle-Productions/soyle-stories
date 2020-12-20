package com.soyle.stories.desktop.config.prose

import com.soyle.stories.desktop.config.InProjectScope
import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.prose.editProse.BulkUpdateProseOutput
import com.soyle.stories.prose.editProse.EditProseController
import com.soyle.stories.prose.editProse.EditProseControllerImpl
import com.soyle.stories.prose.entityMentionedInProse.EntityMentionedInProseNotifier
import com.soyle.stories.prose.entityMentionedInProse.EntityMentionedInProseReceiver
import com.soyle.stories.prose.proseCreated.ProseCreatedNotifier
import com.soyle.stories.prose.proseCreated.ProseCreatedReceiver
import com.soyle.stories.prose.proseEditor.ProseEditorScope
import com.soyle.stories.prose.proseEditor.ProseEditorState
import com.soyle.stories.prose.readProse.ReadProseController
import com.soyle.stories.prose.readProse.ReadProseControllerImpl
import com.soyle.stories.prose.textInsertedIntoProse.TextInsertedIntoProseNotifier
import com.soyle.stories.prose.textInsertedIntoProse.TextInsertedIntoProseReceiver
import com.soyle.stories.prose.usecases.bulkUpdateProse.BulkUpdateProse
import com.soyle.stories.prose.usecases.bulkUpdateProse.BulkUpdateProseUseCase
import com.soyle.stories.prose.usecases.readProse.ReadProse
import com.soyle.stories.prose.usecases.readProse.ReadProseUseCase
import com.soyle.stories.scene.getStoryElementsToMention.GetStoryElementsToMentionController
import com.soyle.stories.scene.getStoryElementsToMention.GetStoryElementsToMentionControllerImpl
import com.soyle.stories.scene.usecases.getStoryElementsToMention.GetStoryElementsToMentionInScene
import com.soyle.stories.scene.usecases.getStoryElementsToMention.GetStoryElementsToMentionInSceneUseCase

object UseCases {

    init {
        scoped<ProjectScope> {
            readProse()
            editProse()
            getSuggestedMentions()
        }
    }

    private fun InProjectScope.readProse() {
        provide<ReadProseController> {
            ReadProseControllerImpl(
                applicationScope.get(),
                get()
            )
        }

        provide<ReadProse> {
            ReadProseUseCase(get())
        }
    }

    private fun InProjectScope.editProse() {
        provide<BulkUpdateProse> {
            BulkUpdateProseUseCase(get())
        }
        provide<BulkUpdateProse.OutputPort> {
            BulkUpdateProseOutput(get(), get(), get())
        }
        provide(ProseCreatedReceiver::class) { ProseCreatedNotifier() }
        provide(TextInsertedIntoProseReceiver::class) { TextInsertedIntoProseNotifier() }
        provide(EntityMentionedInProseReceiver::class) { EntityMentionedInProseNotifier() }

        scoped<ProseEditorScope> {
            provide<EditProseController> {
                EditProseControllerImpl(
                    proseId,
                    get<ProseEditorState>().versionNumber.get(),
                    projectScope.applicationScope.get(),
                    projectScope.get(),
                    projectScope.get()
                )
            }
        }
    }

    private fun InProjectScope.getSuggestedMentions() {
        provide<GetStoryElementsToMentionInScene> {
            GetStoryElementsToMentionInSceneUseCase(
                get(),
                get(),
                get()
            )
        }
        provide<GetStoryElementsToMentionController> {
            GetStoryElementsToMentionControllerImpl(
                get(),
                applicationScope.get()
            )
        }
    }

}