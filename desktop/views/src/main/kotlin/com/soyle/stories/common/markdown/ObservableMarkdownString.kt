package com.soyle.stories.common.markdown

import javafx.beans.value.ObservableValue

class ObservableMarkdownString(observableValue: ObservableValue<String>) : ObservableValue<String> by observableValue