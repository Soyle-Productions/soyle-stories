/**
 * Created by Brendan
 * Date: 3/2/2020
 * Time: 5:33 PM
 */
package com.soyle.stories.characterarc.baseStoryStructure

interface BaseStoryStructureViewListener {

    fun getBaseStoryStructure()
    fun changeSectionValue(sectionId: String, value: String)

}