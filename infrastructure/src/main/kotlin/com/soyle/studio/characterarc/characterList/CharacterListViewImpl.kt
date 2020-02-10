package com.soyle.studio.characterarc.characterList

import javafx.application.Platform
import javafx.scene.layout.Priority
import tornadofx.*

/**
 * Created by Brendan
 * Date: 2/10/2020
 * Time: 9:44 AM
 */
class CharacterListViewImpl : View("Characters"), CharacterListView {

    private val model = find<CharacterListModel>()
    private var viewModel = CharacterListViewModel(listOf())

    override val root = stackpane {
        hgrow = Priority.SOMETIMES
        this += find<PopulatedDisplay>()
        this += find<EmptyDisplay>()
    }

    override fun update(update: CharacterListViewModel.() -> CharacterListViewModel) {
        if (! Platform.isFxApplicationThread()) return runLater { update(update) }
        viewModel = viewModel.update()
        model.characters.setAll(viewModel.characters)
    }
}
