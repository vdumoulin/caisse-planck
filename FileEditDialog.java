import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

import javax.swing.*;

public class FileEditDialog extends JDialog{
	private static final long serialVersionUID = 1L;
	public static final boolean MENU = true;
	public static final boolean EMPLOYES = false;
	
	private JTextArea text = new JTextArea(20,60);
	private String fileName;
	private JButton jbtOK = new JButton("OK");
	private JButton jbtCancel = new JButton("Cancel");
	
	public FileEditDialog(boolean type) {
		this(null,true,type);
	}
	
	public FileEditDialog(java.awt.Frame parent, boolean modal, boolean type) {
		super(parent,modal);
		setTitle("");
		if(type == MENU) fileName = "menu.txt";
		else fileName = "employes.txt";
		
		try {
			text.read(new FileReader(fileName), null);
		} 
		catch(FileNotFoundException fne) {
			JOptionPane.showMessageDialog(
                null,
                "Erreur: le fichier "+fileName+" est introuvable"
            );
		}
		catch(IOException ioe) {
			ioe.printStackTrace();
		}
		
		JPanel jpButtons = new JPanel(new GridLayout(1,0,5,5));
		jpButtons.add(jbtCancel);
		jpButtons.add(jbtOK);
		
		JPanel jpFileEdit = new JPanel(new BorderLayout(5,5));
		jpFileEdit.add(new JScrollPane(text),BorderLayout.CENTER);
		jpFileEdit.add(jpButtons,BorderLayout.SOUTH);
		jpFileEdit.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		add(jpFileEdit);
		pack();
		setLocationRelativeTo(null);
		
		jbtOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String newText = text.getText();
				try {
					PrintWriter output = new PrintWriter(new File(fileName));
					output.print(newText);
					output.close();
				} 
				catch(FileNotFoundException fne) {
					JOptionPane.showMessageDialog(
                        null,
                        "Erreur: fichier non trouvé"
                    );
				}
				setVisible(false);
			}
		});
		
		jbtCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
	}
}
