package jarden.life;

import jarden.life.aminoacid.AddAminoAcidToProtein;
import jarden.life.aminoacid.AminoAcid;
import jarden.life.aminoacid.DigestCell;
import jarden.life.aminoacid.DivideCell;
import jarden.life.aminoacid.FindNextGene;
import jarden.life.aminoacid.GetAminoAcidFromCodon;
import jarden.life.aminoacid.GetCodonFromRNA;
import jarden.life.aminoacid.GetRNAFromGene;
import jarden.life.nucleicacid.Adenine;
import jarden.life.nucleicacid.Codon;
import jarden.life.nucleicacid.Cytosine;
import jarden.life.nucleicacid.DNA;
import jarden.life.nucleicacid.Guanine;
import jarden.life.nucleicacid.Nucleotide;
import jarden.life.nucleicacid.RNA;
import jarden.life.nucleicacid.Thymine;
import jarden.life.nucleicacid.Uracil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static jarden.life.nucleicacid.NucleicAcid.promoterCode;
import static jarden.life.nucleicacid.NucleicAcid.proteinNameDigest;
import static jarden.life.nucleicacid.NucleicAcid.proteinNameDivide;
import static jarden.life.nucleicacid.NucleicAcid.proteinNamePolymerase;
import static jarden.life.nucleicacid.NucleicAcid.proteinNameRibosome;
import static jarden.life.nucleicacid.NucleicAcid.proteinTypeDigestion;
import static jarden.life.nucleicacid.NucleicAcid.proteinTypeDivision;
import static jarden.life.nucleicacid.NucleicAcid.proteinTypeStem;
import static jarden.life.nucleicacid.NucleicAcid.terminatorCode;

public class Cell {
    private static Cell syntheticCell;

	private DNA dna;
    private List<Protein> proteinList = new LinkedList<>();
	private List<AminoAcid> aminoAcidList = new LinkedList<>();
	private List<Nucleotide> nucleotideList = new LinkedList<>();
	private List<RNA> rnaList = new LinkedList<>();
    private int hashCode = 0;
    private CellReadyToSplitListener splitListener;

    /*
	Current implementation of codonTable.
	See Nucleotide for real-life codonTable.
	    (Promoter               UAUAAU)
		AddAminoAcidToProtein	UUU
		GetAminoAcidFromCodon	UUC
		GetCodonFromRNA			UUA
		FindNextGene 			UUG
		GetRNAFromGene 			UCU
		CreateUracil			UCC
		Stop					UAA, UAG, UGA
		DivideCell              UAC
		DigestCell              UCA

    In this pseudo DNA are 4 genes to make the following proteins:
    polymerase:              (RNA codons)
       FindNextGene          (UUG)
       GetRNAFromGene        (UCU)
       Stop                  (UAA)
    ribosome:
       GetCodonFromRNA       (UUA)
       GetAminoAcidFromCodon (UUC)
       AddAminoAcidToProtein (UUU)
       Stop                  (UAA)
    digest:
        DigestCell           (UCA)
        Stop                 (UAA)
    divide:
        DivideCell           (UAC)
        Stop                 (UAA)
     */
    public int getGeneSize() {
        // TODO: calculate this based on dnaStr:
        return 4;
    }
    private static String dnaStr =
            promoterCode + "TTGTCT" + terminatorCode +
                    promoterCode + "TTATTCTTT" + terminatorCode +
                    promoterCode + "TCA" + terminatorCode +
                    promoterCode + "TAC" + terminatorCode;
    /*!!
    private static String dnaStr2 =
            promoterCode + proteinTypeStem + proteinNamePolymerase +
                    "TTGTCT" + terminatorCode +
                    promoterCode + proteinTypeStem + proteinNameRibosome +
                    "TTATTCTTT" + terminatorCode +
                    promoterCode + proteinTypeDivision + proteinNameDivide +
                    "TAC" + terminatorCode +
                    promoterCode + proteinTypeDigestion + proteinNameDigest +
                    "TAG" + terminatorCode;
     */

