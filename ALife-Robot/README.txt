<< Compile command >>
javac ALifeRobot.java

<< Run commant >>
java ALifeRobot

The file containing the grid information has to be in the same directory named 'SS16-4201-PA-F.grid'.

<< Usage >>
The buttons should be self explanatory. You can set and delete walls in the grid by left/right click. Also you can click and drag the robot to a new position. Clicking left/right on the robot will turn it. The current direction is given by the the small arc in front of the robot.

<< Behavior Breitenberg 3b >>
The robot can sense two cells to the left and two to the right of its current position, e.g.

X - robot
L - left cell
R - right cell
O - other cell

When the robot is facing north:

   LOR
   LXR
   OOO

When the robot is facing north east:

   LLO
   OXR
   OOR

It will take the difference of blocked left cells and blocked right cells to determine its next position.
If the difference is 0, the robot will just go one cell forward.
If the difference is negative, more walls are on the right and the robot will first turn left and then go one cell forward.
Analogously if the difference is positive.
If the next cell is itself a wall, the robot will not move in the direction and just continue turning.

With this behavior, the robot gets stuck very fast when the next cell is blocked and at the same time there is the same number of walls on the left and on the right side. So in this case, the robot will turn randomly right or left to escape this situation. One could interpret this behavior as sensor errors.

<< Behavior Wall Following >>

It follows the wall with left hand rule.
