

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class CountDialog extends JDialog {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JNumberTextField nb20 = new JNumberTextField(3,JNumberTextField.NUMERIC);
	private JNumberTextField nb10 = new JNumberTextField(3,JNumberTextField.NUMERIC);
	private JNumberTextField nb5 = new JNumberTextField(3,JNumberTextField.NUMERIC);
	private JNumberTextField nb2 = new JNumberTextField(3,JNumberTextField.NUMERIC);
	private JNumberTextField nb1 = new JNumberTextField(3,JNumberTextField.NUMERIC);
	private JNumberTextField nb25c = new JNumberTextField(3,JNumberTextField.NUMERIC);
	private JNumberTextField nb10c = new JNumberTextField(3,JNumberTextField.NUMERIC);
	private JNumberTextField nb5c = new JNumberTextField(3,JNumberTextField.NUMERIC);
	private JNumberTextField nb1c = new JNumberTextField(3,JNumberTextField.NUMERIC);
	
	private JButton jbtOK = new JButton("OK");
	private JButton jbtCancel = new JButton("Cancel");
	
	private double sum = 0.;
	
	public CountDialog() {
		this(null,true);
	}
	
	public CountDialog(java.awt.Frame parent, boolean modal) {
		super(parent,modal);
		setTitle("");
		
		JPanel jpLabels = new JPanel(new GridLayout(0,1,5,5));
		jpLabels.add(new JLabel("20.0$"));
		jpLabels.add(new JLabel("10.0$"));
		jpLabels.add(new JLabel("5.00$"));
		jpLabels.add(new JLabel("2.00$"));
		jpLabels.add(new JLabel("1.00$"));
		jpLabels.add(new JLabel("0.25$"));
		jpLabels.add(new JLabel("0.10$"));
		jpLabels.add(new JLabel("0.05$"));
		jpLabels.add(new JLabel("0.01$"));
		
		JPanel jpTextFields = new JPanel(new GridLayout(0,1,5,5));
		jpTextFields.add(nb20);
		jpTextFields.add(nb10);
		jpTextFields.add(nb5);
		jpTextFields.add(nb2);
		jpTextFields.add(nb1);
		jpTextFields.add(nb25c);
		jpTextFields.add(nb10c);
		jpTextFields.add(nb5c);
		jpTextFields.add(nb1c);
		
		JPanel jpMony = new JPanel(new BorderLayout(5,5));
		jpMony.add(jpLabels,BorderLayout.WEST);
		jpMony.add(jpTextFields,BorderLayout.CENTER);
		
		JPanel jpButtons = new JPanel(new GridLayout(1,0,5,5));
		jpButtons.add(jbtCancel);
		jpButtons.add(jbtOK);
		
		JPanel jpCount = new JPanel(new BorderLayout(5,5));
		jpCount.add(jpMony,BorderLayout.CENTER);
		jpCount.add(jpButtons,BorderLayout.SOUTH);
		jpCount.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		add(jpCount);
		pack();
		setLocationRelativeTo(null);
		
		jbtOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sum = 0.;
				
				sum += 20*Integer.parseInt("0"+nb20.getText());
				sum += 10*Integer.parseInt("0"+nb10.getText());
				sum += 5*Integer.parseInt("0"+nb5.getText());
				sum += 2*Integer.parseInt("0"+nb2.getText());
				sum += 1*Integer.parseInt("0"+nb1.getText());
				sum += 0.25*Integer.parseInt("0"+nb25c.getText());
				sum += 0.10*Integer.parseInt("0"+nb10c.getText());
				sum += 0.05*Integer.parseInt("0"+nb5c.getText());
				sum += 0.01*Integer.parseInt("0"+nb1c.getText());
				
				setVisible(false);
			}
		});
		
		jbtCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sum = 0.;
				setVisible(false);
			}
		});
	}
	
	public double getSum() {
		return sum;
	}

}
