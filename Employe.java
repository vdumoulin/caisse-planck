public class Employe {
	private String name;
	private String barcode;
	private String codePermanent;
	private int statut;
	
	public static final int ANONYME = 0;
	public static final int BENEVOLE = 1;
	public static final int GERANT = 2;
	
	public Employe(String name, String barcode, String codePermanent, int statut) {
		this.name=name;
		this.barcode=barcode;
		this.statut=statut;
		this.codePermanent = codePermanent;
	}
	
	public String getName() {
		return name;
	}
	
	public String getBarcode() {
		return barcode;
	}
	
	public String getCodePermanent() {
		return codePermanent;
	}
	
	public int getStatut() {
		return statut;
	}
}
