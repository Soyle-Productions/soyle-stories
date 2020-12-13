package com.soyle.stories.prose.proseEditor

import com.soyle.stories.gui.View
import com.soyle.stories.prose.usecases.readProse.ReadProse

class ProseEditorPresenter(
    private val view: View.Nullable<ProseEditorViewModel>
) : ReadProse.OutputPort {

    override suspend fun receiveProse(response: ReadProse.ResponseModel) {
        view.update {
            ProseEditorViewModel(
                versionNumber = response.revision,
                content = response.body,
                mentions = response.mentions
            )
        }
    }

}