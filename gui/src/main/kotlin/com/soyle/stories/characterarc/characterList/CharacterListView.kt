/**
 * Created by Brendan
 * Date: 3/14/2020
 * Time: 6:55 PM
 */
package com.soyle.stories.characterarc.characterList

interface CharacterListView {

    suspend fun displayNewViewModel(list: CharacterListViewModel)
    suspend fun invalidate()
    suspend fun getViewModel(): CharacterListViewModel?

}