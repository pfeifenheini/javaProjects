I implemented a Multi NGAS.

Compile with:
	javac MultiNGAS.java

Run with:
	java MultiNGAS
optional, you can add a seed as a parameter (of type long). Otherwise it will use an arbitrary seed.

The program will ask you for the dimension. Choosing dimension=2, will generate an example with 3 areas from the unit square.
Two circles and a rectangle. The two circles have centers (0.3,0.75) and (0.7,0.75) and radius 0.15 and
the rectangle has the dimensions [0.2,0.8][0.2,0.376715868]. Note that the rectangle has the same area as each circle.

With a fixed size for the Gaussian, I got unsatisfying results, so I implemented it decaying over time as well.
The initial value (sigmaInit) and final value (sigmaFinal) are adjustable. If you insist on a fixed size, you can
simply set both variable to the same value.

After you ran the demo, you can use the files "demoPlot2D.gp" or "demoPlot3D.gp" with gnuplot, to visualize the results.
The centers of each partner net are saved in separate files, so they can be told apart in the plot. The set of points
that were used for training are depicted as well.
Additionally, the centers of ALL neurons are saved in the file "PA-D.net" as demanded.