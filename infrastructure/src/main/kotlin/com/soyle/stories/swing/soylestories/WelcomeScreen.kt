package com.soyle.stories.swing.soylestories

import javax.swing.BoxLayout
import javax.swing.JFrame
import javax.swing.JPanel

class WelcomeScreen {

	private val frame = JFrame().apply {
		defaultCloseOperation = JFrame.HIDE_ON_CLOSE


		contentPane.layout = BoxLayout(contentPane, BoxLayout.Y_AXIS)



		setLocationRelativeTo(null)
		isVisible = true
	}

}