package com.soyle.studio.common

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test

class EntityTest {

    class EntityType1(override val id: String, val name: String = "") : Entity<String>
    class EntityType2(override val id: String?) : Entity<String?>

    @Test
    fun differentTypesOfEntitiesAreNotTheSame()
    {
        assertFalse(EntityType1("1") isSameEntityAs EntityType2("2"))
    }

    @Test
    fun sameEntityIsSame()
    {
        val e = EntityType1("1")
        assertTrue(e isSameEntityAs e)
    }

    @Test
    fun entityOfSameTypeWithDifferentValuesAreDifferent()
    {
        val e1 = EntityType1("1")
        val e2 = EntityType1("2", "Other")
        assertFalse(e1 isSameEntityAs e2)
    }

    @Test
    fun entityWithSameIdIsSame()
    {
        val e1 = EntityType1("1")
        val e2 = EntityType1("1", "Other")
        assertTrue(e1 isSameEntityAs e2)
    }

    @Test
    fun entitiesWithNullIdsAreNeverSame()
    {
        val e1 = EntityType2(null)
        val e2 = EntityType2(null)
        assertFalse(e1 isSameEntityAs e2)
    }
}