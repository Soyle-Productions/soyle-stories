/**
 * Created by Brendan
 * Date: 3/3/2020
 * Time: 5:19 PM
 */
package com.soyle.stories.characterarc.eventbus

import com.soyle.stories.eventbus.Notifier
import com.soyle.stories.theme.usecases.includeCharacterInComparison.IncludeCharacterInComparison

class IncludeCharacterInComparisonNotifier : Notifier<IncludeCharacterInComparison.OutputPort>(), IncludeCharacterInComparison.OutputPort {
    override fun receiveIncludeCharacterInComparisonFailure(failure: Exception) {
        notifyAll {
            it.receiveIncludeCharacterInComparisonFailure(failure)
        }
    }

    override fun receiveIncludeCharacterInComparisonResponse(response: IncludeCharacterInComparison.ResponseModel) {
        notifyAll {
            it.receiveIncludeCharacterInComparisonResponse(response)
        }
    }
}