# csc413-tankgame


| Student Information |                 |
|:-------------------:|-----------------|
|  Student Name       | Bryan Lee       |
|  Student Email      | blee37@sfsu.edu |


## Purpose of jar Folder 
The jar folder will be used to store the built jar of your term-project.

`NO SOURCE CODE SHOULD BE IN THIS FOLDER. DOING SO WILL CAUSE POINTS TO BE DEDUCTED`

`THIS FOLDER CAN NOT BE DELETED OR MOVED`

# Required Information when Submitting Tank Game

## Version of Java Used:
Oracle OpenJDK 21.0.02

## IDE used:
IntelliJ

## Steps to Import project into IDE: 
* Step 1: Click "Get from VCS" on IntelliJ "Projects" menu.</br>
* Step 2: Paste the GitHub "git" link into the "URL" and click clone.</br>

## Steps to Build your Project:
* Step 1: On the top-right corner, click the button that says "Current File" and "Edit Configuration."</br>
* Step 2: Click the "+" and create a new "Applicaton."
* Step 3: Change the name to "Launcher"
* Step 4: Under "Build and run" select the appropriate Java version & launcher class (i.e. TankGame.src.Launcher)
* Step 5: Click "Apply"

Note: Image step-by-step guide is shown in the documentation.

## Steps to run your Project:
* If you have built the project as followed above into IntelliJ, you can simply run the "Launcher" you have built.
* An alternative way is to directly compile the JAR
  * Step 1: Download and extract the project's zip from GitHub.
  * Step 2: In "Command Prompt" or "PowerShell" navigate to the correct directory that contains the "jar folder" but not within it.
  * Step 3: Execute the following command to run the project: `java -jar .\jar\csc413-tankgame-BryanL43.jar`

## Controls to play your Game:

|                                              | Player 1 | Player 2 |
|----------------------------------------------|----------|----------|
| Forward                                      | W        | U        |
| Backward                                     | S        | J        |
| Rotate left                                  | A        | H        |
| Rotate Right                                 | D        | K        |
| Shoot (Hold & Release when finished casting) | F        | L        |
| Previous Spell                               | Q        | Y        |
| Next Spell                                   | E        | I        |
| Recharge Spell (dismiss recharge included)   | R        | O        |

<!-- you may add more controls if you need to. -->