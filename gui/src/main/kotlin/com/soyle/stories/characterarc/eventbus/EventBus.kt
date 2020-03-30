/**
 * Created by Brendan
 * Date: 3/14/2020
 * Time: 1:38 PM
 */
package com.soyle.stories.characterarc.eventbus

import com.soyle.stories.character.usecases.buildNewCharacter.BuildNewCharacter
import com.soyle.stories.character.usecases.removeCharacterFromLocalStory.RemoveCharacterFromLocalStory
import com.soyle.stories.characterarc.usecases.deleteLocalCharacterArc.DeleteLocalCharacterArc
import com.soyle.stories.characterarc.usecases.planNewCharacterArc.PlanNewCharacterArc
import com.soyle.stories.eventbus.Notifier
import com.soyle.stories.theme.usecases.changeCentralMoralQuestion.ChangeCentralMoralQuestion
import com.soyle.stories.theme.usecases.changeCharacterPerspectivePropertyValue.ChangeCharacterPerspectivePropertyValue
import com.soyle.stories.theme.usecases.changeCharacterPropertyValue.ChangeCharacterPropertyValue
import com.soyle.stories.theme.usecases.changeStoryFunction.ChangeStoryFunction
import com.soyle.stories.theme.usecases.changeThematicSectionValue.ChangeThematicSectionValue
import com.soyle.stories.theme.usecases.includeCharacterInComparison.IncludeCharacterInComparison
import com.soyle.stories.theme.usecases.promoteMinorCharacter.PromoteMinorCharacter
import com.soyle.stories.theme.usecases.removeCharacterFromComparison.RemoveCharacterFromLocalComparison

interface EventBus {

    val buildNewCharacter: Notifier<BuildNewCharacter.OutputPort>
    val planNewCharacterArc: Notifier<PlanNewCharacterArc.OutputPort>
    val includeCharacterInComparison: Notifier<IncludeCharacterInComparison.OutputPort>
    val promoteMinorCharacter: Notifier<PromoteMinorCharacter.OutputPort>
    val deleteLocalCharacterArc: Notifier<DeleteLocalCharacterArc.OutputPort>
    val removeCharacterFromStory: Notifier<RemoveCharacterFromLocalStory.OutputPort>
    val changeStoryFunction: Notifier<ChangeStoryFunction.OutputPort>
    val changeThematicSectionValue: Notifier<ChangeThematicSectionValue.OutputPort>
    val changeCentralMoralQuestion: Notifier<ChangeCentralMoralQuestion.OutputPort>
    val changeCharacterPropertyValue: Notifier<ChangeCharacterPropertyValue.OutputPort>
    val changeCharacterPerspectivePropertyValue: Notifier<ChangeCharacterPerspectivePropertyValue.OutputPort>
    val removeCharacterFromLocalComparison: Notifier<RemoveCharacterFromLocalComparison.OutputPort>

}