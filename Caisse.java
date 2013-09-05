import java.util.ArrayList;
import java.util.Scanner;
import java.util.Calendar;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.EOFException;
import java.io.IOException;
import java.io.FileNotFoundException;

import javax.swing.SwingConstants;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.KeyListener;
import java.awt.event.ActionListener;

import drawerControllib.DrawerControl;

public class Caisse extends JFrame {
    // Necessaire pour eviter un warning fatiguant
	private static final long serialVersionUID = 1L;

    // Flag pour desactiver la fonctionnalite de tiroir caise
    // Utile pour le developpement
	private static final boolean tiroirCaisse = false;
	
	// Constantes utiles a l'obtention et au formatage de la date et de l'heure
	public static final String TIME_ONLY = "HH:mm:ss";
	public static final String DATE_ONLY = "yyyy-MM-dd";
	
	// Chemins des fichiers lus par le programme
	public static final String MENU_PATH = "menu.txt";
	public static final String EMPLOYES_PATH = "employes.txt";
	public static final String TOTAL_CAISSE_PATH = "totalcaisse.dat";
	
    // WRITEME
	public static final int REGULAR_ITEM = 0;
	public static final int MONTANT_ARBITRAIRE = 1;
	public static final int AJUSTEMENT = 2;
	public static final int DECOMPTE = 3;

	// ************************************************************************
    // DECLARATION DES ELEMENTS DE L'INTERFACE UTILISATEUR.
	// (L'indentation indique la hierarchie des differents elements.)
	// ************************************************************************
	// Accueille toute l'interface 
	private JPanel wholeInterface;
    // L'interface qu'un benevole/gerant voit
	private JPanel GUInterface;
        // Accueille le champ code barre et le panneau des boutons des items
        // scannes
		private JPanel barcodeFacturePanel;
            // Champ code barre
			private JTextField barcodeField;
            // Accueille les boutons des items scannes
			private Facture facturePanel;
        // Accueille les panneaux de benevole, de gestion et de
        // prix/confirmation
		private JPanel controlPanel;
            // Accueille les panneaux pour le nom du benevole et les boutons de
            // gestion
			private JPanel gestionPanel;
                // Accueille le nom du benevole actif et le texte au-dessus
				private JPanel employePanel;
                    // Texte annoncant le benevole actif
					private JLabel benevoleLabel;
                    // Etiquette de nom du benevole actif
					private JLabel employeLabel;
                // Accueille les boutons de gestion
				private JPanel gestionButtonsPanel;
                    // Bouton pour changer de benevole
					private JButton changeEmployeButton;
                    // Bouton pour compter la caisse
					private JButton countButton;
                    // Bouton pour effectuer un retrait
					private JButton ajustementButton;
                    // Bouton de montant arbitraire
					private JButton montantArbButton;
                    // Bouton pour modifier le menu
					private JButton editMenuButton;
                    // Bouton pour modifier la liste des benevoles
					private JButton editEmployesButton;
                    // Bouton pour voir les releves de transaction
					private JButton viewLogsButton;
            // Accueille les informations sur le total et les boutons pour
            // accepter/annuler
			private JPanel buttonTextPanel;					
                // Etiquette du total de la facture
				private JLabel totalLabel;
                // Etiquette du montant a remettre
				private JLabel aRemettreLabel;
                // Accueille les boutons de confirmation/annulation
				private JPanel buttonPanel;					
                    // Bouton pour annuler la facture
					private JButton cancelButton;
                    // Bouton pour confirmer la facture
					private JButton proceedButton;

    // Interface de login
	private JPanel loginPanel;
        // Indique le nom de la caisse;
		private JLabel nomCaisseLabel;
		private JPanel promptSuperPanel;
		private JPanel promptPanel;
            // Texte demandant de faire le login;
			private JLabel loginLabel;
            // Paneau recevant le champ de texte et le bouton OK
			private JPanel OKTextFieldPanel;
                // Champ de texte d'entree de login
				private JTextField loginTextField;
                // Bouton OK
				private JButton OKButton;

	// ************************************************************************
	// DECLARATION DES VARIABLES RELIEES AU FONCTIONNEMENT DE LA CAISSE 
	// ************************************************************************
    // Nom du benevole actif
	private String employeActuel;							
    // Montant virtuel total contenu dans la caisse	
	private double totalCaisse;								
    // Garde en memoire l'employe actif
	private Employes employes;								
    // Recoit le menu d'items
	private ArrayList <ItemButton> items = new ArrayList<ItemButton>();

	// ************************************************************************
	// OBJETS UTILES AU FONCTIONNEMENT DE LA CAISSE
	// ************************************************************************
    // Permet la conversion d'un double en un String formatte pour la monnaie
	private DecimalFormat df = new DecimalFormat("#0.00");
    // Recoit les "notifications" des differents boutons et champs de texte
	private ClickEnterListener listener = new ClickEnterListener();
    // Fenetre permettant de compter la caisse
	private CountDialog countDialog = new CountDialog();
    // Permet l'ajout de texte a un fichier existant
	private FileWriter writer;
	
