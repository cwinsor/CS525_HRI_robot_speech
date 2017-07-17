package cwinsor.com.loadImageApp;


import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class LoadImageApp 
{	

	Image image = null;
	JLabel label = null;
	JFrame frame = null;

	public void create() {


		frame = new JFrame();
		frame.setSize(600, 600);
		frame.setVisible(true);

	}

	/**
	 * display the image
	 */
	public void displayImage(String imgName) {

		// display it
		System.out.println(this.getClass().getName() + "display ---> " + imgName);
		try {
			image = ImageIO.read(new File("C:/Users/cwinsor/Documents/projects_wpi/CS525_HRI/project_files/" + imgName));
		} catch (IOException e) {
			e.printStackTrace();
		}
		label = new JLabel(new ImageIcon(image));
		frame.add(label);
		frame.setVisible(true);
	}

	/**
	 * clear the image
	 */
	public void clearImage() {
		if (label != null)
			frame.remove(label);
	}
}






