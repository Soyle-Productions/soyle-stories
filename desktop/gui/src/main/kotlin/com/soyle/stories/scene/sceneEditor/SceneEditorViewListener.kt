package com.soyle.stories.scene.sceneEditor

import com.soyle.stories.domain.prose.MentionedEntityId
import com.soyle.stories.domain.prose.ProseMention
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.prose.proseEditor.OnLoadMentionQueryOutput
import com.soyle.stories.prose.proseEditor.OnLoadMentionReplacementsOutput

interface SceneEditorViewListener {

    fun loadMentionSuggestionsForScene(query: NonBlankString, output: OnLoadMentionQueryOutput)
    fun useProseMentionInScene(mention: ProseMention<*>)
    fun loadMentionReplacements(entityId: MentionedEntityId<*>, output: OnLoadMentionReplacementsOutput)

}