package gui;

import java.awt.Dimension;
import javax.swing.JFrame;
import main.DesktopPane;
import main.Box;

public class TimeBoxPanel extends JFrame {

	// Creates the panel that holds the time box
	private static final long serialVersionUID = 1L;
	JFrame boxPanel;
	TimeBoxPanel me;
	public Box box = new Box();
	public Dimension boxSize = new Dimension(500, 850);
	public String animationTitle;
	public int exportCounter = 1;

	public TimeBoxPanel() {
		me = this;
		setResizable(false);
		setTitle("3D Space-Time Analysis");
		pack();
	}

	public void setupBox(DesktopPane father) {
		box = null;
		box = new Box();
		box.setParent(father);
		animationTitle = father.animationTitle;
		setLocation((int) (father.getBounds().getMinX() + father.animationSize.getWidth()),
				(int) (father.getBounds().getMinY()));
		// Location at end of the animation
		this.setContentPane(box);
		this.getContentPane().setPreferredSize(boxSize);
		this.pack();
		box.init();
	}

}