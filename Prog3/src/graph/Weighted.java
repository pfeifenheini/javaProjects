package graph;

class Weighted
{
	int nodes;
	double[][] edges;
	
	Weighted(int n)
	{
		int i, j;
		edges = new double[n][n];
		for(i=0;i<n;i++)
		{
			for(j=0;j<n;j++)
			{
				edges[i][j] = -1;
			}
		}
	}
	
	public void fillRandom(double dichte)
	{
		int i, j;
		double d = dichte/100, tmp=0;
		for(i=0;i<nodes;i++)
		{
			for(j=0;j<nodes;j++)
			{
				tmp = Math.random();
				if(tmp > (1-d))
				{
					edges[i][j] = Math.random()*100;
				}
			}
		}
	}
}