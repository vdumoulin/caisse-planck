import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GestionButton extends JButton {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public GestionButton (String text, ActionListener listener) {
		super(text);
		setHorizontalAlignment(SwingConstants.RIGHT);
		setFont(new Font("Times New Roman", Font.BOLD, 30));
		setBackground(new Color(255,255,255));
		addActionListener(listener);
	}

}
