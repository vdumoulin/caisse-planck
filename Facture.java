
import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.util.ArrayList;
import javax.swing.*;

public class Facture extends JPanel {
	private static final long serialVersionUID = 1L;
	private ArrayList<ItemButton> itemButtonsAL;
	private double total;
	private DecimalFormat df = new DecimalFormat("#0.00");
	private ButtonPressedListener listener = new ButtonPressedListener();
	private ActionListener caisseListener;
	
	public Facture(LayoutManager manager, ActionListener caisseListener) {
		super(manager);
		itemButtonsAL = new ArrayList<ItemButton>();
		total=0.;
		this.caisseListener = caisseListener;
	}
	
	class ButtonPressedListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			ItemButton item = (ItemButton)e.getSource();
			item.subOne();
			if(item.quantityIsZero())
				removeItem(item);
			else
				update();
		}
	}
	
	public String toString() {
		StringBuffer laNote = new StringBuffer();
		for(ItemButton item : itemButtonsAL) {
			laNote.append(
                item.getName() + " x " + item.getQuantity() + " = " +
                df.format(item.getSubtotal()) + "\n"
            );
		}
		laNote.append("Total = "+df.format(total));
		return laNote.toString();
	}
	
	public String addItem(ItemButton newItem) {
		boolean itemFound = false;
		for(ItemButton item : itemButtonsAL) {
			if(item.equals(newItem)) {
				item.addOne();
				itemFound = true;
				update();
				break;
			}
		}
		if(!itemFound) {
			newItem.resetQuantity();
			newItem.addOne();
			newItem.addActionListener(listener);
			itemButtonsAL.add(newItem);
			add(newItem);
			update();
		}
		return df.format(total);
	}
	
	public ArrayList<String> getItemsLog() {
		ArrayList<String> itemsLog = new ArrayList<String>();
		for(ItemButton item : itemButtonsAL) {
			for(int i=0; i<item.getQuantity(); i++)
				itemsLog.add(
                    item.getCategory() + "|" + item.getBarcode() + "|" +
                    item.getName() + "|" + df.format(item.getPrice())
                );
		}
		return itemsLog;
	}
	
	private void removeItem(ItemButton item) {
		remove(item);
		itemButtonsAL.remove(item);
		update();
	}
	
	public void removeAllItems() {
		removeAll();
		itemButtonsAL.clear();
		update();
	}
	
	public double getTotal() {
		return total;
	}
	
	public boolean isEmpty() {
		return itemButtonsAL.size() == 0;
	}
	
	public boolean matchesBarcode(String barcode) {
		for(ItemButton item : itemButtonsAL) {
			if(barcode.compareToIgnoreCase(item.getBarcode()) == 0)
				return true;
		}
		return false;
	}
	
	private void update() {
		recalculateTotal();
		revalidate();
		repaint();
		caisseListener.actionPerformed(new ActionEvent(this,1,""));
	}
	
	private void recalculateTotal() {
		total = 0.;
		for(ItemButton item : itemButtonsAL) {
			total += item.getSubtotal();
		}
	}
}
