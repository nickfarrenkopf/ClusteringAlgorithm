package KMeans;
import java.util.ArrayList;
import java.util.Random;
import Math.Matrix;
import static KMeans.Constants.*;

/**
 * KMeans is a clustering algorithm that iterates through a data set to find the center (centroids) of the data.
 * Each centroid is initialized to a random data point, then each point is labeled with its closest vertex.
 * Each iteration consists of updating the centroids position (average of all its corresponding data points),
 * then updating which centroid is the closest. 
 * This algorithm is not guaranteed to find the optimal cluster, so several runs are repeated.
 * @author Nick Farrenkopf
 */
public class KMeans {

	// Hold data vectors
	private Matrix data;
	private int[] dataCentroidIndex;
	
	// Centroid variables
	private int numberCentroids;
	private Matrix centroids;
	private ArrayList<Matrix> oldCentroids;

	///// CONSTRUCTORS /////

	/**
	 * Initializes a K Means variable given a data set and number of centroids.
	 * It then randomizes the centroids and updates each data point's closest centroid.
	 * @param matrixData - Matrix of data
	 * @param numCentroids - integer number of centroids
	 */
	public KMeans(Matrix matrixData, int numCentroids)
	{
		// Set data
		data = matrixData.copy();
		numberCentroids = numCentroids;

		// Initialize centroid index to 0
		dataCentroidIndex = new int[matrixData.numRows()];
		for (int i=0; i<matrixData.numRows(); i++)
			dataCentroidIndex[i] = 0;

		// Initialize centroids
		randomizeCentroids();
		updateClosestCentroid();
		
		// Initialize list containing all centroids
		oldCentroids = new ArrayList<>();
		oldCentroids.add(centroids.copy());
	}
	
	///// K MEANS METHODS /////

	/**
	 * Initialize all centroids to a random data point.
	 */
	public void randomizeCentroids()
	{
		// Place holder variables
		int newIndex;
		ArrayList<Integer> indexes = new ArrayList<>();
		Random rand = new Random();

		// Choose random number until not in indexes, then add to indexes
		while (indexes.size() != numberCentroids)
		{
			newIndex = rand.nextInt(data.numRows());
			if (!indexes.contains(newIndex))
				indexes.add(newIndex);
		}	

		// Set centroids to random vertices
		centroids = new Matrix(numberCentroids, data.numCols());
		for (int i=0; i<numberCentroids; i++)
			centroids.setRowVector(i, data.getRowVector(indexes.get(i)));
	}
	
	/**
	 * Iterates through the data set and finds the closest centroid, setting it in data centroid index.
	 */
	public void updateClosestCentroid()
	{
		// Holds index of minimum centroid and distance
		int minIndex;
		double minDist, newDist;
		
		// Iterate through all the data points
		for (int i=0; i<data.numRows(); i++)
		{
			// Sets the current minimum value for closest centroid
			minIndex = dataCentroidIndex[i];
			minDist = data.getRowVector(i).minus(centroids.getRowVector(minIndex)).squareSum();
			
			// Iterate through centroids to find minimum distance
			for (int j=0; j<numberCentroids; j++)
			{
				// If new distance is smaller, update closest centroid
				newDist = data.getRowVector(i).minus(centroids.getRowVector(j)).squareSum();
				if (newDist < minDist)
				{
					dataCentroidIndex[i] = j;
					minDist = newDist;
				}
			}
		}
	}
	
	/**
	 * Iterates through data, summing position if it belongs to a certain centroid.
	 * Average is then found by dividing by counter.
	 */
	public void updateCentroidMeans()
	{
		// Initializes sum and count variables
		Matrix sum = new Matrix(centroids.numRows(), centroids.numCols(), 0);
		double[] count = new double[centroids.numRows()];
		
		// Iterate through data
		for (int i=0; i<data.numRows(); i++)
		{
			// Set variables to zero
			int index = dataCentroidIndex[i];
			
			// Add data vector to sum
			sum.setRowVector(index, sum.getRowVector(index).add(data.getRowVector(i)));
			
			// Increase counter
			count[index]++;
		}

		// Find average of sum rows and set to centroids
		for (int i=0; i<centroids.numRows(); i++)
			if (count[i] != 0)
				centroids.setRowVector(i, sum.getRowVector(i).mult(1 / count[i]));
		
		// Save new centroids in all centroids
		oldCentroids.add(centroids.copy());
	}
	
