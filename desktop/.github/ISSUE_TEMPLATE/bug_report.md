---
name: Bug report
about: File a new bug
title: ''
labels: bug
assignees: b-camphart

---

### Title
The title should be only as long as is needed to describe what's wrong.  If you think about the bug in terms of a UAT, which of the 'Then's are failing?  The title should be the inverse of that 'Then' statement.

**Example:** 
Given ....
When the button is clicked on
Then the dialog should be opened

Thus the title is: The dialog is not open after clicking on the button.

### Description
The description should contain the steps to reproduce the issue.  Think of this like writing a new UAT.  The difference is in how the steps are described and how many.  The steps should be all the 'Given' statements and a final 'When' statement with the expected result being the failing 'Then' statement.  However, do not use the 'Given' 'When' and 'Then' words.

**Example:**
If the UAT would be written like this:

    Given a project has been opened
    And a Location has been created
    And the Location List Tool has been opened
    When a Location is right-clicked
    Then the Location right-click menu should be open

Then the steps for filing the bug should look like this:

    1. Open a project
    2. Create a Location
    3. Open the Location List Tool
    4. Right-click the Location

Expected Result: The Location right-click menu should be open
Actual Result: The location right-click menu is not open

*delete everything above this line*
___
**Steps to Reproduce**

1.

**Expected Result**

**Actual Result**

**Additional Information**

___
*delete everything blow this line*

### Additional Information
It's important that we organize these bugs so nothing is lost.  To that end, whenever you file a new bug, please make sure you do the following:

**Projects** - Add to the `Bug Priority` project.  After submitting, select the priority.  High priority is reserved for system-breaking issues; if something completely blocks the ability to perform a given function, it is high-priority.  If something is blocked, but there are ways to work around it, it's generally medium priority.  If it's a visual glitch or a nuisance, it's low priority.
