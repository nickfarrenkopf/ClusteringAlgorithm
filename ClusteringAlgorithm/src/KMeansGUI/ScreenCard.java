package KMeansGUI;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import KMeans.Controller;
import Math.Matrix;
import static KMeans.Constants.*;

/**
 * ScreenCard is a JPanel that houses the iteration screen for the KMeans algorithm.
 * Once loaded, the user is able to change the number of centroids for the algorithm,
 * iterate, iterate on a timer, or run all the iterations for the KMeans algorithm.
 * ScreenCard implements ActionListener so it can listen to components.
 */
@SuppressWarnings("serial")
public class ScreenCard extends JPanel implements ActionListener{

	// Controller variable
	private Controller controller;

	// JComboBox for choosing number of centroids
	private JComboBox<Integer> numberCentroidsBox;

	// Buttons that control running
	private JButton runButton;
	private JButton timedButton;
	private JButton allButton;
	private JButton resetButton;

	/**
	 * Initializes the screen card with a controller. Initializes a JComponents and adds them to panel.
	 * @param controller
	 */
	public ScreenCard(Controller controller)
	{
		// Sets controller
		this.controller = controller;

		// JComboBox for number of centroids
		JLabel centroidLabel = new JLabel("Number of Centroids:");
		Integer[] numCents = new Integer[maxNumberCentroids];
		for (int i=0; i<numCents.length; i++)
			numCents[i] = (Integer) (i + 1);
		numberCentroidsBox = new JComboBox<Integer>(numCents);
		numberCentroidsBox.setSelectedIndex(initialNumCentroids - 1);

		// Initialize buttons
		runButton = new JButton("Run Once");
		timedButton = new JButton("Run Timed");
		allButton = new JButton("Run All");
		resetButton = new JButton("Reset");

		// Add actions listeners
		numberCentroidsBox.addActionListener(this);
		runButton.addActionListener(this);
		timedButton.addActionListener(this);
		allButton.addActionListener(this);
		resetButton.addActionListener(this);
		
		// Add to panel
		add(centroidLabel);
		add(numberCentroidsBox);
		add(runButton);
		add(timedButton);
		add(allButton);
		add(resetButton);
	}
	
	/**
	 * Action Listener for JPanel. Contains code for what to do when boxes are changed or buttons are clicked.
	 */
	@Override
	public void actionPerformed(ActionEvent e) 
	{	
		// If centroid box changed
		if (e.getSource() == numberCentroidsBox)
			if (controller.getKM() != null)
				controller.setMessage("To change number of centroids, reset KMeans.");

		// If run button clicked
		if (e.getSource() == runButton)
			controller.Iterate();
		
		// If timed button clicked
		if (e.getSource() == timedButton)
			if (timedButton.getText().equals("Run Timed"))
			{
				controller.startTimedIteration();
				timedButton.setText("Stop");
			} else {
				controller.setKeepRunning(false);
				timedButton.setText("Run Timed");
			}
		
		// If run all button clicked
		if (e.getSource() == allButton)
			controller.runAll();
		
		// If reset button clicked
		if (e.getSource() == resetButton)
			controller.resetKMeans();
		
		// If not timed button, turn timer off
		if (e.getSource() != timedButton)
		{
			controller.setKeepRunning(false);
			timedButton.setText("Run Timed");
		}
		
		// Repaint screen
		repaint();
	}
	
	/**
	 * Return the values for number of centroids JComboBox so controller knows how to initialize K Means.
	 * @return int 
	 */
	public int getNumberCentroids()
	{
		return (int) numberCentroidsBox.getSelectedItem();
	}
	
	/**
	 * Paints components to screen. Paints all points to screen, transformaing to fit to screen.
	 * Also draws lines connecting old centroids so user can visualize how centroids change.
	 */
	public void paint(Graphics g)
	{
		// Draw everything else
		super.paint(g);
		
		// Initialize variables
		Graphics2D g2 = (Graphics2D) g;
		int[] ind = controller.getPlotIndexes();	
		Matrix data = controller.transformData(controller.getData());
		int size = (int) (Math.sqrt(getParent().getHeight() * getParent().getWidth()) / 100);
		if (size > maxPointSize)
			size = maxPointSize;
		
		// Prints message to user
		g.setFont(smallFont);
		g.drawString(controller.getMessage(), messageX, messageY);
		
		// Plot black points if k means not initialized
		if (controller.getKM() == null)
		{
			for (int i=0; i<data.numRows(); i++)
				g2.fill(new Ellipse2D.Double(data.getValue(i, 0), data.getValue(i, 1), size, size));
				
		// Plot colored points if k means is initialized
		} else {
			
			// Grab k means variables
			ArrayList<Matrix> allCentroids = controller.getKM().getAllCentroids();
			Matrix c1, c2;
			int[] dataIndexes = controller.getKM().getDataCentroidIndex();
			
			// Draw all data points
			for (int i=0; i<controller.getKM().getCentroids().numRows(); i++)
			{
				// If point has specified centroid, draw circle
				g2.setColor(colorScheme[i]);
				for (int j=0; j<data.numRows(); j++)
					if (dataIndexes[j] == i)
						g2.fill(new Ellipse2D.Double(data.getValue(j, ind[0]), data.getValue(j, ind[1]), size, size));
			}
			
			// Draw all centroids
			for (int i=0; i<allCentroids.size(); i++)
			{
				// Draws all centroids
				c1 = controller.transformData(allCentroids.get(i));
				for (int j=0; j<c1.numRows(); j++)
				{
					g2.setColor(colorScheme[j]);
					g2.draw(new Ellipse2D.Double(c1.getValue(j, ind[0]), c1.getValue(j, ind[1]), 2 * size, 2 * size));
				}
			}
			
			// Draw lines connecting centroids
			double x1, y1, x2, y2;
			for (int i=1; i<allCentroids.size(); i++)
			{
				c1 = controller.transformData(allCentroids.get(i));
				c2 = controller.transformData(allCentroids.get(i - 1));
				for (int j=0; j<c1.numRows(); j++)
				{
					g2.setColor(colorScheme[j]);
					x1 = c1.getValue(j, ind[0]) + size;
					y1 = c1.getValue(j, ind[1]) + size;
					x2 = c2.getValue(j, ind[0]) + size;
					y2 = c2.getValue(j, ind[1]) + size;
					g2.draw(new Line2D.Double(x1, y1, x2, y2));
				}
			}
		}
	}
}