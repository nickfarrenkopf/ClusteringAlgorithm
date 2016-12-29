package KMeansGUI;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import KMeans.Controller;
import static KMeans.Constants.*;

/**
 * TitlePage is a JPanel that initialize a title page for K Means program. Title page contains
 * title info, author info, a start buttons and instructions button.
 */
@SuppressWarnings("serial")
public class TitlePage extends JPanel implements ActionListener {
	
	// Controller variable
	private Controller controller;
	
	// All buttons
	private JButton startButton;
	private JButton iButton;

	/**
	 * Initializes title screen with title, author, start button, and instructions button.
	 * @param controller
	 */
	public TitlePage(Controller controller)
	{
		// Sets controller
		this.controller = controller;

		// Sets title and author label
		JLabel titleLabel = new JLabel("K Means Clustering Algorithm");
		JLabel nameLabel = new JLabel("by Nick Farrenkopf");
		
		// Sets font for labels
		titleLabel.setFont(largeFont);
		nameLabel.setFont(mediumFont);

		// Initialize buttons
		startButton = new JButton("Start");
		iButton = new JButton("Instructions");
		
		// Adds action listeners to buttons
		startButton.addActionListener(this);
		iButton.addActionListener(this);

		// Sets panel layout
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		// Center all components on screen
		titleLabel.setAlignmentX(CENTER_ALIGNMENT);
		nameLabel.setAlignmentX(CENTER_ALIGNMENT);
		startButton.setAlignmentX(CENTER_ALIGNMENT);
		iButton.setAlignmentX(CENTER_ALIGNMENT);

		// Adds to panel, adding separation for esthetic appeal
		add(Box.createVerticalGlue());
		add(titleLabel);
		add(nameLabel);
		add(Box.createRigidArea(new Dimension(0, vertSpace * 2)));
		add(startButton);
		add(Box.createRigidArea(new Dimension(0, vertSpace)));
		add(iButton);
		add(Box.createVerticalGlue());
	}

	/**
	 * Code for when buttons are clicked. Start button initializes program and instructions button
	 * brings up instructions page.
	 */
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		// Remove title page from screen
		controller.getFrame().remove(this);
		
		// If start button clicked, remove title screen and initialize program screen
		if (e.getSource() == startButton)
			controller.InitializeScreen();
		
		// If instructions button clicked, remove title screen and initialize instructions panel
		if (e.getSource() == iButton)
			controller.InstructionsPage();
	}
}