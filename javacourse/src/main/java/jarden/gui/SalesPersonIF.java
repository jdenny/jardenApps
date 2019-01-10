package jarden.gui;

public interface SalesPersonIF extends SalesUnitIF {
	double addSales(double newSale);
	double getSales();
	String getPhotoFile();
}
