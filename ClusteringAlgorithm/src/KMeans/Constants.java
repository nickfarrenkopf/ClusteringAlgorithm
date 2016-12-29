package KMeans;
import java.awt.Color;
import java.awt.Font;

/**
 * Contains constants for KMeans Algorithm and Controller/GUI.
 */
public class Constants {
	
	// Frame variables
	public static final String frameTitle = "K Means Clustering Algorithm";
	public static final int frameSize = 600;
	
	
	// Panel variables
	public static final int vertSpace = 10;
	public static final Font largeFont = new Font(Font.SANS_SERIF, Font.BOLD, 25);
	public static final Font mediumFont = new Font(Font.SANS_SERIF, Font.PLAIN, 18);
	public static final Font smallFont = new Font(Font.SANS_SERIF, Font.PLAIN, 15);
	
	
	// K Means variables
	public static final int maxNumberCentroids = 6;
	public static final int maxNumberIterations = 100;
	public static final Color[] colorScheme = {Color.RED, Color.BLUE, Color.MAGENTA, 
			Color.BLACK, Color.ORANGE, Color.GREEN};
	public static final double epsilon = Math.pow(10, -6);
	
	
	// Plotting variables
	public static final int maxPointSize = 10;
	public static final int messageX = 15;
	public static final int messageY = 50;
	
	
	// Data variables
	public static final String[] exampleData = {"exampleData1.txt", "exampleData2.txt", "exampleData3.txt",
												"exampleData4.txt", "exampleData5.txt", "Manual"};
	public static final int initialDataSelection = 3;
	public static final int initialNumCentroids = 4;
	
	
	// Timer variables
	public static final int timerMessage = 4000;
	public static final int timerIterate = 1000;
}