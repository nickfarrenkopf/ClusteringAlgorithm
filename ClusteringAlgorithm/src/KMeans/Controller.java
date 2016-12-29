package KMeans;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JTabbedPane;
import KMeansGUI.DataCard;
import KMeansGUI.Frame;
import KMeansGUI.ScreenCard;
import KMeansGUI.TitlePage;
import KMeansGUI.InstructionsPage;
import Math.Matrix;
import static KMeans.Constants.*;

/**
 * Controller is a controller for the K Means GUI and algorithm. GUI is set in a frame and several cards are added
 * that allow user to change conditions of the KMenas clustering algorithm.
 * @author Nick Farrenkopf
 */
public class Controller {

	/**
	 * 
	 * Main method for program start.
	 * 
	 */
	public static void main(String[] args)
	{
		new Controller();
	}
	/**
	 * 
	 * 
	 * 
	 */
	
	// GUI variables
	private Frame frame;
	private ScreenCard sc;

	// KMeans variables
	private KMeans km;

	// Array to hold vector data
	private Matrix data;

	// Plotting variables
	private int[] plotIndexes;
	private double[] transformData;

	// Timer variables
	private Timer timer;
	private boolean keepRunning;

	// Message to user
	private String message;

	///// CONSTRUCTOR /////

	/**
	 * Initializes controller for K Means algorithm. Creates a frame and starts the title page,
	 * then initializes some variables
	 */
	public Controller()
	{
		// Initializes title page
		frame = new Frame();
		frame.add(new TitlePage(this));
		frame.revalidate();
		
		// Initialize variables
		km = null;
		message = "";
		timer = new Timer();
		plotIndexes = new int[] {0, 1};
		keepRunning = false;
	}

	///// GUI /////

	/**
	 * Initializes instructions page by creating and adding to frame
	 */
	public void InstructionsPage() 
	{
		frame.add(new InstructionsPage(this));
		frame.revalidate();
	}

	/**
	 * Initializes main screen for program by creating cards and adding to JTabbedPane
	 */
	public void InitializeScreen() 
	{
		// Create panels
		sc = new ScreenCard(this);
		DataCard dc = new DataCard(this); 

		// Sets card layout
		JTabbedPane cardLayout = new JTabbedPane();
		cardLayout.add(dc, "Data Selection");
		cardLayout.add(sc, "Iteration Screen");

		// Add card layout to frame and update
		frame.add(cardLayout);
		frame.revalidate();
	}

	///// K MEANS /////
	
	/**
	 * Initialize K Means by grabbing number of centroids from Screen card 
	 */
	public void InitializeKMeans()
	{
		km = new KMeans(data, sc.getNumberCentroids());
	}
	
	/**
	 * Reset K Means variable (set to null) and turn running off
	 */
	public void resetKMeans() 
	{
		km = null;
		setKeepRunning(false);
	}

	/**
	 * Clears data Matrix and sets K Means to null
	 */
	public void clearData()
	{
		data = new Matrix(0,0);
		resetKMeans();
	}
	
	/**
	 * Iterate KMeans variable, initialize if null. Checks if converged after iteration.
	 */
	public void Iterate()
	{
		// If not initialized
		if (km == null)
			InitializeKMeans();
		
		// Iterate, then check convergence
		else {
			km.Iterate();
			if (km.isConverged())
				setMessage("Converged!");
		}
		sc.repaint();
	}
	
	/**
	 * Runs many K Means iterations to find most probable centroids
	 */
	public void runAll()
	{
		// If not initialized
		if (km == null)
			InitializeKMeans();
		km.runAll();
		sc.repaint();
	}
	
	///// TIMER //////

	/**
	 * Turns keep running boolean on or off
	 * @param boolean
	 */
	public void setKeepRunning(boolean b) 
	{
		keepRunning = b;
	}	

	/**
	 * Starts timed iterations
	 */
	public void startTimedIteration()
	{
		keepRunning = true;
		timer.schedule(new Iterate(), timerIterate);
	}


	/**
	 * Iterates the K Means algorithm, then checks convergence to continue iterating or reset iteration
	 */
	class Iterate extends TimerTask {
		public void run() {
			
			// Iterate, then check convergence, reseting if converged
			Iterate();
			if (keepRunning)
				if (!km.isConverged())
					timer.schedule(new Iterate(), timerIterate);
				else
					timer.schedule(new ResetTimed(), timerIterate * 2);
			
			// Repaint screen
			sc.repaint();
		}
	}

