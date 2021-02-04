package com.soyle.stories.writer

import com.soyle.stories.entities.Writer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.*

class WriterUnitTest {

	@Test
	fun `id used as map key`() {
		val uuid = UUID.randomUUID()
		val id1 = Writer.Id(uuid)
		val id2 = Writer.Id(uuid)
		val map = mapOf(id1 to Unit)
		assertEquals(Unit, map[id2])
	}

	@Test
	fun `id string does not include property names`() {
		val uuid = UUID.randomUUID()
		val id = Writer.Id(uuid)
		assertEquals("Writer($uuid)", id.toString())
	}

}