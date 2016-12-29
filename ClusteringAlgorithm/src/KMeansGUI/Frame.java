package KMeansGUI;
import javax.swing.JFrame;
import java.awt.Dimension;
import java.awt.Toolkit;
import static KMeans.Constants.*;

/**
 * Frame is a JFrame that houses the K Means GUI. It contains a JTabbed Pane to switch between
 * different cards for data viewing/iterating.
 */
@SuppressWarnings("serial")
public class Frame extends JFrame {

	/** 
	 * Creates frame object, setting initial conditions
	 */
	public Frame()
	{
		// Initializes components of frame
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle(frameTitle);
		setVisible(true);

		// ToolMethods to make GUI half of computer screen size
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension dim = kit.getScreenSize();
		setSize(dim.width / 2, dim.height / 2);
		setLocation(dim.width / 4, dim.height / 4);
		setSize(frameSize, frameSize);
	}	
}