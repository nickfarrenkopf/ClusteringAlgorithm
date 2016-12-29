package KMeansGUI;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import KMeans.Controller;
import static KMeans.Constants.*;

/**
 * InstructionsPage is a JPanel that houses instructions for what the KMeans program does.
 * It has a few what-to-do message and  suggestions for starting conditions.
 */
@SuppressWarnings("serial")
public class InstructionsPage extends JPanel implements ActionListener {

	// Controller variable
	private Controller controller;
	
	// Button that starts program
	private JButton beginButton;

	/**
	 * Houses instructions in messages printed to screen and start button.
	 * @param controller
	 */
	public InstructionsPage(Controller controller)
	{
		// Controller variable
		this.controller = controller;
		
		// String header
		JLabel header = new JLabel("Hello! Welcome to the K Means Algorithm!");
		
		// Message after header and before example fractal
		ArrayList<String> openingMessage = new ArrayList<String>();
		openingMessage.add("K Means is a data clustering algorithm.");
		openingMessage.add("It finds clusters by creating centroids,");
		openingMessage.add("which act as the centers of these clusters.");
		openingMessage.add("It finds final centroids by iterating through the data set,");
		openingMessage.add("assigning each data point to a centroid,");
		openingMessage.add("finding the new centroid's average position,");
		openingMessage.add("then updating a point's centroid to the closest one.");
		openingMessage.add("For an easy to see example, head to the");
		openingMessage.add("'Iteration Screen' and click 'Run Timed'.");
		openingMessage.add(" ");
		openingMessage.add("Note: One K Means iteration does not guarantee finding");
		openingMessage.add("the optimal solutions, so it is often run several times.");
		
		// Initialize button
		beginButton = new JButton("Begin");
		beginButton.addActionListener(this);
		
		// Sets layout of panel
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		// Sets alignment
		header.setAlignmentX(CENTER_ALIGNMENT);
		beginButton.setAlignmentX(CENTER_ALIGNMENT);
		
		// Add header to panel
		header.setFont(largeFont);
		add(header);
		add(Box.createRigidArea(new Dimension(0, vertSpace * 3)));
		
		// Opening messages
		for(int i=0; i<openingMessage.size(); i++)
		{
			JLabel label = new JLabel(openingMessage.get(i));
			label.setAlignmentX(CENTER_ALIGNMENT);
			label.setFont(mediumFont);
			add(label);
		}
		add(Box.createRigidArea(new Dimension(0, vertSpace)));
		
		// Add buttons to panel
		add(beginButton);
	}

	/**
	 * Action listeners for button. If button is clicked, then main program gets initialized.
	 */
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		// Remove this from screen
		controller.getFrame().remove(this);
		controller.InitializeScreen();
	}
}