	/**
	 * Timer to reset current K Means algorithm. This allows timed iterations to continue instead of stopping
	 */
	class ResetTimed extends TimerTask {
		public void run() {
			// Only run if currently running
			if (keepRunning)
			{
				InitializeKMeans();
				timer.schedule(new Iterate(), timerIterate);
				sc.repaint();
			}
		}
	}
	
	/**
	 * Timer to clear message so messages to user expire after a designated time
	 */
	class ClearMessage extends TimerTask {
		public void run() {
			message = "";
			frame.repaint();
		}
	}
	
	///// PLOTTING /////
	
	/**
	 * Sets data that acts as base transform, grabbing minimum and maximum of plot indexes
	 * @param data - Matrix of data to be base transform
	 */
	public void setTransformationData(Matrix data)
	{	
		// Transformation variables
		double xmin, ymin;
		double xmax, ymax;
		double xvalue, yvalue;
		int[] plotIndexes = getPlotIndexes();

		// Sets min and max
		xmin = data.getValue(0, plotIndexes[0]);
		xmax = data.getValue(0, plotIndexes[0]);
		ymin = data.getValue(0, plotIndexes[1]);
		ymax = data.getValue(0, plotIndexes[1]);

		// Iterates though list, finding maximum and minimum
		for (int i=1; i<data.numRows(); i++)
		{
			xvalue = data.getValue(i, plotIndexes[0]);
			yvalue = data.getValue(i, plotIndexes[1]);
			if (xvalue < xmin)
				xmin = xvalue;
			if (xvalue > xmax)
				xmax = xvalue;
			if (yvalue < ymin)
				ymin = yvalue;
			if (yvalue > ymax)
				ymax = yvalue;
		}
		
		// Sets data
		transformData = new double[4];
		transformData[0] = xmin;
		transformData[1] = ymin;
		transformData[2] = xmax;
		transformData[3] = ymax;
	}
	
	/**
	 * Transforms data so it scales to fit screen nicely
	 * @param data (Matrix of data to transform)
	 * @return Matrix - Matrix of transformed data
	 */
	public Matrix transformData(Matrix data)
	{
		// Initialize some variables
		Matrix m = new Matrix(data.numRows(), 2);
		int[] plotIndexes = getPlotIndexes();
		double frameH = frame.getHeight() * 0.75;
		double frameW = frame.getWidth() * 0.75;
		double buffer = 50;
		double xNew, yNew;
		
		// Sets transform data
		double xmin = transformData[0];
		double ymin = transformData[1];
		double xmax = transformData[2];
		double ymax = transformData[3];

		// Iterates though points, setting new position relative to screen
		for (int i=0; i<data.numRows(); i++)
		{
			xNew = (data.getValue(i, plotIndexes[0]) - xmin) / (xmax - xmin) * frameW + buffer;
			yNew = (data.getValue(i, plotIndexes[1]) - ymin) / (ymax - ymin) * frameH + buffer;
			m.setValue(i, 0, xNew);
			m.setValue(i, 1, yNew);
		}
		return m;
	}

	///// SETTERS /////

	/**
	 * Allows data card to set data and reset KMeans variable
	 * @param m (Matrix of data)
	 */
	public void setData(Matrix m)
	{
		data = m;
		setTransformationData(data);
		km = null;
	}

	/**
	 * Allows data card to set plot indexes for ease of access.
	 * @param indexes (int[])
	 */
	public void setPlotIndexes(int[] indexes)
	{
		plotIndexes = indexes;
		if (data != null)
			setTransformationData(data);
	}

	/**
	 * Sets String message and timer so message expires after set time.
	 * @param s (String)
	 */
	public void setMessage(String s)
	{
		message = s;
		timer.schedule(new ClearMessage(), timerMessage);
	}

	///// GETTERS /////

	/**
	 * Return frame for title and instruction pages
	 * @return Frame - container for GUI
	 */
	public Frame getFrame()
	{
		return frame;
	}

	/**
	 * Returns K Means variable for data viewing and null checking
	 * @return KMeans 
	 */
	public KMeans getKM()
	{
		return km;
	}

	/**
	 * Returns for K Means algorithm, return empty matrix if no data
	 * @return Matrix - data matrix
	 */
	public Matrix getData()
	{
		if (data == null)
			return new Matrix(0, 0);
		return data;
	} 

	/** 
	 * Returns plot indexes to view
	 * @return int[] - Integers holding plot indexes
	 */
	public int[] getPlotIndexes()
	{
		return plotIndexes;
	}

	/**
	 * Gets message to display to user
	 * @return String - message to user
	 */
	public String getMessage()
	{
		return message;
	}
}