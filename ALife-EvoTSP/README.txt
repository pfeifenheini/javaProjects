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
threads   - set the number of independent simulations
file path - path to the file containing the cities
            path can be relative to the source folder
            format: one city per line '<int:id> <int:x-coordinate> <int:y-coordinate>'
			ids need to go in order from 1 to <total number of cities>
			lines starting with '#' are ignored

> After the simulation started, the status is printed to the
> standard output whenever the best value has improved. At any
> time one of the following commands can be entered:

help       - prints the list of commands
status / s - gives an overview of the current simulations
best       - gives the currently best strategy: <length> | <list of city ids in visited order>
gnuplot    - generates for each thread a file with gnuplot commands
             to plot the development of the population. The data can
             be plotted while the simulation is still running
restart    - reinitializes the program
stop       - stops the simulation

> For each thread, a file named 'output<threadID>.data' is created,
> containing the development of the generations in the following format:

output.data - one generation per line:
              '<int:generation> <double:length of the best tour> <double:length of the worst tour> <double:mean length of all tours>'
			  separated by whitespace
			 
> A new line is only added after the best value has improved.
> Otherwise the files would become very large very quickly.
> It means on the other hand that the development of the worst
> and mean value are not tracked for every intermediate generation. 

<<< Plotting data >>>

> The development of each generation can be plottet with
> gnuplot using the data provided in the file 'output<threadID>.data'.
> For that you can use the 'gnuplot' command during the simulation
> to generate files 'output<threadID>.plot' that cyn be loaded
> with gnuplot to plot the data of the corresponding 'output<threadID>.data

<<< Result >>>

> When the simulation is stopped, the best tour is shown and
> saved in the file 'bestTour.txt'