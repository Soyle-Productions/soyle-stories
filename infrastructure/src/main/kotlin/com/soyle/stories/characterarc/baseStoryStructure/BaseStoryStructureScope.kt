/**
 * Created by Brendan
 * Date: 3/2/2020
 * Time: 5:35 PM
 */
package com.soyle.stories.characterarc.baseStoryStructure

import com.soyle.stories.di.characterarc.BaseStoryStructureComponent
import com.soyle.stories.project.ProjectScope
import kotlinx.coroutines.runBlocking
import tornadofx.Scope
import tornadofx.find
import tornadofx.runAsync

class BaseStoryStructureScope(val projectScope: ProjectScope, val characterId: String, val themeId: String) : Scope() {

    init {
        val baseStoryStructureViewListener = find<BaseStoryStructureComponent>(scope = this).baseStoryStructureViewListener
        runAsync {
            runBlocking {
                baseStoryStructureViewListener.getBaseStoryStructure()
            }
        }
    }

}