    // use static DNA; add build proteins
    public static Cell getSyntheticCell() {
        if (syntheticCell == null) {
            syntheticCell = new Cell();
            syntheticCell.dna = buildDNAFromString(dnaStr);
            // create resources for 1 daughter cell of 4 proteins:
            for (int i = 0; i < 1; i++) {
                syntheticCell.aminoAcidList.add(new AddAminoAcidToProtein(syntheticCell));
                syntheticCell.aminoAcidList.add(new GetAminoAcidFromCodon(syntheticCell));
                syntheticCell.aminoAcidList.add(new GetCodonFromRNA(syntheticCell));
                syntheticCell.aminoAcidList.add(new FindNextGene(syntheticCell));
                syntheticCell.aminoAcidList.add(new GetRNAFromGene(syntheticCell));
                syntheticCell.aminoAcidList.add(new DigestCell(syntheticCell));
                syntheticCell.aminoAcidList.add(new DivideCell(syntheticCell));
            }
            for (int i = 0; i < 17; i++) {
                syntheticCell.nucleotideList.add(new Uracil());
            }
            for (int i = 0; i < 11; i++) {
                syntheticCell.nucleotideList.add(new Adenine());
            }
            for (int i = 0; i < 4; i++) {
                syntheticCell.nucleotideList.add(new Cytosine());
            }
            for (int i = 0; i < 1; i++) {
                syntheticCell.nucleotideList.add(new Guanine());
            }
            Protein rnaPolymerase = new Protein();
            rnaPolymerase.add(new FindNextGene(syntheticCell));
            rnaPolymerase.add(new GetRNAFromGene(syntheticCell));
            Protein ribosome = new Protein();
            ribosome.add(new GetCodonFromRNA(syntheticCell));
            ribosome.add(new GetAminoAcidFromCodon(syntheticCell));
            ribosome.add(new AddAminoAcidToProtein(syntheticCell));
            Protein proteinDigest = new Protein();
            proteinDigest.add(new DigestCell(syntheticCell));
            Protein proteinDivide = new Protein();
            proteinDivide.add(new DivideCell(syntheticCell));
            syntheticCell.addProtein(rnaPolymerase);
            syntheticCell.addProtein(ribosome);
            syntheticCell.addProtein(proteinDigest);
            syntheticCell.addProtein(proteinDivide);
        }
        return syntheticCell;
    }
    /*??
    public static Cell makeFirstCell() {
        return getSyntheticCell().split();
    }
    */
    public Cell(DNA dna) {
        this.dna = dna;
	}
    public Cell() {
        
    }
	public DNA getDNA() {
		return dna;
	}
	public void addProtein(Protein protein) {
		proteinList.add(protein);
		Thread thread = new Thread(protein);
		MasterDesigner.print(thread.getName() +
				" starting for protein " + protein);
		thread.start();
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
			while (true) {
                Nucleotide nucleotide = getNucleotideByName(name);
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
			while (true) {
				RNA rna = getRNA();
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
			while (true) {
                AminoAcid aminoAcid = getAminoAcidByCodon(codon);
				if (aminoAcid != null) return aminoAcid;
				MasterDesigner.print(Thread.currentThread().getName() +
						" waiting for amino acid for codon " + codon);
				try { aminoAcidList.wait(); }
				catch(InterruptedException e) {}
			}
		}
	}

    public List<Protein> getProteinList() {
        return proteinList;
    }

    public interface CellReadyToSplitListener {
        void splitCell();
    }
    public int waitForEnoughProteins(CellReadyToSplitListener splitListener) {
        this.splitListener = splitListener;
        synchronized (proteinList) {
            while (true) {
                int geneSize = getGeneSize();
                int proteinSize = proteinList.size();
                if (proteinSize >= (geneSize * 2)) {
                    splitListener.splitCell();
                    return geneSize;
                }
                MasterDesigner.print(Thread.currentThread().getName() +
                        " cell divide waiting for " +
                        ((geneSize * 2) - proteinSize) +
                        " more proteins");
                try { proteinList.wait(); }
                catch(InterruptedException e) {}
            }
        }
    }
	private RNA getRNA() {
		int size = rnaList.size();
		if (size == 0) return null;
		RNA rna = rnaList.remove(0);
		return rna;
	}
	public void printNucleotides() {
		System.out.print("Cell's nucleotides:");
		for (Nucleotide nucleotide: nucleotideList) {
			System.out.print(" " + nucleotide.getCode());
		}
        System.out.println();
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

    /*!!
    private Cell splitCell() {
        // TODO: use life to copy DNA
        String dnaStr = this.dna.dnaToString();
        DNA daughterDNA = buildDNAFromString(dnaStr);
        Cell daughterCell = new Cell();
        daughterCell.dna = daughterDNA;
        if (rnaList.size() > 0) {
            // TODO: wait for ribosome to finish building from rnaList
            System.out.println("ranList.size()=" + rnaList.size());
            return null;
        }
        // TODO: find polymerase, instead of assuming it's proteinList[0]
        proteinList.get(0).action(null);
        // TODO: find ribosome, instead of assuming it's proteinList[1]
        Protein ribosome = proteinList.get(1);
        int oldProteinCount = this.proteinList.size();
        if (!ribosome.isRunning()) {
            ribosome.action(null);
        }
        try {
            Thread.sleep(500); // give ribosome time to finish its job
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int newProteinCount = this.proteinList.size();
        for (int i = oldProteinCount; i < newProteinCount; i++) {
            Protein protein = proteinList.remove(oldProteinCount);
            protein.setCell(daughterCell);
            daughterCell.addProtein(protein);
        }
        return daughterCell;
    }
    */
    // Convenience method
    public static DNA buildDNAFromString(String dnaStr) {
        DNA dna = new DNA();
        for (int i = 0; i < dnaStr.length(); i++) {
            char code = dnaStr.charAt(i);
            switch (code) {
                case 'A':
                    dna.add(new Adenine());
                    break;
                case 'T':
                    dna.add(new Thymine());
                    break;
                case 'C':
                    dna.add(new Cytosine());
                    break;
                case 'G':
                    dna.add(new Guanine());
                    break;
            }
        }
        return dna;
    }
    @Override
    public boolean equals(Object any) {
        if (any instanceof Cell) {
            Cell that = (Cell) any;
            if (!that.dna.dnaToString().equals(this.dna.dnaToString())) return false;
            int proteinListSize = that.proteinList.size();
            if (that.proteinList.size() != proteinListSize) return false;
            for (int i = 0; i < proteinListSize; i++) {
                if (!that.proteinList.get(i).equals(this.proteinList.get(i))) {
                    return false;
                }
            }
            return true;
        } else return false;
    }
    @Override
    public int hashCode() {
        if (this.hashCode == 0) {
            // lazy evaluation:
            this.hashCode = this.dna.dnaToString().hashCode();
        }
        return this.hashCode;
    }

}
