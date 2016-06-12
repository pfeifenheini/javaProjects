<<<Instructions using a Linux OS >>>

> Navigate to the folder containing all source files:
EvoTSP.java
Population.java
Tour.java
City.java

> Compile command
javac EvoTSP.java

> Run command
java EvoTSP

> Enter parameters:
P         - population size
mu        - number of parents
lambda    - number of offspring, enter '0' to set to P-mu
file path - path to the file containing the cities
            path can be relative to the source folder
            format: one city per line '<id> <x-coordinate> <y-coordinate>'
			ids need to go in order from 1 to <total number of cities>
			lines starting with '#' are ignored

> After the simulation started, the status of the first 20
> generations will be given. After that on of the following
> commands can be entered:

help       - prints the list of commands
status / s - gives an overview of the current simulation:
             best tour,
			 worst tour,
			 mean length,
			 number of generations
best       - gives the currently best strategy: <length> | <list of city ids in visited order>
stop       - stops the simulation

> A file named 'output.txt' is created, containing the first
> 1000 generations in the following format:

output.txt - one generation per line:
             '<generation> <length of the best tour> <length of the worst tour> <mean length of all tours>'
			 separated by whitespace

<<< Plotting data >>>

> The development of each generation can be plottet with
> gnuplot using the data provided in the file 'output.txt'.
> Start gnuplot by typing 'gnuplot' (given it is installed)
> and use the following commands:

unset logscale x
unset logscale y
set xlabel "Generation"
set ylabel "Length"
plot "output.txt" using 1:2 title 'best' with steps, \
"output.txt" using 1:3 title 'worst' with steps, \
"output.txt" using 1:4 title 'mean' with steps

> Using a logscale for the x-axis emphasizes the early development

set logscale x
unset logscale y
set xrange [1:*]
set xlabel "Generation"
set ylabel "Length"
plot "output.txt" using 1:2 title 'best' with steps, \
"output.txt" using 1:3 title 'worst' with steps, \
"output.txt" using 1:4 title 'mean' with steps