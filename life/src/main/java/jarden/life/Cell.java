package jarden.life;

import jarden.life.aminoacid.AddAminoAcidToProtein;
import jarden.life.aminoacid.AminoAcid;
import jarden.life.aminoacid.CreateUracil;
import jarden.life.aminoacid.GetAminoAcidFromCodon;
import jarden.life.aminoacid.GetCodonFromRNA;
import jarden.life.aminoacid.GetGeneFromDNA;
import jarden.life.aminoacid.GetRNAFromGene;
import jarden.life.nucleicacid.Adenine;
import jarden.life.nucleicacid.Codon;
import jarden.life.nucleicacid.Cytosine;
import jarden.life.nucleicacid.DNA;
import jarden.life.nucleicacid.Guanine;
import jarden.life.nucleicacid.Nucleotide;
import jarden.life.nucleicacid.RNA;
import jarden.life.nucleicacid.Uracil;

import java.util.ArrayList;

public class Cell {
	private ArrayList<Protein> proteinList = new ArrayList<>();
	private ArrayList<AminoAcid> aminoAcidList = new ArrayList<>();
	private ArrayList<Nucleotide> nucleotideList = new ArrayList<>();
	private ArrayList<RNA> rnaList = new ArrayList<>();
	private DNA dna;
	
	public Cell(DNA dna) {
		this.dna = dna;
		// enough amino acids for 1st 3 proteins:
		for (int i = 0; i < 2; i++) {
			aminoAcidList.add(new AddAminoAcidToProtein(this));
			aminoAcidList.add(new GetAminoAcidFromCodon(this));
			aminoAcidList.add(new GetCodonFromRNA(this));
			aminoAcidList.add(new GetGeneFromDNA(this));
			aminoAcidList.add(new GetRNAFromGene(this));
			aminoAcidList.add(new CreateUracil(this));
		}
		// enough nucleotides for RNA for 1st 3 proteins: UUA UUC UUU UAA; UUG UCU UAA; UCC UAA
		for (int i = 0; i < 40; i++) {
			nucleotideList.add(new Uracil());
		}
		for (int i = 0; i < 14; i++) {
			nucleotideList.add(new Adenine());
		}
		for (int i = 0; i < 8; i++) {
			nucleotideList.add(new Cytosine());
		}
		for (int i = 0; i < 4; i++) {
			nucleotideList.add(new Guanine());
		}
	}
    public Object action(Object object) {
        Object currentObject = object;
        // create copy, in case proteins are making new proteins and adding
        // them to proteinList:
        ArrayList<Protein> proteinListCopy = new ArrayList<>(proteinList);
        for (Protein protein: proteinListCopy) {
            currentObject = protein.action(currentObject);
        }
        return currentObject;
    }
	public DNA getDNA() {
		return dna;
	}
	public void addProtein(Protein protein) {
		proteinList.add(protein);
//		Thread thread = new Thread(protein);
//		MasterDesigner.print(thread.getName() +
//				" starting for protein " + protein);
//		thread.start();
	}
	public void addAminoAcid(AminoAcid aminoAcid) {
		synchronized (aminoAcidList) {
			aminoAcidList.add(aminoAcid);
			aminoAcidList.notifyAll();
		}
	}
	public void addNucleotide(Nucleotide nucleotide) {
		synchronized (nucleotideList) {
			nucleotideList.add(nucleotide);
			nucleotideList.notifyAll();
		}
	}
	public void addRNA(RNA rna) {
		synchronized(rnaList) {
			rnaList.add(rna);
			rnaList.notifyAll();
		}
	}
	public Nucleotide waitForNucleotide(String name) {
		synchronized (nucleotideList) {
			Nucleotide nucleotide;
			while (true) {
				nucleotide = getNucleotideByName(name);
				if (nucleotide != null) return nucleotide;
				MasterDesigner.print(Thread.currentThread().getName() +
						" waiting for nucleotide " + name);
				try { nucleotideList.wait(); }
				catch(InterruptedException e) {}
			}
		}
	}
	public RNA waitForRNA() {
		synchronized (rnaList) {
			RNA rna;
			while (true) {
				rna = getRNA();
				if (rna != null) return rna;
				MasterDesigner.print(Thread.currentThread().getName() +
						" waiting for some RNA");
				try { rnaList.wait(); }
				catch(InterruptedException e) {}
			}
		}
	}
	public AminoAcid waitForAminoAcid(Codon codon) {
		synchronized (aminoAcidList) {
			AminoAcid aminoAcid;
			while (true) {
				aminoAcid = getAminoAcidByCodon(codon);
				if (aminoAcid != null) return aminoAcid;
				MasterDesigner.print(Thread.currentThread().getName() +
						" waiting for amino acid for codon " + codon);
				try { aminoAcidList.wait(); }
				catch(InterruptedException e) {}
			}
		}
	}
	private RNA getRNA() {
		int size = rnaList.size();
		if (size == 0) return null;
		RNA rna = rnaList.get(size - 1); // TODO: why getting the last one?
		rnaList.remove(rna);
		return rna;
	}
	public void printNucleotides() {
		System.out.println("Cell's nucleotides:");
		for (Nucleotide nucleotide: nucleotideList) {
			System.out.println("   " + nucleotide);
		}
	}
	public void printRNA() {
		System.out.println("Cell's rna:");
		for (RNA rna: rnaList) {
			System.out.println("   " + rna);
		}
	}
	public void printProteins() {
		System.out.println("Cells proteins:");
		for (Protein protein: proteinList) {
			System.out.println("   " + protein);
		}
	}
	public void printThreads() {
		System.out.println("Cells threads:");
		Thread[] threads = new Thread[Thread.activeCount()];
		Thread.currentThread().getThreadGroup().enumerate(threads);
		for (Thread thread: threads) {
			StackTraceElement[] stackElements = thread.getStackTrace();
			int i;
			for (i = 0; i < stackElements.length; i++) {
				if (stackElements[i].toString().startsWith("jarden.")) {
					System.out.println("   " + thread.getName() + ": " + stackElements[i]);
					break;
				}
			}
			if (i > stackElements.length) {
				System.out.println("   " + thread.getName() + " no stack trace!");
			}
		}
	}
	public void printCell() {
		this.printNucleotides();
		this.printRNA();
		this.printProteins();
		this.printThreads();
	}
	/*
	 * Find aminoAcid with specified name; if found, remove from list.
	 */
	private AminoAcid getAminoAcidByCodon(Codon codon) {
		for (AminoAcid aminoAcid: aminoAcidList) {
			if (aminoAcid.matchCodon(codon)) {
				aminoAcidList.remove(aminoAcid);
				return aminoAcid;
			}
		}
		System.out.println("no amino acid found for " + codon);
		return null;
	}
	/*
	 * Find aminoAcid with specified name; if found, remove from list.
	 */
	private Nucleotide getNucleotideByName(String name) {
		for (int i = nucleotideList.size() - 1; i >= 0; i--) {
			Nucleotide nucleotide = nucleotideList.get(i);
			if (nucleotide.getName().equals(name)) {
				nucleotideList.remove(nucleotide);
				return nucleotide;
			}
		}
		// throw new IllegalStateException("no nucleotide found for " + name);
		MasterDesigner.print("no nucleotide found for " + name);
		return null;
	}
}
