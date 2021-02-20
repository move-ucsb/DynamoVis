package gui;

import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.UIManager;

import main.DesktopPane;
import main.SketchData;
import net.miginfocom.swing.MigLayout;

public class MoveParameterPanel extends JPanel  {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static int openFrameCount = 0;
    static final int xOffset = 30, yOffset = 30;
    
	public JFileChooser dataChooser;	
	private DesktopPane parent;
	private SketchData data;
	
	public MoveParameterPanel(DesktopPane father) {
		parent = father;
		data = parent.data;
	   	try {
	   		UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
	   	} catch (Exception e) {
	   		e.printStackTrace();
	   	}
		setLayout(new MigLayout("insets 0", "[20px:20px:20px][15.00][20px:20px:20px,fill][][grow,fill][]", "[][][][grow,fill][][grow,fill][][][]"));
		
		
	}	
}


