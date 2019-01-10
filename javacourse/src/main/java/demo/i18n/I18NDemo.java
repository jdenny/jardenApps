package demo.i18n;

import jarden.gui.GridBag;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.text.DateFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Simple Swing application that shows current date and time
 * in various countries. User selects from list of pre-defined
 * countries. Allow user to choose locale from list of countries
 * and languages!
 * @author john.denny@gmail.com
 *
 */
public class I18NDemo implements ActionListener {
	private final static String bundleName = "demo.i18n.messages";
	private final static Locale uk = Locale.UK;
	private final static Locale spain = new Locale("es", "ES");
	private final static String[] DATE_STYLES = { "Short", "Medium", "Long", "Full"};
	private final static int[] DATE_FORMAT_STYLES = { 
		DateFormat.SHORT,
		DateFormat.MEDIUM,
		DateFormat.LONG,
		DateFormat.FULL
	};

	private ResourceBundle bundleEngland;
	private ResourceBundle bundleSpain;
	private Locale selectedLocale;
	
	private JLabel localeLabel;
	private JLabel amountLabel;
	private JLabel dateLabel;
	private JLabel timeLabel;
	private JLabel amountLabel2;
	private JLabel dateLabel2;
	private JLabel timeLabel2;
	private JButton enButton;
	private JButton esButton;
	private JFrame frame;
	private JComboBox<String> dateStyleCombo;
	private JButton goButton;
	private JComboBox<String> localeCombo;
	private Locale[] availableLocales;
	
	public I18NDemo() {
		bundleEngland = ResourceBundle.getBundle(bundleName, uk);
		bundleSpain = ResourceBundle.getBundle(bundleName, spain);
		this.availableLocales = Locale.getAvailableLocales();
		
		// slightly long-winded code to sort the array!
		List<Locale> localeList = new ArrayList<Locale>(
				Arrays.asList(this.availableLocales));
	    Collections.sort(localeList, new Comparator<Locale>() {
	        @Override
	        public int compare(Locale o1, Locale o2) {
	            return o1.getDisplayName().compareTo(o2.getDisplayName());
	        }
	    });
	    this.availableLocales = localeList.toArray(new Locale[0]);
	    // end of sort!
		
		URL imgURL = I18NDemo.class.getResource("spanish.jpg");
	    ImageIcon spanishIcon = new ImageIcon(imgURL);
		imgURL = I18NDemo.class.getResource("english.jpg");
	    ImageIcon englishIcon = new ImageIcon(imgURL);
		
		this.frame = new JFrame();
		this.amountLabel = new JLabel();
		this.dateLabel = new JLabel();
		this.timeLabel = new JLabel();
		this.amountLabel2 = new JLabel();
		this.dateLabel2 = new JLabel();
		this.timeLabel2 = new JLabel();
		this.enButton = new JButton(englishIcon);
		this.enButton.setBorder(BorderFactory.createEmptyBorder());
		this.enButton.setContentAreaFilled(false);
		this.esButton = new JButton(spanishIcon);
		this.esButton.setBorder(BorderFactory.createEmptyBorder());
		this.esButton.setContentAreaFilled(false);
		this.dateStyleCombo = new JComboBox<String>(DATE_STYLES);
		this.localeLabel = new JLabel();
		this.localeCombo = new JComboBox<String>();
		for (Locale locale: this.availableLocales) {
			localeCombo.addItem(locale.getDisplayName());
		}
		this.goButton = new JButton();
		
		setLanguage(spain, bundleSpain);
		
		enButton.addActionListener(this);
		esButton.addActionListener(this);
		dateStyleCombo.addActionListener(this);
		goButton.addActionListener(this);
		// set layout of components:
		Container container = frame.getContentPane();
		JPanel northPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JPanel centrePanel = new JPanel();
		JPanel southPanel = new JPanel();
		container.add(northPanel, BorderLayout.NORTH);
		container.add(centrePanel, BorderLayout.CENTER);
		container.add(southPanel, BorderLayout.SOUTH);
		northPanel.add(enButton);
		northPanel.add(esButton);
		southPanel.add(localeLabel);
		southPanel.add(localeCombo);
		southPanel.add(goButton);
		GridBag gridBag = new GridBag(centrePanel);
		gridBag.add(amountLabel, 0, 0);
		gridBag.add(dateLabel, 0, 1);
		gridBag.add(timeLabel, 0, 2);
		gridBag.add(this.dateStyleCombo, 2, 1);
		gridBag.fill = GridBag.HORIZONTAL;
		gridBag.weightx = 1.0;
		gridBag.add(amountLabel2, 1, 0);
		gridBag.add(dateLabel2, 1, 1);
		gridBag.add(timeLabel2, 1, 2);
		frame.pack();
		Dimension dim = frame.getToolkit().getScreenSize();
		frame.setLocation(dim.width / 3,
			dim.height / 3);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true); // start event handling thread

	}
	private void setLanguage(Locale locale, ResourceBundle bundle) {
		this.selectedLocale = locale;
		this.localeCombo.setSelectedItem(locale.getDisplayName());
		this.localeLabel.setText(bundle.getString("select"));;
		this.amountLabel.setText(bundle.getString("amount"));
		this.dateLabel.setText(bundle.getString("date"));
		this.timeLabel.setText(bundle.getString("time"));
		this.goButton.setText(bundle.getString("go"));
		this.frame.setTitle(bundle.getString("title"));
		displayValues();
	}
	private void displayValues() {
		double amount = 12345.67;
		Format currencyFormat = NumberFormat.getCurrencyInstance(this.selectedLocale);
		this.amountLabel2.setText(currencyFormat.format(amount));
		Date date = new Date();
		int style = DATE_FORMAT_STYLES[this.dateStyleCombo.getSelectedIndex()];
		Format dateFormat = DateFormat.getDateInstance(style, this.selectedLocale);
		this.dateLabel2.setText(dateFormat.format(date));
		Format timeFormat = DateFormat.getTimeInstance(style, this.selectedLocale);
		this.timeLabel2.setText(timeFormat.format(date));
	}

	public static void main(String[] args) {
		new I18NDemo();
	}
	@Override
	public void actionPerformed(ActionEvent event) {
		Object source = event.getSource();
		if (source == this.enButton) {
			setLanguage(uk, this.bundleEngland);
		} else if (source == this.esButton) {
			this.selectedLocale = spain;
			setLanguage(spain, this.bundleSpain);
		} else if (source == this.dateStyleCombo) {
			displayValues();
		} else if (source == this.goButton) {
			int index = this.localeCombo.getSelectedIndex();
			this.selectedLocale = this.availableLocales[index];
			displayValues();
		} else {
			throw new IllegalStateException("unknown button: " + source);
		}
	}
}