	// ************************************************************************
	// CONSTRUCTEUR DE LA CLASSE CAISSE
	// ************************************************************************
	public Caisse() {
		// INITIALISATION DES INSTANCES DE FACTURE ET EMPLOYE
        // Recuperation du menu
		fetchMenu();
        // A sa creation, un objet Employes recupere la liste des benevoles
		employes = new Employes();							

		
        // LE PROGRAMME TENTE DE RECUPERER LE MONTANT VIRTUEL TOTAL ENREGISTRE
        // DANS UN FICHIER BINAIRE LORS DE SA DERNIER EXECUTION. S'IL EN EST
        // INCAPABLE, LE MONTANT EST MIS A 0.00$ ET EST ENREGISTRE DANS UN
        // NOUVEAU FICHIER BINAIRE.
		try {
			DataInputStream input = new DataInputStream(
                new FileInputStream(TOTAL_CAISSE_PATH)
            );
			totalCaisse = input.readDouble();
		}
		// Gestion d'un fichier corrompu
		catch(EOFException eofe) {
			JOptionPane.showMessageDialog(
                null,
                "Le fichier "+TOTAL_CAISSE_PATH+" semble corrompu.\n" +
				"Le total a été remis à zéro et le ficher a été remplacé."
            );
			totalCaisse=0.;
			try {
				DataOutputStream output = new DataOutputStream(
                    new FileOutputStream(TOTAL_CAISSE_PATH)
                );
				output.writeDouble(totalCaisse);
			}
			catch(IOException ex) {
				JOptionPane.showMessageDialog(
                    null,
                    "Une erreur inconnue s'est produite lors de l'ouverture" +
                    "\nde "+TOTAL_CAISSE_PATH+". Le programme doit cesser " +
                    "son exécution."
                );
				System.exit(0);
			}
		}
		// Gestion d'un fichier introuvable
		catch(FileNotFoundException fne) {
			JOptionPane.showMessageDialog(
                null,
                "Le fichier "+TOTAL_CAISSE_PATH+" est introuvable. " +
                "Cette situation\nest normale lors de la premiere " +
                "utilisation. Le total\na été remis é zéro et enregistré " +
                "dans un nouveau fichier."
            );
			totalCaisse=0.;
			try {
				DataOutputStream output = new DataOutputStream(
                    new FileOutputStream(TOTAL_CAISSE_PATH)
                );
				output.writeDouble(totalCaisse);
			}
			catch(IOException ex) {
				JOptionPane.showMessageDialog(
                    null,
                    "Une erreur inconnue s'est produite lors de " +
                    "l'ouverture\nde "+TOTAL_CAISSE_PATH+". Le programme " +
                    "doit cesser son exécution."
                );
				System.exit(0);
			}
		}
		// Gestion de toute autre erreur
		catch(IOException ex) {
			JOptionPane.showMessageDialog(
                null,
                "Une erreur inconnue s'est produite lors de l'ouverture\n" +
                "de "+TOTAL_CAISSE_PATH+". Le programme doit cesser son " +
                "exécution."
            );
			System.exit(0);
		}
		
		// INITIALISATION DES ELEMENTS DE L'INTERFACE UTILISATEUR
		// Champ code barre
		barcodeField = new JTextField();
		barcodeField.setFont(new Font("Times New Roman", Font.PLAIN, 57));
		barcodeField.addActionListener(listener);
		barcodeField.addKeyListener(new KeyPressedListener());
		
		// Panneau recevant les boutons d'items scannes et gerant la facture
		facturePanel = new Facture(new GridLayout(6,0,5,5),listener);
		
		// Panneau recevant le panneau de boutons d'items scannes et le champ
        // de code barre
		barcodeFacturePanel = new JPanel(new BorderLayout(5,5));
		barcodeFacturePanel.add(barcodeField,BorderLayout.NORTH);
		barcodeFacturePanel.add(facturePanel,BorderLayout.CENTER);
		
		// Bouton Accepter
		proceedButton = new JButton("Accepter (F12)");
		proceedButton.setFont(new Font("Times New Roman", Font.BOLD, 25));
		proceedButton.setForeground(new Color(0,150,0));
		proceedButton.setBackground(new Color(255,255,255));
		proceedButton.setToolTipText("Raccourci: F12");
		proceedButton.addActionListener(listener);
		
		// Bouton Annuler
		cancelButton = new JButton("Annuler (ESC)");
		cancelButton.setFont(new Font("Times New Roman", Font.BOLD, 25));
		cancelButton.setForeground(new Color(200,0,0));
		cancelButton.setBackground(new Color(255,255,255));
		cancelButton.addActionListener(listener);
		cancelButton.setToolTipText("Raccourci: ESC");
		
		// Recoit les boutons Accepter et Annuler
		buttonPanel = new JPanel(new GridLayout(1,0,5,5));
		buttonPanel.add(cancelButton);
		buttonPanel.add(proceedButton);
		
		// Etiquette du total de la transaction
		totalLabel = new JLabel("Total: 0.00$",SwingConstants.RIGHT);
		totalLabel.setFont(new Font("Times New Roman", Font.BOLD, 60));
		
		// Etiquette du montant a remettre
		aRemettreLabel = new JLabel("À remettre: 0.00$",SwingConstants.RIGHT);
		aRemettreLabel.setFont(new Font("Times New Roman", Font.BOLD, 40));
		
		// Recoit le panneau de boutons Accepter/Annuler et les etiquettes
        // Total et A remettre
		buttonTextPanel = new JPanel(new GridLayout(0,1,5,5));
		buttonTextPanel.add(totalLabel);
		buttonTextPanel.add(aRemettreLabel);
		buttonTextPanel.add(buttonPanel);
		
		// Etiquette du nom du benevole actif
		employeLabel = new JLabel(employeActuel);
		employeLabel.setFont(new Font("Times New Roman", Font.BOLD, 40));
		
		// Indication du benevole actif
		benevoleLabel = new JLabel("Bénévole actuel:");
		benevoleLabel.setFont(new Font("Times New Roman", Font.BOLD, 20));
		
		// Recoit les etiquettes relatives au benevole actif
		employePanel= new JPanel(new BorderLayout(0,0));
		employePanel.add(benevoleLabel,BorderLayout.NORTH);
		employePanel.add(employeLabel,BorderLayout.CENTER);
		
		// Bouton de changement de benevole
		changeEmployeButton = new GestionButton(
            "Changer de bénévole (F1)", listener
        );
		// Bouton d'edition du fichier menu.txt (visible seulement aux gerants)
		editMenuButton = new GestionButton("Modifier le menu (F3)", listener);
		// Bouton d'edition du fichier de benevoles (visible seulement aux
        // gerants)
		editEmployesButton = new GestionButton(
            "Modifier les bénévoles (F4)", listener
        );
		// Bouton du decompte de la caisse
		countButton = new GestionButton("Compter la caisse (F5)", listener);
		// Bouton d'ajustement
		ajustementButton = new GestionButton("Ajustement (F6)", listener);
		// Bouton de montant arbitraire
		montantArbButton = new GestionButton(
            "Montant arbitraire (F7)", listener
        );
		// Bouton pour voir les transactions (visible seulement aux gerants)
		viewLogsButton = new GestionButton(
            "Voir les transactions (F9)", listener
        );
		
		// Recoit les boutons de gestion (initialise lors du premier login)
		gestionButtonsPanel = new JPanel(new GridLayout(0,1,5,5));
		
		// Recoit les deux panneaux precedents
		gestionPanel = new JPanel(new BorderLayout(5,5));
		gestionPanel.add(employePanel,BorderLayout.NORTH);
		gestionPanel.add(gestionButtonsPanel,BorderLayout.CENTER);
		
		// Recoit la partie droite de l'interface
		controlPanel = new JPanel(new BorderLayout(5,5));
		controlPanel.add(buttonTextPanel,BorderLayout.SOUTH);
		controlPanel.add(gestionPanel,BorderLayout.CENTER);
		
		// Supporte le GUI
		GUInterface = new JPanel(new BorderLayout(5,5));
		GUInterface.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		GUInterface.add(barcodeFacturePanel,BorderLayout.CENTER);
		GUInterface.add(controlPanel,BorderLayout.EAST);
		
		// MISE EN PLACE DU PANNEAU DE LOGIN
		nomCaisseLabel = new JLabel("Café la Planck",SwingConstants.CENTER);
		nomCaisseLabel.setFont(new Font("Times New Roman", Font.BOLD, 130));
		nomCaisseLabel.setBorder(BorderFactory.createEmptyBorder(40, 0, 0, 0));
		nomCaisseLabel.setVerticalAlignment(SwingConstants.BOTTOM);
		
		loginLabel = new JLabel("Veuillez scanner votre carte étudiante ou " +
                                "entrer votre code permanent:",
                                SwingConstants.CENTER);
		loginLabel.setFont(new Font("Times New Roman", Font.BOLD, 30));
		
		loginTextField = new JTextField();
		loginTextField.addActionListener(listener);
		loginTextField.setFont(new Font("Times New Roman", Font.BOLD, 40));
		
		OKButton = new JButton("OK");
		OKButton.setFont(new Font("Times New Roman", Font.BOLD, 40));
		OKButton.setBackground(Color.WHITE);
		OKButton.addActionListener(listener);
		
		OKTextFieldPanel = new JPanel(new BorderLayout(5,5));
		OKTextFieldPanel.add(loginTextField,BorderLayout.CENTER);
		OKTextFieldPanel.add(OKButton,BorderLayout.EAST);
		
		promptSuperPanel = new JPanel(new BorderLayout(5,5));
		promptSuperPanel.setBorder(
            BorderFactory.createEmptyBorder(130, 0, 0, 0)
        );
		promptPanel = new JPanel(new GridLayout(2,0,5,5));
		promptPanel.add(loginLabel);
		promptPanel.add(OKTextFieldPanel);
		promptSuperPanel.add(promptPanel,BorderLayout.NORTH);
		
		loginPanel = new JPanel(new BorderLayout(5,5));
		loginPanel.add(nomCaisseLabel,BorderLayout.NORTH);
		loginPanel.add(promptSuperPanel,BorderLayout.CENTER);
		loginPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		
		// Avant toute chose, le benevole doit se logger. Le programme est
        // inactif tant et aussi longtemps que personne ne s'est logge
		wholeInterface = new JPanel(new BorderLayout());
		wholeInterface.add(loginPanel,BorderLayout.CENTER);
		add(wholeInterface);
	}
	
