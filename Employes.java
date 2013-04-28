

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JOptionPane;

public class Employes {
	private ArrayList <Employe> listeEmp = new ArrayList<Employe>();
	private Employe employeActuel;
	
	public Employes() {
		employeActuel = null;
		try{
			Scanner input = new Scanner(new File("employes.txt"));
			input.useDelimiter("\n");
			boolean success = true;
			while(input.hasNext()) {
				String employeString = input.next();
				String[] champs = employeString.split("\\|");
				try {
					if(champs != null && champs.length == 4) {
						listeEmp.add(new Employe(champs[0],champs[1],champs[2],Integer.parseInt(champs[3])));
					}
					else if(champs != null && champs[0].compareToIgnoreCase("") != 0){
						success = false;
					}
				}
				catch(NumberFormatException nfe) {
					success = false;
				}
				
			}
			if(!success) {
				JOptionPane.showMessageDialog(null, "Le programme n'a pas pu charger tous les bénévoles. Assurez-vous\n" +
													"que la syntaxe NOM|CODE BARRE|CODE PERMANENT|PERMISSION(0-2)\n" +
													"du fichier "+Caisse.EMPLOYES_PATH+" est respectée.");
			}
			input.close();
		}
		catch(FileNotFoundException fne) {
			JOptionPane.showMessageDialog(null, "Erreur: Fichier des bénévoles non trouvé");
			System.exit(0);
		}
	}
	
	public String setEmploye(String barcode) {
		for(Employe employe : listeEmp) {
			if(employe.getBarcode().compareToIgnoreCase(barcode) == 0 
					|| employe.getCodePermanent().compareToIgnoreCase(barcode) == 0) {
				employeActuel = employe;
				return employe.getName();
			}
		}
		return null;
	}
	
	public int getEmployeStatus() {
		return employeActuel.getStatut();
	}
}
