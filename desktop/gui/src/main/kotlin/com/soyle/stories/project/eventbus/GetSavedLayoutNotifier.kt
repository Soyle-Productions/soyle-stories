package com.soyle.stories.project.eventbus

import com.soyle.stories.common.Notifier
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.layout.usecases.getSavedLayout.GetSavedLayout

/**
 * Created by Brendan
 * Date: 2/15/2020
 * Time: 6:35 PM
 */
class GetSavedLayoutNotifier(
    private val threadTransformer: ThreadTransformer
) : GetSavedLayout.OutputPort, Notifier<GetSavedLayout.OutputPort>() {
    override fun receiveGetSavedLayoutResponse(response: GetSavedLayout.ResponseModel) {
        threadTransformer.async {
            notifyAll {
                it.receiveGetSavedLayoutResponse(response)
            }
        }
    }
}