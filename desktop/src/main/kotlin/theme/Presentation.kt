package com.soyle.stories.desktop.config.theme

import com.soyle.stories.characterarc.moveCharacterArcSectionInMoralArgument.CharacterArcSectionMovedInMoralArgumentNotifier
import com.soyle.stories.characterarc.removeCharacterArcSectionFromMoralArgument.CharacterArcSectionRemovedNotifier
import com.soyle.stories.common.listensTo
import com.soyle.stories.desktop.config.locale.LocaleHolder
import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.domain.theme.valueWeb.ValueWeb
import com.soyle.stories.layout.config.dynamic.MoralArgument
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.theme.addCharacterArcSectionToMoralArgument.ArcSectionAddedToCharacterArcNotifier
import com.soyle.stories.theme.characterValueComparison.CharacterValueComparisonScope
import com.soyle.stories.theme.characterValueComparison.components.addValueButton.AddValueButton
import com.soyle.stories.theme.moralArgument.*
import com.soyle.stories.theme.valueWeb.create.CreateValueWebForm
import com.soyle.stories.theme.valueWeb.opposition.create.CreateOppositionValueForm
import com.soyle.stories.usecase.theme.addOppositionToValueWeb.OppositionAddedToValueWeb
import com.soyle.stories.usecase.theme.addValueWebToTheme.ValueWebAddedToTheme

object Presentation {

    init {
        scoped<MoralArgumentScope> {
            provide<MoralArgumentViewListener> {
                val presenter = MoralArgumentPresenter(
                    themeId,
                    get<MoralArgumentState>()
                )

                presenter listensTo projectScope.get<ArcSectionAddedToCharacterArcNotifier>()
                presenter listensTo projectScope.get<CharacterArcSectionMovedInMoralArgumentNotifier>()
                presenter listensTo projectScope.get<CharacterArcSectionRemovedNotifier>()

                MoralArgumentController(
                    themeId,
                    projectScope.applicationScope.get(),
                    projectScope.get(),
                    presenter,
                    projectScope.get(),
                    presenter,
                    projectScope.get(),
                    presenter,
                    projectScope.get(),
                    presenter,
                    projectScope.get(),
                    projectScope.get(),
                    projectScope.get(),
                    projectScope.get(),
                    projectScope.get(),
                    projectScope.get(),
                    projectScope.get()
                )
            }
        }
        scoped<ProjectScope> {
            provide<CreateValueWebForm.Factory> {
                object : CreateValueWebForm.Factory {
                    override fun invoke(
                        themeId: Theme.Id,
                        onCreateValueWeb: suspend (ValueWebAddedToTheme) -> Unit
                    ): CreateValueWebForm {
                        return CreateValueWebForm(
                            themeId,
                            onCreateValueWeb,
                            applicationScope.get<LocaleHolder>(),
                            get()
                        )
                    }
                }
            }
            provide<CreateOppositionValueForm.Factory> {
                object : CreateOppositionValueForm.Factory {
                    override fun invoke(
                        valueWebId: ValueWeb.Id,
                        onCreateOppositionValue: suspend (OppositionAddedToValueWeb) -> Unit
                    ): CreateOppositionValueForm {
                        return CreateOppositionValueForm(
                            valueWebId,
                            onCreateOppositionValue,
                            applicationScope.get<LocaleHolder>(),
                            get()
                        )
                    }
                }
            }
        }
        scoped<CharacterValueComparisonScope> {
            provide<AddValueButton.Factory> {
                object : AddValueButton.Factory {
                    override fun invoke(themeId: Theme.Id, characterId: Character.Id): AddValueButton {
                        return AddValueButton(
                            themeId,
                            characterId,
                            projectScope.applicationScope.get<LocaleHolder>(),
                            projectScope.get(),
                            projectScope.get(),
                            projectScope.get(),
                            projectScope.get()
                        )
                    }
                }
            }
        }
    }

}