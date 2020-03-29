package com.soyle.stories.common

import com.soyle.stories.entities.CharacterArcTemplateSection
import java.util.*

/**
 * Created by Brendan
 * Date: 2/5/2020
 * Time: 3:59 PM
 */
fun template(name: String) = CharacterArcTemplateSection(
    CharacterArcTemplateSection.Id(UUID.randomUUID()), name
)

val PsychologicalWeakness = template("Psychological Weakness")
val MoralWeakness = template("Moral Weakness")
val PsychologicalNeed = template("Psychological Need")
val MoralNeed = template("Moral Need")
val Desire = template("Desire")
val ValuesOrBeliefs = template("Values or Beliefs")
val Opponent = template("Opponent")
val Plan = template("Plan")
val Battle = template("Battle")
val PsychologicalSelfRevelation = template("Psychological Self-Revelation")
val MoralSelfRevelation = template("Moral Self-Revelation")
val NewEquilibrium = template("New Equilibrium")

private const val Required = true
private const val Optional = false
private const val InComp = true
private const val NoComp = false
private const val Moral = true
private const val NotMoral = false
private const val Psych = true
private const val NotPsych = false
private const val Single = false
private const val Multi = true/*

fun String.template(isRequired: Boolean, inComp: Boolean, isMoral: Boolean, isPsych: Boolean, multi: Boolean, vararg subsections: String): CharacterArcSectionType {
	val (sectionType) =
	CharacterArcSectionType.define(this, isRequired, inComp, isMoral, isPsych, multi, subsections.toList()) as Either.Right
	return sectionType
}

val DefaultArcSectionSet = setOf(
	"Psychological Weakness"                    .template(Optional, InComp, NotMoral, Psych, Single),
	"Moral Weakness"                            .template(Optional, InComp, Moral, NotPsych, Single),
	"Psychological Need"                        .template(Required, InComp, NotMoral, Psych, Single),
	"Moral Need"                                .template(Required, InComp, Moral, NotPsych, Single),
	"Desire"                                    .template(Required, InComp, Moral, Psych, Single),
	"Values or Beliefs"                         .template(Optional, InComp, Moral, NotPsych, Single),
	"Problem"                                   .template(Optional, NoComp, NotMoral, Psych, Multi),
	"Opponent"                                  .template(Required, NoComp, NotMoral, Psych, Single),
	"Plan"                                      .template(Required, NoComp, NotMoral, Psych, Single),
	"Battle"                                    .template(Required, NoComp, Moral, Psych, Single),
	"Psychological Self-Revelation"             .template(Required, NoComp, NotMoral, Psych, Single),
	"Moral Self-Revelation"                     .template(Required, NoComp, Moral, NotPsych, Single),
	"New Equilibrium"                           .template(Required, NoComp, NotMoral, Psych, Single),
	"Immoral Action"                            .template(Optional, NoComp, Moral, NotPsych, Multi,
		"Criticism",
		"Justification"
	),
	"Attack by Ally"                            .template(Optional, NoComp, Moral, NotPsych, Multi,
		"Justification"
	),
	"Final Action Against Opponent"             .template(Optional, NoComp, Moral, NotPsych, Single),
	"Moral Decision"                            .template(Optional, NoComp, Moral, NotPsych, Single),
	"Initial Error"                             .template(Optional, NoComp, NotMoral, Psych, Single),
	"Ghost"                                     .template(Optional, NoComp, NotMoral, Psych, Multi),
	"Story World"                               .template(Optional, NoComp, NotMoral, Psych, Multi),
	"Ally"                                      .template(Optional, NoComp, NotMoral, Psych, Multi),
	"Inciting Incident"                         .template(Optional, NoComp, NotMoral, Psych, Single),
	"Fake-Ally Opponent"                        .template(Optional, NoComp, NotMoral, Psych, Multi),
	"Revelation"                                .template(Optional, NoComp, NotMoral, Psych, Multi,
		"Decision",
		"Changed Desire",
		"Changed Motive"
	),
	"Opponent's Main Plan and Counter-attack"   .template(Optional, NoComp, NotMoral, Psych, Single),
	"Apparent Defeat"                           .template(Optional, NoComp, NotMoral, Psych, Multi),
	"Audience Revelation"                       .template(Optional, NoComp, NotMoral, Psych, Multi),
	"Grave, Gauntlet, Visit to Death"           .template(Optional, NoComp, NotMoral, Psych, Multi),
	"Drive"                                     .template(Optional, NoComp, Moral, Psych, Multi)
)
  */