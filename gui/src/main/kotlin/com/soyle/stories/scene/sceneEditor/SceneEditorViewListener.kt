package com.soyle.stories.scene.sceneEditor

import com.soyle.stories.common.NonBlankString
import com.soyle.stories.entities.ProseMention
import com.soyle.stories.prose.proseEditor.OnLoadMentionQueryOutput

interface SceneEditorViewListener {

    fun loadMentionSuggestionsForScene(query: NonBlankString, output: OnLoadMentionQueryOutput)
    fun useProseMentionInScene(mention: ProseMention<*>)

}