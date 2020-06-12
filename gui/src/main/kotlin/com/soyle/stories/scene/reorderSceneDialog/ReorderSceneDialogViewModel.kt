package com.soyle.stories.scene.reorderSceneDialog

data class ReorderSceneDialogViewModel(
  val title: String,
  val header: String,
  val content: String,
  val reorderButtonLabel: String,
  val cancelButtonLabel: String,
  val showAgainLabel: String,
  val errorMessage: String?,
  val doDefaultAction: Boolean?
)