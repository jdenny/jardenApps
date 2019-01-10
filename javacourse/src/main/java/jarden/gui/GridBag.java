package jarden.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

/**
 * Helper class for defining layout of AWT and Swing components;
 * combines GridBagLayout and GridBagConstraints.
 * @author john.denny@gmail.com
 *
 */
public class GridBag extends GridBagConstraints {
	private static final long serialVersionUID = 1L;
	private Container container;
	private GridBagLayout gbLayout;

	public GridBag(Container container) {
		this.container = container;
		anchor = NORTHWEST;
		gridwidth = 1;
		gridheight = 1;
		insets = new Insets(1, 2, 2, 2);
		gbLayout = new GridBagLayout();
		container.setLayout(gbLayout);
	}
	public void add(Component component, int x, int y) {
		gridx = x;
		gridy = y;
		gbLayout.setConstraints(component, this);
		container.add(component);
	}
	public void add(Component component, int gx, int gy, int gw, int gh) {
		gridx = gx;
		gridy = gy;
		gridwidth = gw;
		gridheight = gh;
		gbLayout.setConstraints(component, this);
		container.add(component);
	}
	public void remove(Component component) {
		container.remove(component);
	}
}
