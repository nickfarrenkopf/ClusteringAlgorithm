package KMeansGUI;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import FileThings.TextFile;
import KMeans.Controller;
import Math.Matrix;
import static KMeans.Constants.*;

/**
 * DataCard is a JPanel that houses the data loading screen for the KMeans algorithm.
 * User is able to select between a variety of example data and change which columns of data are wanted to view.
 * Implements ActionListener to listen to JComponents and MouseListener to listen
 * to mouse clicking for manual points.
 */
@SuppressWarnings("serial")
public class DataCard extends JPanel implements ActionListener, MouseListener {

	// Controller variable
	private Controller controller;
	
	// Allows user to select data from file
	private JComboBox<String> fileSelectionBox;
	
	// Save button to save manual data
	private JButton saveButton;

	// JComboBox so user can choose indexes to plot
	private JComboBox<Integer> plotIndex1;
	private JComboBox<Integer> plotIndex2;
	
	// Array list to hold manual point data
	private ArrayList<Point2D.Double> manualPoints;

	/**
	 * Initializes DataCard with controller. Initializes all comonents and adds to panel.
	 * @param controller
	 */
	public DataCard(Controller controller)
	{
		// Sets controller
		this.controller = controller;

		// JComboBox file choices for selecting data files
		JLabel dataLabel = new JLabel("Data selection:");
		fileSelectionBox = new JComboBox<>(exampleData);
		fileSelectionBox.setSelectedIndex(initialDataSelection);
		
		// Save button to save manual data
		saveButton = new JButton("Save Data");
		saveButton.setEnabled(false);
		
		// Plot indexes for selecting data indexes (initialized in loadData())
		JLabel indexLabel = new JLabel("Plot Indexes:");
		Integer[] indexes = new Integer[2];
		for (int i=0; i<indexes.length; i++)
			indexes[i] = (Integer) i;
		plotIndex1 = new JComboBox<>(indexes);
		plotIndex2 = new JComboBox<>(indexes);
		
		// Add action listeners
		fileSelectionBox.addActionListener(this);
		saveButton.addActionListener(this);
		plotIndex1.addActionListener(this);
		plotIndex2.addActionListener(this);

		// Initializes data
		manualPoints = new ArrayList<>();
		loadData();

		// Adds to panel
		add(dataLabel);
		add(fileSelectionBox);
		add(saveButton);
		add(indexLabel);
		add(plotIndex1);
		add(plotIndex2);
	}

	/**
	 * Loads new data from combo box, setting data to controller.
	 * Changing the combo boxes is tedious, so there is a bit of code here.
	 */
	public void loadData()
	{
		// Load file data
		Matrix data = TextFile.readFileToMatrix((String) fileSelectionBox.getSelectedItem(), 0, Integer.MAX_VALUE);
		
		// Remove unwanted data and add wanted data
		plotIndex1.setSelectedIndex(0);
		plotIndex2.setSelectedIndex(0);
		int itemCount = plotIndex1.getItemCount();
		for (int i=1; i<itemCount; i++)
		{
			plotIndex1.removeItemAt(1);
			plotIndex2.removeItemAt(1);
		}
		for (int i=1; i<data.numCols(); i++)
		{
			plotIndex1.addItem(i);
			plotIndex2.addItem(i);
		}

		// Set data and initial values
		if (data.numCols() >= 2)
			plotIndex2.setSelectedIndex(1);
		controller.setData(data);
		repaint();
	}
	
	/**
	 * Action listeners for JPanel. Does things when boxes are changed or buttons are clicked.
	 * If user selects manual data, adds mouse listener to panel to listen for clicks.
	 */
	@Override
	public void actionPerformed(ActionEvent e) 
	{	
		// If user wants to save manual data
		if (e.getSource() == saveButton)
		{
			// Sets data
			controller.setData(new Matrix(manualPoints));
			controller.setMessage("Data saved in program.");
		}
		
		// What happens when combo box changed
		if (e.getSource() == fileSelectionBox)
			// If user wants to load example data
			if (!fileSelectionBox.getSelectedItem().equals("Manual"))
			{	
				// Remove mouse listener if they exist
				MouseListener[] mls = getMouseListeners();
				if (mls.length > 0)
					for (MouseListener ml:mls)
						removeMouseListener(ml);
				
				// Disable buttons
				saveButton.setEnabled(false);
				
				// Load data
				loadData();

			// If user wants manual data
			} else {
				// Add mouse listener to allow user to click
				addMouseListener(this);

				// Enable buttons
				saveButton.setEnabled(true);
				
				// Reset data lists
				controller.clearData();
				manualPoints.clear();
			}
		
		// If plot index box changed
		if (e.getSource() == plotIndex1 || e.getSource() == plotIndex2)
		{
			int[] indexes = {(int) plotIndex1.getSelectedItem(), (int) plotIndex2.getSelectedItem()};
			controller.setPlotIndexes(indexes);
		}
		
		// Refresh screen
		repaint();
	}
	
	/**
	 * Mouse listener to add manual points to array list.
	 */
	@Override
	public void mouseClicked(MouseEvent e) 
	{
		manualPoints.add(new Point2D.Double(e.getX(), e.getY()));
		repaint();
	}
	
	/**
	 * Paints all components to screen. Paints points to screen, transforming to fit to screen.
	 */
	public void paint(Graphics g)
	{
		// Paint everything else
		super.paint(g);
		
		// Other variables
		Graphics2D g2 = (Graphics2D) g;
		int size = (int) (Math.sqrt(getParent().getHeight() * getParent().getWidth()) / 100);
		if (size >= maxPointSize)
			size = maxPointSize;

		// Prints message to user
		g.setFont(smallFont);
		g.drawString(controller.getMessage(), messageX, messageY);

		// Draws data if wanted, manual otherwise
		if (!fileSelectionBox.getSelectedItem().equals("Manual"))
		{
			// Draws data if controller has data
			Matrix data = controller.transformData(controller.getData());
			for (int i=0; i<data.numRows(); i++)
				g2.fill(new Ellipse2D.Double(data.getValue(i, 0), data.getValue(i, 1), size, size));
		} else {
			// Plots manual points
			for (Point2D.Double p:manualPoints)
				g2.fill(new Ellipse2D.Double(p.getX(), p.getY(), size, size));
		}
	}

	// Unused methods
	@Override
	public void mouseEntered(MouseEvent arg0) {}
	public void mouseExited(MouseEvent arg0) {}
	public void mousePressed(MouseEvent arg0) {}
	public void mouseReleased(MouseEvent arg0) {}
}