package com.soyle.stories.theme.includeCharacterInTheme

import com.soyle.stories.common.Notifier
import com.soyle.stories.theme.usecases.includeCharacterInComparison.CharacterIncludedInTheme
import com.soyle.stories.theme.usecases.includeCharacterInComparison.IncludeCharacterInComparison

class IncludeCharacterInComparisonNotifier : Notifier<IncludeCharacterInComparison.OutputPort>(), IncludeCharacterInComparison.OutputPort {
    override fun receiveIncludeCharacterInComparisonFailure(failure: Exception) {
        notifyAll {
            it.receiveIncludeCharacterInComparisonFailure(failure)
        }
    }

    override fun receiveIncludeCharacterInComparisonResponse(response: CharacterIncludedInTheme) {
        notifyAll {
            it.receiveIncludeCharacterInComparisonResponse(response)
        }
    }
}