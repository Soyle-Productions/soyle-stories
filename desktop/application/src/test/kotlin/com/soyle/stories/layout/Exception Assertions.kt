package com.soyle.stories.layout

import com.soyle.stories.layout.entities.Layout
import com.soyle.stories.layout.usecases.getSavedLayout.GetSavedLayout


fun assertLayoutDoesNotExist(): (Any?) -> Unit = { actual ->
	actual as LayoutDoesNotExist
}

fun assertResponseModel(layout: Layout): (Any?) -> Unit = { actual ->
	actual as GetSavedLayout.ResponseModel

}