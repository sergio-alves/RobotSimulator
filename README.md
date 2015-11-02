# RobotSimulator

The RobotSimulator, as you can imagine, is a robot simulation software that allows
 you to generate, playgrounds and robots as well as testing them in a single software. 
The goal of this software is to have the ability to quickly create and test new 
algorithms that should be deployed (after java -> c++ conversion) on my arduino
based double wheeled robot.

The interface is splitted into 3 panes (names should change slightly).

* Sandbox
* Playground Configuration
* Robot Building

## Sandbox

![alt](https://raw.githubusercontent.com/sergio-alves/RobotSimulator/master/res/exec.png)
This is the most interesting pane since it's the place where you will test you're
newly created robot/playground. 

To start the simulation, set a playground selected in the dropdown list in the 
Playground selection pane, then lets select the Robot (the algorithm developped 
before) and add it to the playground, you can update the initial position of the
robot by playing with de X-Y values (even after havind added it to the playground),
finally just click the **Start Simulation** button

