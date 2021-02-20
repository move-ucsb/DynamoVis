package utils;

import main.DesktopPane;
import java.awt.Color;


public class Buffer {
    
	static final double DEFAULT_SIZE = 1.0;
    Color color;
    double size;
    boolean b;
	DesktopPane parent;
	private String[] distances = {"1", "2", "3"};

    public Buffer() {
        size = Double.parseDouble(distances[0]);
        color = Color.WHITE;
        b=false;
    }

    public Buffer(double size) {
        color = Color.WHITE;
        this.size = size;
    }
    public Buffer(boolean b) {
        if (b){
	    	color = Color.YELLOW;
	        size = 1.0;
	        this.b=b;
        }
        else{
        	color = Color.WHITE;
            size = 1.0;
            this.b=b;
        }
    }
    
	public double getBufferDistance(){
		return size;
	}
    
	
	public String[] getBufferDistances(){
		return distances;
	}
}
