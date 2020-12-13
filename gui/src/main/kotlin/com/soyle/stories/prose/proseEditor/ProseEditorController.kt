package com.soyle.stories.prose.proseEditor

import com.soyle.stories.entities.Prose
import com.soyle.stories.gui.View
import com.soyle.stories.prose.readProse.ReadProseController

class ProseEditorController(
    private val proseId: Prose.Id,
    view: View.Nullable<ProseEditorViewModel>,
    private val readProseController: ReadProseController
) : ProseEditorViewListener {

    private val presenter: ProseEditorPresenter = ProseEditorPresenter(view)

    override fun getValidState() {
        readProseController.readProse(proseId, presenter)
    }

}