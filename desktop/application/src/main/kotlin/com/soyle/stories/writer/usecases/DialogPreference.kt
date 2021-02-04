package com.soyle.stories.writer.usecases

import com.soyle.stories.writer.DialogType

data class DialogPreference(
  val id: DialogType,
  val shouldShow: Boolean
)