	class WindowClosing implements WindowListener {
		public void windowOpened(WindowEvent e) {}
		public void windowClosing(WindowEvent e) {
			if(tiroirCaisse) {
				try {
					DrawerControl.closeCommunication();
				} 
                catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		public void windowDeactivated(WindowEvent e) {}
		public void windowClosed(WindowEvent e) {}
		public void windowDeiconified(WindowEvent e) {}
		public void windowActivated(WindowEvent e) {}
		public void windowIconified(WindowEvent e) {}
	}
	
	// Implementation de ActionListener qui determine l'objet dont origine
    // l'evenement et execute l'action attendue de l'objet en question.
	class ClickEnterListener implements ActionListener {
		// Lorsqu'un evenement se produit, ClickEnterListener cherche
        // sequentiellement d'ou l'evenement provient parmi les boutons
        // permanents (i.e. tous les boutons sauf les boutons d'items scannes).
        // Si aucun d'eux n'a genere l'evenement, il s'agit d'un evenement lie
        // a un bouton d'item scanne; il suffit alors de recuperer la
        // "actionCommand" liee a cet evenement pour obtenir une String
        // correspondant au code barre de l'item dont le bouton a ete clique.
		public void actionPerformed(ActionEvent e) {
			// Si l'evenement provient du champ code barre, le texte entre est
            // recupere et separe en tokens delimites par des espaces. Le
            // programme compare alors le premier token aux mots "ajuster"
            // (pour ajuster une transaction erronnee) et "sesame" (pour ouvrir
            // manuellement la caisse). S'il n'y a pas correspondance, le
            // programme tente d'ajouter a la facture un item dont le code
            // barre est identique au premier token. S'il n'y a toujours aucune
            // correspondance, le programme ne fait rien. Puisqu'un evenement
            // genere par le champ code barre constitue une interaction de la
            // part de l'utilisateur, l'etiquette A remettre est reinitialisee
            // dans tous les cas.
			if(e.getSource() == loginTextField) {
				loginAttempt();
			}
			else if(e.getSource() == OKButton) {
				loginAttempt();
			}
			else if(e.getSource() == barcodeField) {
				scanAttempt();
			}
			else if (e.getSource() == cancelButton) {
				cancelTransaction();
			}
			else if(e.getSource() == changeEmployeButton) {
				changeEmploye();
			}
			else if(e.getSource() == countButton) {
				countCaisse();
			}
			else if(e.getSource() == montantArbButton) {
				montantArbitraire();
			}
			else if(e.getSource() == ajustementButton) {
				ajuster();
			}
			else if(e.getSource() == editMenuButton) {
				editMenu();
			}
			else if(e.getSource() == editEmployesButton) {
				editEmployes();
			}
			else if(e.getSource() == viewLogsButton) {
				viewLogs();
			}
			else if (e.getSource() == proceedButton) {
				finishTransaction();
			}
			// Si l'evenement provient d'un bouton d'item scanne, cela signifie
			// que le total a change et donc qu'il faut le mettre a jour sur le
            // GUI.
			else if(e.getSource() == facturePanel){
				updateTotal();
			}
		}
	}
	
	// Ici, des touches de raccourci appelant les memes methodes 
	// que les boutons Accepter/Annuler et les boutons de gestion 
	// sont mises en place.
    //	
	// La gestion des permissions est implantee directement dans les
	// methodes que la pression de chaque touche appelle.
	class KeyPressedListener implements KeyListener {
		public void keyPressed(KeyEvent ke) {
			if(wholeInterface.getComponents()[0] == GUInterface) {
				if(ke.getKeyCode() == KeyEvent.VK_ESCAPE) {
					cancelTransaction();
				}
				else if(ke.getKeyCode() == KeyEvent.VK_F12) {
					finishTransaction();
				}
				else if(ke.getKeyCode() == KeyEvent.VK_F1) {
					changeEmploye();
				}
				else if(ke.getKeyCode() == KeyEvent.VK_F3) {
					editMenu();
				}
				else if(ke.getKeyCode() == KeyEvent.VK_F4) {
					editEmployes();
				}
				else if(ke.getKeyCode() == KeyEvent.VK_F5) {
					countCaisse();
				}
				else if(ke.getKeyCode() == KeyEvent.VK_F6) {
					ajuster();
				}
				else if(ke.getKeyCode() == KeyEvent.VK_F7) {
					montantArbitraire();
				}
				else if(ke.getKeyCode() == KeyEvent.VK_F9) {
					viewLogs();
				}
			}
		}
		public void keyReleased(KeyEvent ke) {}
		public void keyTyped(KeyEvent ke) {}
	}
	
	public static void main(String[] args) {
		if(tiroirCaisse) {
			System.load("/usr/local/lib/libdrawerControl.so");
			try {
				DrawerControl.openCommunication();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		Caisse frame = new Caisse();
		frame.setTitle("Café la Planck 1.1");
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setVisible(true);
	}
	
	private void loginAttempt() {
		String barcode = loginTextField.getText();
		loginTextField.setText(null);
		employeActuel = employes.setEmploye(barcode);
		if(employeActuel != null) {
			if(employes.getEmployeStatus() == Employe.ANONYME) {
				String name = JOptionPane.showInputDialog(
                    "Veuillez entrer votre nom:"
                );
				employeActuel = name;
			}
			if(gestionButtonsPanel != null) {
				gestionButtonsPanel.removeAll();
				gestionButtonsPanel.add(changeEmployeButton);
				if(employes.getEmployeStatus() == 2) {
					gestionButtonsPanel.add(editMenuButton);
					gestionButtonsPanel.add(editEmployesButton);
				}
				gestionButtonsPanel.add(countButton);
				if(employes.getEmployeStatus() > Employe.ANONYME)
					gestionButtonsPanel.add(ajustementButton);
				gestionButtonsPanel.add(montantArbButton);
				if(employes.getEmployeStatus() == Employe.GERANT) 
					gestionButtonsPanel.add(viewLogsButton);
				if(employes.getEmployeStatus() == Employe.GERANT)
					ajustementButton.setText("Ajustement/retrait (F6)");
				else
					ajustementButton.setText("Ajustement (F6)");
				gestionButtonsPanel.revalidate();
				gestionButtonsPanel.repaint();
			}
			employeLabel.setText(employeActuel);
			wholeInterface.remove(loginPanel);
			wholeInterface.add(GUInterface);
			wholeInterface.revalidate();
			wholeInterface.repaint();
			barcodeField.requestFocus();
		}
		else {
			JOptionPane.showMessageDialog(
                null,
                "Acces refuse: le login que vous avez entre est invalide"
            );
		}
	}
	
	private void cancelTransaction() {
		facturePanel.removeAllItems();
		aRemettreLabel.setForeground(Color.BLACK);
		totalLabel.setForeground(Color.BLACK);
		barcodeField.requestFocus();
	}
	
	private void finishTransaction() {
		if(!facturePanel.isEmpty()) {
			String aRemettreString = JOptionPane.showInputDialog(
                "(OPTIONNEL: appuyer sur ENTER pour passer)\n" +
                "Combien vous a-t-on remis?"
            );
			if(tiroirCaisse) {
				try {
					DrawerControl.openDrawer();
				}
                catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			if(aRemettreString != null) {
				try{
					double montantRemis = Double.parseDouble(aRemettreString);
					double aRemettre = montantRemis - facturePanel.getTotal();
					aRemettreLabel.setText(
                        "À remettre: " + df.format(aRemettre) + "$"
                    );
					aRemettreLabel.setForeground(new Color(200,0,0));
				}
				catch(NumberFormatException nfe) {}
				
				try {
					writer = new FileWriter(
                        "transactions/" + nowDate() + ".txt",
                        true
                    );
					writer.append(transactionHeader());
					writer.append(facturePanel+"\n");
					writer.close();
				}
				catch(IOException ex) {
					ex.printStackTrace();
				}
				
				try {
					writer = new FileWriter("logs/"+nowDate()+".txt",true);
					ArrayList<String> itemsLog = facturePanel.getItemsLog();
					for(String item : itemsLog) {
						String[] fields = item.split("\\|");
						if(fields[0].compareToIgnoreCase("Montant arbitraire") == 0)
							writer.append(
                                logHeader() + MONTANT_ARBITRAIRE + "|" + item +
                                "\n"
                            );
						else
							writer.append(
                                logHeader() + REGULAR_ITEM + "|" + item + "\n"
                            );
					}
					writer.close();
				}
				catch(IOException ex) {
					ex.printStackTrace();
				}
				
				totalLabel.setForeground(Color.BLACK);
				
				totalCaisse+=facturePanel.getTotal();
				try {
					DataOutputStream input = new DataOutputStream(
                        new FileOutputStream("totalcaisse.dat")
                    );
					input.writeDouble(totalCaisse);
				}
				catch(IOException ex) {
					ex.printStackTrace();
				}
				
				facturePanel.removeAllItems();
				barcodeField.requestFocus();
			}
			
		}
	}
	
	private void changeEmploye() {
		int choice = JOptionPane.showConfirmDialog(
            null,
            "Souhaitez-vous vraiment vous délogger?",
            "Changement de bénévole",
            JOptionPane.YES_NO_OPTION
        );
		if(choice == JOptionPane.YES_OPTION) {
			wholeInterface.remove(GUInterface);
			wholeInterface.add(loginPanel);
			wholeInterface.revalidate();
			wholeInterface.repaint();
			loginTextField.requestFocus();
		}
	}
	
	private void countCaisse() {
		if(tiroirCaisse) {
			try {
				DrawerControl.openDrawer();
			}
            catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		countDialog.setVisible(true);
		double sum = countDialog.getSum();
		if(sum > 0) {
			if(employes.getEmployeStatus() == 2) {
				if(sum == totalCaisse) {
					JOptionPane.showMessageDialog(
                        null,
                        "La caisse est bien balancée."
                    );
					try {
						writer = new FileWriter(
                            "transactions/" + nowDate() + ".txt",
                            true
                        );
						writer.append(ajustmentHeader());
						writer.append("DÉCOMPTE CAISSE:\n");
						writer.append(
                            "Montant physique de la caisse: " +
                            df.format(sum) + "$\n"
                        );
						writer.append(
                            "Montant virtuel de la caisse: " +
                            df.format(totalCaisse) + "$\n"
                        );
						writer.append(
                            "Différence: " + df.format(sum-totalCaisse) + "$\n"
                        );
						writer.close();
					}
					catch(IOException ex) {
						ex.printStackTrace();
					}
					
					try {
						writer = new FileWriter("logs/"+nowDate()+".txt",true);
						writer.append(
                            logHeader() + DECOMPTE + "|Décompte|0|Décompte|" +
                            df.format(sum-totalCaisse) + "\n"
                        );
						writer.close();
					}
					catch(IOException ex) {
						ex.printStackTrace();
					}
				}
				else {
					JOptionPane.showMessageDialog(
                        null,
                        "Il y a " + df.format(sum) + "$ dans la caisse et " +
                        df.format(totalCaisse) + "$ selon le programme.\n " +
                        "Réajustement automatique de la valeur du programme " +
                        "en conséquence."
                    );
					
					try {
						writer = new FileWriter(
                            "transactions/" + nowDate() + ".txt",
                            true
                        );
						writer.append(ajustmentHeader());
						writer.append("DÉCOMPTE CAISSE AVEC AJUSTEMENT:\n");
						writer.append(
                            "Montant physique de la caisse: " +
                            df.format(sum) + "$\n"
                        );
						writer.append(
                            "Montant virtuel de la caisse: " +
                            df.format(totalCaisse) + "$\n"
                        );
						writer.append(
                            "Différence: " + df.format(sum-totalCaisse) + "$\n"
                        );
						writer.close();
					}
					catch(IOException ex) {
						ex.printStackTrace();
					}
					
					try {
						writer = new FileWriter("logs/"+nowDate()+".txt",true);
						writer.append(
                            logHeader() + DECOMPTE + "|Décompte|0|Décompte|" +
                            df.format(sum-totalCaisse) + "\n"
                        );
						writer.close();
					}
					catch(IOException ex) {
						ex.printStackTrace();
					}
					
					totalCaisse = sum;
					
					try {
						DataOutputStream input = new DataOutputStream(
                            new FileOutputStream("totalcaisse.dat")
                        );
						input.writeDouble(totalCaisse);
					}
					catch(IOException ex) {
						ex.printStackTrace();
					}
				}
			}
			else {
				if(sum == totalCaisse) {
					JOptionPane.showMessageDialog(
                        null,
                        "La caisse est bien balancée."
                    );
					try {
						writer = new FileWriter(
                            "transactions/" + nowDate() + ".txt",
                            true
                        );
						writer.append(ajustmentHeader());
						writer.append("DÉCOMPTE CAISSE:\n");
						writer.append(
                            "Montant physique de la caisse: " +
                            df.format(sum) + "$\n"
                        );
						writer.append(
                            "Montant virtuel de la caisse: " +
                            df.format(totalCaisse) + "$\n"
                        );
						writer.append(
                            "Différence: " + df.format(sum-totalCaisse) +
                            "$\n"
                        );
						writer.close();
					}
					catch(IOException ex) {
						ex.printStackTrace();
					}
					
					try {
						writer = new FileWriter("logs/"+nowDate()+".txt",true);
						writer.append(
                            logHeader() + DECOMPTE + "|Décompte|0|Décompte|" +
                            df.format(sum-totalCaisse) + "\n"
                        );
						writer.close();
					}
					catch(IOException ex) {
						ex.printStackTrace();
					}
				}
				else {
					JOptionPane.showMessageDialog(
                        null,
                        "Il y a "+df.format(sum)+"$ dans la caisse et " +
                        df.format(totalCaisse) + "$ selon le programme."
                    );
				}
				
				try {
					writer = new FileWriter(
                        "transactions/" + nowDate() + ".txt",
                        true
                    );
					writer.append(ajustmentHeader());
					writer.append("DÉCOMPTE CAISSE:\n");
					writer.append(
                        "Montant physique de la caisse: " + df.format(sum) +
                        "$\n"
                    );
					writer.append(
                        "Montant virtuel de la caisse: " +
                        df.format(totalCaisse) + "$\n"
                    );
					writer.append(
                        "Différence: "+df.format(sum-totalCaisse)+"$\n"
                    );
					writer.close();
				}
				catch(IOException ex) {
					ex.printStackTrace();
				}
				
				try {
					writer = new FileWriter("logs/"+nowDate()+".txt",true);
					writer.append(
                        logHeader() + DECOMPTE + "|Décompte|0|Décompte|" +
                        df.format(sum-totalCaisse) + "\n"
                    );
					writer.close();
				}
				catch(IOException ex) {
					ex.printStackTrace();
				}
			}
			
		}
		barcodeField.requestFocus();
	}
	
	private void montantArbitraire() {
		
		String nom = JOptionPane.showInputDialog("Veuillez entrer un nom");
		if(nom != null) {
			if(!facturePanel.matchesBarcode(nom)) {
				try {
					String montant = JOptionPane.showInputDialog(
                        "Veuillez entrer un montant"
                    );
					double prix = Double.parseDouble(montant);
					facturePanel.addItem(
                        new ItemButton(nom,nom,prix,"Montant arbitraire",null)
                    );
				}
				catch(NumberFormatException nfe) {}
			}
			else
				facturePanel.addItem(
                    new ItemButton(nom,nom,0.00,"Montant arbitraire",null)
                );
		}
		
		barcodeField.requestFocus();
	}
	
	// Affiche un dialogue demandant le montant de l'ajustement et appelle 
    // ajuster(String montant). La methode n'a d'effet que si l'employe n'est
    // pas anonyme.
	private void ajuster() {
		if(employes.getEmployeStatus() > Employe.ANONYME) {
			String montant = JOptionPane.showInputDialog(
                "Veuillez entrer le montant de l'ajustement"
            );
			if(montant != null) {
				ajuster(montant);
			}
		}
		barcodeField.requestFocus();
	}
	
	private void ajuster(String montant) {
		if(employes.getEmployeStatus() > Employe.ANONYME) {
			try {
				double ajustement = Double.parseDouble(montant);
				totalCaisse+=ajustement;
				String raison = JOptionPane.showInputDialog(
                    "Veuillez entrer la raison de l'ajustement:"
                );
				try {
					writer = new FileWriter(
                        "transactions/" + nowDate() + ".txt",
                        true
                    );
					writer.append(ajustmentHeader());
					writer.append(
                        "Ajustement de " + df.format(ajustement) + "$\n"
                    );
					writer.append(
                        "Nouveau montant caisse: " + df.format(totalCaisse) +
                        "$\n"
                    );
					writer.append("Raison: "+raison+"\n");
					writer.close();
				}
				catch(IOException ex) {
					ex.printStackTrace();
				}
				try {
					writer = new FileWriter("logs/"+nowDate()+".txt",true);
					writer.append(
                        logHeader() + AJUSTEMENT + "|Ajustement|0|" + raison +
                        "|" + df.format(ajustement)+"\n"
                    );
					writer.close();
				}
				catch(IOException ex) {
					ex.printStackTrace();
				}
				try {
					DataOutputStream input = new DataOutputStream(
                        new FileOutputStream("totalcaisse.dat")
                    );
					input.writeDouble(totalCaisse);
				}
				catch(IOException ex) {
					ex.printStackTrace();
				}	
			}
			catch(NumberFormatException nfe2) {
				JOptionPane.showMessageDialog(
                    null,
                    "Erreur: veuillez entrer un nombre de type \"float\" " +
                    "en argument"
                );
			}
		}
		barcodeField.requestFocus();
	}
	
	private void viewLogs() {
		if(employes.getEmployeStatus() == Employe.GERANT) {
			Calendar cal = new DatePicker(null).setPickedDate();
			if(cal != null) {
				SimpleDateFormat sdf = new SimpleDateFormat(DATE_ONLY);
			    String fileName = "transactions/" + sdf.format(cal.getTime()) +
                                  ".txt";
			    
			    try {
			    	JTextArea ta = new JTextArea(20, 60);  
			    	ta.read(new FileReader(fileName), null);  
			    	ta.setEditable(false);  
			    	JOptionPane.showMessageDialog(null, new JScrollPane(ta)); 
			    }
			    catch(FileNotFoundException fne) {
			    	JOptionPane.showMessageDialog(
                        null,
                        "Aucun fichier de transaction pour cette date."
                    );
			    }
			    catch(IOException ioe) {
			    	ioe.printStackTrace();
			    }
			}
		}
	    barcodeField.requestFocus();
	}
	
	private void editMenu() {
		if(employes.getEmployeStatus() == Employe.GERANT) {
			FileEditDialog fed = new FileEditDialog(FileEditDialog.MENU);
			fed.setVisible(true);
			barcodeField.requestFocus();
		}
	}
	
	private void editEmployes() {
		if(employes.getEmployeStatus() == Employe.GERANT) {
			FileEditDialog fed = new FileEditDialog(FileEditDialog.EMPLOYES);
			fed.setVisible(true);
			barcodeField.requestFocus();
		}
	}
	
	private void updateTotal() {
		totalLabel.setText("Total: "+df.format(facturePanel.getTotal())+"$");
		totalLabel.setForeground(
            (facturePanel.isEmpty())? Color.BLACK : new Color(0,150,0)
        );
		barcodeField.requestFocus();
	}
	
	private void scanAttempt() {
		String barcodeFieldText = barcodeField.getText();
		String[] command = barcodeFieldText.split(" "); 
		if(command.length > 0) {
            // Cas 1 - On desire ajuster le montant:
            //     * Il doit y avoir un montant d'ajustement en argument
            //     * L'employe doit etre un benevole enregistre
            boolean commandIsAdjust = (
                command[0].compareToIgnoreCase("ajuster") == 0
                && command.length > 1
                && employes.getEmployeStatus() > Employe.ANONYME
            );
			if(commandIsAdjust) {
				ajuster(command[1]);
			}
            // Cas 2 - On desire ouvrir la caisse
            //     Aucun prerequis
			else if(command[0].compareToIgnoreCase("sesame") == 0) {
				if(tiroirCaisse) {
					try {
						DrawerControl.openDrawer();
					} 
                    catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
            // Cas 3 - On desire connaitre le total de la caisse:
            //     * L'employe doit etre un gerant
            boolean commandIsTotal = (
                command[0].compareToIgnoreCase("totalcaisse") == 0
                && employes.getEmployeStatus() == Employe.GERANT
            );
			else if(commandIsTotal) {
				JOptionPane.showMessageDialog(
                    null, 
                    "Total caisse: "+df.format(totalCaisse)+"$"
                );
			}
            // Cas 4 - Tout le reste: on essaie de scanner un item
			else {
				ItemButton itemToAdd = searchFor(command[0]);
				if(itemToAdd != null)
					facturePanel.addItem(itemToAdd);
			}
		}
		aRemettreLabel.setText("À remettre: 0.00$");
		aRemettreLabel.setForeground(Color.BLACK);
		barcodeField.setText(null);
		barcodeField.requestFocus();
	}
	
	private ItemButton searchFor(String barcode) {
		for(ItemButton item : items) {
			if(item.matchesBarcode(barcode) || item.matchesShortcut(barcode))
				return item.clone();
		}
		return null;
	}
	
	private void fetchMenu() {
		try {
			Scanner input = new Scanner(new File("menu.txt"));
			input.useDelimiter("\n");
			boolean success = true;
			String categorie = "Inconnue";
			double prixCat = 0.;
			while(input.hasNext()) {
				String itemString = input.next();
				String [] infos = itemString.split("\\|");
				try {
					if(infos != null && infos[0].length() > 0 && infos[0].charAt(0) == '#' && infos.length > 1) {
						categorie = infos[0].substring(1);
						prixCat = Double.parseDouble(infos[1]);
					}
					else if(infos != null && infos.length == 4) {
						items.add(new ItemButton(
                            infos[0],
                            infos[1],
                            Double.parseDouble(infos[2]),
                            categorie,
                            infos[3]
                        ));
					}
					else if(infos != null && infos.length == 3) {
						items.add(new ItemButton(
                            infos[0],
                            infos[1],
                            Double.parseDouble(infos[2]),
                            categorie,
                            null
                        ));
					}
					else if(infos != null && infos.length == 2) {
						items.add(new ItemButton(
                            infos[0],
                            infos[1],
                            prixCat,
                            categorie,
                            null
                        ));
					}
					else if(infos != null && infos[0].compareToIgnoreCase("") == 0){
						continue;
					}
					else {
						success = false;
					}
				}
				catch(NumberFormatException nfe) {
					success = false;
				}
			}
			if(!success) {
				JOptionPane.showMessageDialog(
                    null,
                    "Le programme n'a pas pu charger tous les items. " +
                    "Assurez-vous\nque la syntaxe " +
                    "CODE BARRE|NOM(Double)|PRIX|CATEGORIE du\n" +
                    "fichier "+Caisse.MENU_PATH+" est respectée."
                );
			}
			input.close();
		}
		catch (FileNotFoundException fne) {
			JOptionPane.showMessageDialog(null, "Erreur: Menu inexistant");
			System.exit(0);
		}
	}
	
	public static String nowTime() {
	    Calendar cal = Calendar.getInstance();
	    SimpleDateFormat sdf = new SimpleDateFormat(TIME_ONLY);
	    return sdf.format(cal.getTime());
	}
	
	public static String nowDate() {
	    Calendar cal = Calendar.getInstance();
	    SimpleDateFormat sdf = new SimpleDateFormat(DATE_ONLY);
	    return sdf.format(cal.getTime());
	}
	
	private String transactionHeader() {
		return "---------- "+nowTime()+" ("+employeActuel+") ----------\n";
	}
	
	private String ajustmentHeader() {
		return "########## "+nowTime()+" ("+employeActuel+") ##########\n";
	}
	
	private String logHeader() {
	    return nowTime()+"|"+employeActuel+"|";
	}
}
