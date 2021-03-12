# Summary
abs

## Preconditions:
1. A scene has been created
2. The character has been included in the scene

## Basic Course of Events:
1. The user indicates that they want to cover character arc sections for a character in this scene
2. The software responds by listing all the available character arcs for this character and marks any previously
covered character arc sections
3. The user specifies which character arc sections should be covered by this scene for this character
4. The software covers the specified character arc sections in this scene for this character

## Alternative Paths:
1. In step 3, the user requests making a new character arc for this character first.
    1. The software responds by requesting the name of the new character arc and presenting a list of
    soon-to-be-created character arc sections
    2. The user inputs the name of the new character arc and selects one or more of the soon-to-be-created character
    arc sections
    3. The software creates the new character arc and the default character arc sections for the character then
    covers the specified, newly-created character arc sections in this scene for this character

2. In step 1b, the user requests making a new character arc section while making the new character arc.
    1. The software responds by presenting a list of available character arc section types.
    2. The user selects one of the presented character arc section types.
    3. The user may repeat steps 2 - 2.2 multiple times.
    4. The software creates the new character arc, the default character arc sections, and the selected types of
    character arc sections for the character, then covers the specified, newly-created character arc sections in this
    scene for this character

3. In step 3, the user specifies which character arc sections to uncover.  The post conditions are the same, but the
software also uncovers the specified character arc sections

4. In step 3, the user requests making a new character arc section for one of the listed character arcs.
    1. The software responds by presenting a list of available character arc section types for that character arc.
    2. The user selects one of the presented character arc section types.
    3. The software creates the specified character arc section type for the specified character arc and covers it in
    this scene for this character.