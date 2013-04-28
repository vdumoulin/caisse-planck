

public class Produit {
	
	private String barcode;
	private String name;
	private double price;
	private String category;
	private int quantity;
	private double subtotal;
	
	public Produit(String barcode, String name, double price, String category) {
		this.barcode=barcode;
		this.name=name;
		this.price=price;
		this.category=category;
		quantity=0;
		subtotal=0.;
	}
	
	public String toString() {
		return name;
	}
	
	public String getBarcode() {
		return barcode;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public double getPrice() {
		return price;
	}
	
	public String getCategory() {
		return category;
	}
	
	public String getInfo() {
		return barcode+" "+name+" "+price;
	}
	
	public void addOne() {
		quantity++;
		subtotal=quantity*price;
	}
	
	public void subOne() {
		quantity--;
		subtotal=quantity*price;
	}
	
	public void resetQuant() {
		quantity=0;
		subtotal=0.;
	}
	
	public void setQuant(int quantity) {
		this.quantity = quantity;
		subtotal=quantity*price;
	}
	
	public double getSubtotal() {
		return subtotal;
	}
	
	public int getQuant() {
		return quantity;
	}
	
	public Produit copySelf() {
		return new Produit(barcode,name,price,category);
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

}
