package com.soyle.stories.scene.sceneEditor

import com.soyle.stories.domain.prose.MentionedEntityId
import com.soyle.stories.domain.prose.content.ProseContent
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.prose.proseEditor.OnLoadMentionQueryOutput
import com.soyle.stories.prose.proseEditor.OnLoadMentionReplacementsOutput

interface SceneEditorViewListener {

    fun getValidState()
    fun loadMentionSuggestionsForScene(query: NonBlankString, output: OnLoadMentionQueryOutput)
    fun useProseMentionInScene(mention: ProseContent.Mention<*>)
    fun loadMentionReplacements(entityId: MentionedEntityId<*>, output: OnLoadMentionReplacementsOutput)
    fun changeConflict(conflict: String)
    fun changeResolution(resolution: String)

}