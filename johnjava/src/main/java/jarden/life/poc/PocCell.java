package jarden.life.poc;

import java.util.ArrayList;
import java.util.List;

public class PocCell {
	public final static boolean DEBUG = true;
	private List<Protein> proteins;
	private StringBuilder aminoAcids;
	private List<String> rnaList;
	private List<DockingSite> sites;
	private final static PocCell instance = new PocCell();
	
	public static PocCell getInstance() {
		return instance;
	}

	private PocCell() {
		this.proteins = new ArrayList<Protein>();
		this.aminoAcids = new StringBuilder("tqft");
		rnaList = new ArrayList<String>();
		String testRNA = "ACUCAAUAA"; // t q STOP
		rnaList.add(testRNA);
		this.sites = new ArrayList<DockingSite>();
		new Ribosome().activate();
		new TransferRNA(new char[] {'A', 'C', 'U'}, 't').activate();
		new TransferRNA(new char[] {'C', 'A', 'A'}, 'q').activate();
	}

	public static void main(String[] args) {
	}

	// TODO: perhaps put this in new class RNA
	public String findMessengerRNA() {
		synchronized(rnaList) {
			while (rnaList.size() == 0) {
				try {
					if (DEBUG) {
						System.out.println("findMessengerRNA() about to wait()");
					}
					rnaList.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			return rnaList.remove(rnaList.size() - 1);
		}
	}

	public void addDockingSite(DockingSite site) {
		this.sites.add(site);
	}

	public char getAminoAcid(char aminoAcid) {
		synchronized(aminoAcids) {
			int index;
			while ((index = aminoAcids.indexOf(Character.toString(aminoAcid))) < 0) {
				try {
					aminoAcids.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			char aa = aminoAcids.charAt(index);
			aminoAcids.deleteCharAt(index);
			return aa;
		}
	}

	public DockingSite findSite(String name, String codonStr) {
		synchronized(sites) {
			boolean found = false;
			DockingSite matchingSite = null; 
			while (!found) {
				for (DockingSite site: sites) {
					if (site.match(name, codonStr)) {
						matchingSite = site;
						found = true;
						break;
					}
				}
			}
			return matchingSite;
		}
	}

}

class DockingSite {
	private String name;
	private String subType;
	private Object value;
	
	public DockingSite(String name) {
		this.name = name;
	}
	public boolean match(String name, String subtype) {
		return name.equals(this.name) && subtype.endsWith(this.subType);
	}
	public void setSubType(String subType) {
		this.subType = subType;
	}

	public synchronized Object getValue() {
		while (this.value == null) {
			try {
				if (PocCell.DEBUG) {
					System.out.println("DockingSite.getValue() about to wait()");
				}
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		Object obj = this.value;
		this.value = null;
		return obj;
	}
	public synchronized void setValue(Object character) {
		this.value = character;
		notifyAll();
	}
}

class Protein implements Runnable {
	private StringBuilder aminoAcids = new StringBuilder();
	
	public void activate() {
		new Thread(this).start();
	}
	public void appendAminoAcid(char aa) {
		System.out.println("Protein(appendAminoAcid(" + aa + ")");
		aminoAcids.append(aa);
	}

	@Override
	public void run() {
		System.out.println("hi, I am a protein! My amino acids are:");
		System.out.println(aminoAcids.toString());
	}
	
}

// TODO: sadly, tRNA isn't a protein! Please fix this!
// Should change this to the enzyme (protein) that builds the tRNA
class TransferRNA extends Protein {
	private char[] codon;
	private char aminoAcid;

	public TransferRNA(char[] codon, char aminoAcid) {
		this.codon = codon;
		this.aminoAcid = aminoAcid;
	}

	@Override
	public void run() {
		PocCell temp = PocCell.getInstance();
		char aa = temp.getAminoAcid(aminoAcid);
		String codonStr = new String(codon);
		DockingSite site = temp.findSite("addaa", codonStr);
		site.setValue(Character.valueOf(aa));
	}
	
}

class Ribosome extends Protein {

	@Override
	public void run() {
		PocCell temp = PocCell.getInstance();
		String mRNA = temp.findMessengerRNA();
		Protein protein = new Protein();
		DockingSite site = new DockingSite("addaa");
		temp.addDockingSite(site);
		for (int i = 0; (i + 2) < mRNA.length(); i += 3) {
			String codon = mRNA.substring(i, i+3);
			if (codon.equals("UAA")) break; // i.e. test for STOP
			site.setSubType(codon);
			Object obj = site.getValue();
			if (obj instanceof Character) {
				char aa = (Character)obj;
				protein.appendAminoAcid(aa);
			} else {
				throw new IllegalStateException(
						"Ribosome.run(): unrecognised object attached to docking site");
			}
		}
		protein.activate();
		// TODO: at this point, should really get the next mRNA
	}
	
}
