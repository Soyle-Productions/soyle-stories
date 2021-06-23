package com.soyle.stories.desktop.view.location.create

import com.soyle.stories.common.components.ComponentsStyles
import com.soyle.stories.common.components.text.TextStyles
import com.soyle.stories.desktop.view.common.NodeAccess
import com.soyle.stories.location.createLocationDialog.CreateLocationDialog
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextInputControl
import tornadofx.SimpleMessageDecorator
import tornadofx.Stylesheet
import tornadofx.decorators

class CreateLocationDialogViewAccess private constructor(view: CreateLocationDialog.View) : NodeAccess<CreateLocationDialog.View>(view) {
    companion object {
        fun CreateLocationDialog.View.access(): CreateLocationDialogViewAccess = CreateLocationDialogViewAccess(this)
    }

    val nameInput: TextInputControl by mandatoryChild(CreateLocationDialog.Styles.name)

    val nameLabel: Label by nameInput.parent.mandatoryChild(TextStyles.fieldLabel)

    val nameError: SimpleMessageDecorator?
        get() = nameInput.decorators.filterIsInstance<SimpleMessageDecorator>().singleOrNull()

    val descriptionInput: TextInputControl by mandatoryChild(CreateLocationDialog.Styles.description)

    val descriptionLabel: Label by descriptionInput.parent.mandatoryChild(TextStyles.fieldLabel)

    val createButton: Button by mandatoryChild(Stylesheet.button) { it.isDefaultButton }

    val cancelButton: Button by mandatoryChild(Stylesheet.button) { it.isCancelButton }

}