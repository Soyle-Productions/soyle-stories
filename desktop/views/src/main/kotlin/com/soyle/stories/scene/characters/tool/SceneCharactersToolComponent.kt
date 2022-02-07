package com.soyle.stories.scene.characters.tool

import com.soyle.stories.di.resolve
import com.soyle.stories.project.ProjectScope
import javafx.scene.Parent
import tornadofx.View
import tornadofx.objectProperty

class SceneCharactersToolComponent : View() {

    override val scope = super.scope as ProjectScope
    private val subScope = SceneCharactersToolScope(scope)
    val locale: SceneCharactersToolLocale = resolve()

    val viewModel = SceneCharactersToolViewModel(
        objectProperty(SceneCharactersToolViewModel.SceneSelection.None),
        objectProperty(null),
        subScope
    )

    override val root: Parent = SceneCharactersTool(viewModel, locale = locale)

    override fun onUndock() {
        super.onUndock()
        subScope.close()
    }

}
