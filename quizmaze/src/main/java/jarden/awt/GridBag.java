package jarden.awt;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

/**
 * Combines GridBagLayout and GridBagConstraints
 * @author John
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
	/**
	 * @deprecated  Replaced by {@link #add(Component, int, int)}
	 */
	public void addItem(Component component, int x, int y) {
		add(component, x, y);
	}
	public void add(Component component, int x, int y) {
		gridx = x;
		gridy = y;
		gbLayout.setConstraints(component, this);
		container.add(component);
	}
	/**
	 * @deprecated  Replaced by {@link #add(Component, int, int, int, int)}
	 */
	public void addItem(Component component, int gx, int gy, int gw, int gh) {
		add(component, gx, gy, gw, gh);
	}
	public void add(Component component, int gx, int gy, int gw, int gh) {
		gridx = gx;
		gridy = gy;
		gridwidth = gw;
		gridheight = gh;
		gbLayout.setConstraints(component, this);
		container.add(component);
	}
}
