package com.soyle.stories.scene.createNewSceneDialog

data class CreateNewSceneDialogViewModel(
  val title: String,
  val nameLabel: String,
  val errorMessage: String?,
  val success: Boolean = false
)