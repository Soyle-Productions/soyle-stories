package com.soyle.stories.scene.sceneEditor

import com.soyle.stories.di.get
import com.soyle.stories.di.resolve
import com.soyle.stories.prose.proseEditor.ProseEditorScope
import com.soyle.stories.prose.proseEditor.ProseEditorView
import javafx.scene.Parent
import tornadofx.View
import tornadofx.pane

class SceneEditorView : View() {

    override val scope: SceneEditorScope = super.scope as SceneEditorScope
    val state = resolve<SceneEditorState>()

    override val root: Parent = pane {
        add(ProseEditorScope(scope.projectScope, scope.type.proseId).get<ProseEditorView>())
    }

}