Compile with:
	javac MPerzeptron.java

Run with:
	java MPerzeptron

Parameters can be set in the main function.

The size of the network is defined by the array
	int[] neurons
Containing the number of neurons per each layer.
e.g. a network with 3 input neurons, one hidden layer with 4 neurons and 5 ouput neurons, you set
	int[] neurons = {3,4,5};

similar to that you define the learning rate and transfer function per layer in the arrays
	TransferFunction[] transfer
and
	double[] learningRate
Although, these start with the second layer. Choices for the transfer functions are
Fermi function
	TransferFunction.FERMI
Hyperbolic Tangent
	TransferFunction.TANH
Identity
	TransferFunction.ID
	
Specify the training and test file in
	String trainingFile
and
	String testFile
	
A seed can be set to replicate former results. This seed is used to initialize all random number generators used in the network.

The number of iterations (how often each pattern is trained) can be set when calling the train() method.