	/**
	 * Runs one iteration of K Means consisting of updating means, then updating closest centroid
	 */
	public void Iterate()
	{
		updateCentroidMeans();
		updateClosestCentroid();
	}
	
	///// CONVERGENCE METHODS /////

	/**
	 * Checks if K Means has converged by comparing the last two Matrix centroids in old centroids array list
	 * @return boolean
	 */
	public boolean isConverged()
	{	
		if (oldCentroids.size() >= 2)
			return (oldCentroids.get(oldCentroids.size() - 1).minus(oldCentroids.get(oldCentroids.size() - 2)).squareSum() == 0);
		return false;
	}
	
	/**
	 * Iterates K Means until converged
	 */
	public void converge()
	{
		for (int i=0; i<maxNumberIterations; i++)
		{
			// Iterates 
			Iterate();
			
			// If they are equal, exit
			if (isConverged())
				i = maxNumberIterations;
		}
	}
	
	/**
	 * Runs through a number of K Means objects, finding converged centroids.
	 * Each iteration adds centroids to array list if array list did not contain
	 * or increases the counter for how often centroids appear.
	 * This array list/boolean implementation necessary because similar matricies
	 * can be different object, thus not handling .contains() well.
	 */
	public void runAll()
	{
		// Array lists to hold converged centroids and counter
		ArrayList<Matrix> allCents = new ArrayList<>();
		ArrayList<Integer> allCounter = new ArrayList<>();
		
		// Boolean to check if loop increments counter or not
		boolean incremented = false;
		
		// While less than max number of iterations
		KMeans km;
		Matrix cents;
		for (int i=0; i<maxNumberIterations; i++)
		{
			// Initializes new KMeans object, iterates until converged, then grabs centroids
			km = new KMeans(data, numberCentroids);
			km.converge();
			cents = km.getCentroids().sortByRows();

			// If Array List contains matrix similar to current centroids, increase counter
			incremented = false;
			for (int j=0; j<allCents.size(); j++)
				if (cents.minus(allCents.get(j)).squareSum() < epsilon)
				{
					allCounter.set(j, allCounter.get(j) + 1);
					incremented = true;
				}
			
			// If counter was not incremented, add new centroids
			if (!incremented)
			{
				allCents.add(cents);
				allCounter.add(0);
			}
		}
		
		// Finds centroids with maximum counter
		int maxIndex = 0;
		for (int i=1; i<allCounter.size(); i++)
			if (allCounter.get(i) > allCounter.get(maxIndex))
				maxIndex = i;
		
		// Updates current K Means with max centroids
		oldCentroids.clear();
		centroids = allCents.get(maxIndex);
		updateClosestCentroid();
		updateCentroidMeans();
	}
	
	///// GETTERS /////
	
	/**
	 * Returns matrix of data
	 * @return Matrix
	 */
	public Matrix getData()
	{
		return data;
	}

	/**
	 * Returns array of ints that labels each data point with a specified centroid
	 * @return int[]
	 */
	public int[] getDataCentroidIndex()
	{
		return dataCentroidIndex;
	}

	/**
	 * Returns most recent centroids in Matrix form
	 * @return Matrix
	 */
	public Matrix getCentroids()
	{
		return centroids;
	}
	
	/**
	 * Returns array list of matricies that is collection of all past centroids
	 * @return allCentroids (ArrayList<Matrix>)
	 */
	public ArrayList<Matrix> getAllCentroids()
	{
		return oldCentroids;
	}
}