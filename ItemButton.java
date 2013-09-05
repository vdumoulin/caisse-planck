
import java.awt.*;
import java.text.DecimalFormat;
import javax.swing.JButton;


public class ItemButton extends JButton implements Cloneable {
	
	private static final long serialVersionUID = 1L;
	
	private String barcode;
	private String name;
	private String category;
	private String shortcut;
	private double price;
	private int quantity;
	
	private DecimalFormat df = new DecimalFormat("#0.00");
	
	public ItemButton(String barcode,String name, double price,
                      String category, String shortcut) {
		super();
		
		setFont(new Font("Times New Roman", Font.PLAIN, 60));
		setForeground(new Color(100,100,100));
		setBackground(new Color(255,255,255));
		
		this.barcode = barcode;
		this.name = name;
		this.price = price;
		this.category = category;
		this.shortcut = shortcut;
		quantity = 0;
		
		setText(name+" x "+quantity);
	}
	
	public String toString() {
		return category + "|" + name + " x " + quantity + " = " +
            df.format(price*quantity);
	}
	
	public String getBarcode() {
		return barcode;
	}
	
	public String getName() {
		return name;
	}
	
	public double getPrice() {
		return price;
	}
	
	public String getCategory() {
		return category;
	}
	
	public int getQuantity() {
		return quantity;
	}
	
	public double getSubtotal() {
		return quantity*price;
	}
	
	public boolean matchesBarcode(String barcode) {
		return this.barcode.compareToIgnoreCase(barcode) == 0;
	}
	
	public boolean matchesShortcut(String shortcut) {
		if(shortcut != null && this.shortcut != null)
			return this.shortcut.compareTo(shortcut) == 0;
		else
			return false;
	}
	
	public boolean quantityIsZero() {
		return quantity == 0;
	}
	
	public void addOne() {
		quantity++;
		setText(name+" x "+quantity);
	}
	
	public void subOne() throws ArithmeticException {
		quantity--;
		if(quantity < 0)
			throw new ArithmeticException("Quantity cannot become negative");
		setText(name+" x "+quantity);
	}
	
	public void resetQuantity() {
		quantity = 0;
	}
	
	public ItemButton clone() {
		return new ItemButton(barcode,name,price,category,shortcut);
	}
	
	public boolean equals(ItemButton itemButton) {
		return barcode.compareToIgnoreCase(itemButton.getBarcode()) == 0;
	}
}
