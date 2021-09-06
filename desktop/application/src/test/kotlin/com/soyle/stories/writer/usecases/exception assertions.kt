package com.soyle.stories.writer.usecases

import com.soyle.stories.usecase.writer.WriterNotRegistered
import org.junit.jupiter.api.Assertions.assertEquals
import java.util.*


fun writerNotRegistered(writerId: UUID): (Any?) -> Unit = { actual ->
	actual as WriterNotRegistered
	assertEquals(writerId, actual.writerId)
}