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

This is the most interesting pane since it's the place where you will test your
newly created robot/playground. 

### How to play

* Set the playground. Select one from the dropdown list in the Playground selection pane
* Select the Robot and add it to the playground.
* Update the initial position of the robot by playing with de X-Y values (optional) 
* Click the **Start Simulation** button

### Robot cartography
After simulation starts you will see the robot representation of explored world in
an image displayed below the **Start Simulation** button.

## Playground configuration
![alt](https://raw.githubusercontent.com/sergio-alves/RobotSimulator/master/res/playground.png)

In the second tab pane you will be allowed to create new playgrounds. 

### Tile selection and drawing
Actually you can only select one of the three types of tiles:

* Normal tile, the robot can walk on
* Void tile, the void detectors will detected this kind of ground. The robot cannot walk on.
* Wall tile, the long range subsonic detectors will detected this tiles. The robot cannot go through.

You can select the tile type to use for painting in the dropdown box selector, 
the last one in the right part of the interface.

To create the playground, select one tile type from dropdown described above, 
press the mouse left button and keep pressed on the starting tile and drag the 
mouse over it to the end tile (like a bitmap editor) and release the mouse left
button. The grid tiles will be painted into green if the tile type is *Wall* in
black if *Void* and interface grey if *Normal*.

### Grid bounds
You can select the grid width x grid height x tile size of your playground by setting
the respective values to fields.

###Save and Load Playgrounds
To save input the playground name in the textbox above the *Save* button and click on it.

You have the possiblity to update a previously created playground. Select it in 
the list above the *Load* button and click on *Load* button
