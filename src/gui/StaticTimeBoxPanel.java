package gui;

import java.awt.Dimension;
import java.awt.Window;
import java.lang.reflect.Method;

import javax.swing.JFrame;
import javax.swing.UIManager;

import main.DesktopPane;
import main.StaticBox;

public class StaticTimeBoxPanel extends JFrame {

	// Creates the panel that holds static
	private static final long serialVersionUID = 1L;
	JFrame boxPanel;
	StaticTimeBoxPanel me;
	public StaticBox box = new StaticBox();
	public Dimension boxSize = new Dimension(500, 850);

	public StaticTimeBoxPanel() {
		me = this;
		setResizable(true);
		setTitle("3D Static Space-Time Analysis");
		pack();
	}

	public void setupStaticBox(DesktopPane father) {
		box = null;
		box = new StaticBox();
		box.setParent(father);
		setLocation((int) (father.getBounds().getMinX() + father.animationSize.getWidth()),
				(int) (father.getBounds().getMinY() + 50));
		this.setContentPane(box);
		this.getContentPane().setPreferredSize(boxSize);
		this.pack();
		box.init();
	}

	public void clear() {
		box = null;
		this.setContentPane(null);
	}

}