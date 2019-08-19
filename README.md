# LogicGateSimulator
A simple program to test your Logic Gate idea's

## Technical difficulties/hazards
#### Communication between UI thread and Logic thread 
Since the bulk of the two main tasks (drawing images and logic calculations) are mostly independent of each other, which means we can parallelize them. This comes with many hazards in timing and concurrency, but the end result makes it worth the effort.

#### Optimization of the Logic thread
After many versions and iterations of optimizations, there still seems to be a non-repeatable hazard in the 'Quick logic' which results in the undesired 'non-deterministic' behavior. A temporary fix for this is the implementation of 'Safe logic' which is a couple orders of magnitude slower, but still gets the job done (running with an acceptable update frequency of 10 kHz).

#### Performance issues with many 'Dots' and 'Lines' on the screen
Drawing thousands upon thousands of Dots to the screen can cause performance issues in some cases. Most of these performance issues are a product of lazy writing, 

## Inspiration and goals
This project, created by Kamron Geijsen, started late 2015 as a home project using only basic Java libraries. The main inspiration behind the project was to replicate and improve Minecraft's Redstone logic, consisting of the basic building blocks of logic: NOR-gates, OR-gates and BUFFER-gates.
The long-term goal with this project was to design and create a working CPU (Central Processing Unit), which can run a custom ISA (Instruction Set Architecture). This goal was realized sooner than expected - in just three years since the project started. 
Since then, the focus has shifted towards the user-interface to make it accessible and useful for other users than just myself.

The code is far from the cleanest because it started off as a high school project. It for instance lacks formal tests and writing conventions. Now, that the project is starting to take shape, it is starting to be more prominent.

## Gallery

<img src="/docs/images/LGS exampleOld.png" height="300">
First working GUI version

<img src="/docs/images/LGS example1.png" height="300">
Simon Says in logic gates

<img src="/docs/images/LGS example2.png" height="300">
16-bit CPU compared to Simon Says