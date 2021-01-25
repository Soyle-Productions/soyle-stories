package com.soyle.stories.desktop.config.prose

import com.soyle.stories.desktop.config.InProjectScope
import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.prose.editProse.*
import com.soyle.stories.prose.entityMentionedInProse.EntityMentionedInProseNotifier
import com.soyle.stories.prose.entityMentionedInProse.EntityMentionedInProseReceiver
import com.soyle.stories.prose.invalidateRemovedMentions.InvalidateRemovedMentionsController
import com.soyle.stories.prose.invalidateRemovedMentions.InvalidateRemovedMentionsControllerImpl
import com.soyle.stories.prose.proseCreated.ProseCreatedNotifier
import com.soyle.stories.prose.proseCreated.ProseCreatedReceiver
import com.soyle.stories.prose.proseEditor.ProseEditorScope
import com.soyle.stories.prose.readProse.ReadProseController
import com.soyle.stories.prose.readProse.ReadProseControllerImpl
import com.soyle.stories.prose.textInsertedIntoProse.TextInsertedIntoProseNotifier
import com.soyle.stories.prose.textInsertedIntoProse.TextInsertedIntoProseReceiver
import com.soyle.stories.prose.usecases.bulkUpdateProse.BulkUpdateProse
import com.soyle.stories.prose.usecases.bulkUpdateProse.BulkUpdateProseUseCase
import com.soyle.stories.prose.usecases.detectInvalidMentions.DetectInvalidatedMentions
import com.soyle.stories.prose.usecases.detectInvalidMentions.DetectInvalidatedMentionsUseCase
import com.soyle.stories.prose.usecases.readProse.ReadProse
import com.soyle.stories.prose.usecases.readProse.ReadProseUseCase
import com.soyle.stories.prose.usecases.updateProse.UpdateProse
import com.soyle.stories.prose.usecases.updateProse.UpdateProseUseCase
import com.soyle.stories.scene.getStoryElementsToMention.GetStoryElementsToMentionController
import com.soyle.stories.scene.getStoryElementsToMention.GetStoryElementsToMentionControllerImpl
import com.soyle.stories.scene.usecases.getStoryElementsToMention.GetStoryElementsToMentionInScene
import com.soyle.stories.scene.usecases.getStoryElementsToMention.GetStoryElementsToMentionInSceneUseCase

object UseCases {

    init {
        scoped<ProjectScope> {
            readProse()
            editProse()
            updateProse()
            getSuggestedMentions()
            invalidateRemovedMentions()
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

    private fun InProjectScope.updateProse() {
        provide<UpdateProse> {
            UpdateProseUseCase(get())
        }
        provide<UpdateProse.OutputPort> {
            UpdateProseOutput(get())
        }
        provide(ContentReplacedReceiver::class) { ContentReplacedNotifier() }
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

    private fun InProjectScope.invalidateRemovedMentions() {
        provide<InvalidateRemovedMentionsController> {
            InvalidateRemovedMentionsControllerImpl(
                applicationScope.get(),
                get(),
                get()
            )
        }

        provide<DetectInvalidatedMentions> {
            DetectInvalidatedMentionsUseCase(get(), get(), get(), get())
        }
    }

}