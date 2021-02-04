package com.soyle.stories.scene.deleteSceneDialog

data class DeleteSceneDialogViewModel(
  val title: String,
  val header: String,
  val content: String,
  val deleteButtonLabel: String,
  val cancelButtonLabel: String,
  val errorMessage: String?,
  val showAgain: Boolean?
)