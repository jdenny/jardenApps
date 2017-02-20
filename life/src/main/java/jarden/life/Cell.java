package jarden.life;

import jarden.life.aminoacid.AddAminoAcidToProtein;
import jarden.life.aminoacid.AminoAcid;
import jarden.life.aminoacid.DigestFood;
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

import java.util.LinkedList;
import java.util.List;

import static jarden.life.nucleicacid.NucleicAcid.promoterCode;
import static jarden.life.nucleicacid.NucleicAcid.terminatorCode;

public class Cell implements Food {
    private static Cell syntheticCell;
    private static int currentId = 0;
    private boolean active = true; // true means run threads on create

    private int id;
    private int generation = 1;
    private DNA dna;
    private final List<Protein> proteinList = new LinkedList<>();
	private final List<AminoAcid> aminoAcidList = new LinkedList<>();
	private final List<Nucleotide> nucleotideList = new LinkedList<>();
	private final List<RNA> rnaList = new LinkedList<>();
    private final List<Food> foodList = new LinkedList<>();
    private int hashCode = 0;
    private OnNewCellListener onNewCellListener;
    private boolean divideCellRunning;

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
		DigestFood              UCA

    In this pseudo DNA are 4 genes to make the following proteins:
    polymerase:               (RNA codons)
        FindNextGene          (UUG)
        GetRNAFromGene        (UCU)
        Stop                  (UAA)
    ribosome:
        GetCodonFromRNA       (UUA)
        GetAminoAcidFromCodon (UUC)
        AddAminoAcidToProtein (UUU)
        Stop                  (UAA)
    digest:
        DigestFood            (UCA)
        Stop                  (UAA)
    divide:
        DivideCell            (UAC)
        Stop                  (UAA)

    Common thread numbers:
        Cell 1                Cell 2
        5  polymerase
        6  ribsome
        7  digestFood
        8  divideCell

        9  polymerase     ->  12
        10 ribsome        ->  13
        11 digestFood     ->  14
        -  divideCell     ->  15

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

    public static Cell getSyntheticCell() {
        if (syntheticCell == null) {
            syntheticCell = makeSyntheticCell(true);
        }
        return syntheticCell;
    }
    public static Cell makeSyntheticCell(boolean active) {
        Cell synCell = new Cell();
        synCell.setActive(active);
        synCell.dna = buildDNAFromString(dnaStr);
        // create resources for 1 daughter cell of 4 proteins:
        for (int i = 0; i < 1; i++) {
            synCell.aminoAcidList.add(new AddAminoAcidToProtein(synCell));
            synCell.aminoAcidList.add(new GetAminoAcidFromCodon(synCell));
            synCell.aminoAcidList.add(new GetCodonFromRNA(synCell));
            synCell.aminoAcidList.add(new FindNextGene(synCell));
            synCell.aminoAcidList.add(new GetRNAFromGene(synCell));
            synCell.aminoAcidList.add(new DigestFood(synCell));
            synCell.aminoAcidList.add(new DivideCell(synCell));
            synCell.aminoAcidList.add(new AddAminoAcidToProtein(synCell));
            synCell.aminoAcidList.add(new GetAminoAcidFromCodon(synCell));
        }
        for (int i = 0; i < 20; i++) { // minimum is 17
            synCell.nucleotideList.add(new Uracil());
        }
        for (int i = 0; i < 11; i++) {
            synCell.nucleotideList.add(new Adenine());
        }
        for (int i = 0; i < 4; i++) {
            synCell.nucleotideList.add(new Cytosine());
        }
        for (int i = 0; i < 1; i++) {
            synCell.nucleotideList.add(new Guanine());
        }
        Protein rnaPolymerase = new Protein(synCell);
        rnaPolymerase.add(new FindNextGene(synCell));
        rnaPolymerase.add(new GetRNAFromGene(synCell));
        Protein ribosome = new Protein(synCell);
        ribosome.add(new GetCodonFromRNA(synCell));
        ribosome.add(new GetAminoAcidFromCodon(synCell));
        ribosome.add(new AddAminoAcidToProtein(synCell));
        Protein proteinDigest = new Protein(synCell);
        proteinDigest.add(new DigestFood(synCell));
        Protein proteinDivide = new Protein(synCell);
        proteinDivide.add(new DivideCell(synCell));
        synCell.addProtein(rnaPolymerase);
        synCell.addProtein(ribosome);
        synCell.addProtein(proteinDigest);
        synCell.addProtein(proteinDivide);
        return synCell;
    }

    /**
     * Asynchronously create a new cell from syntheticCell. The new cell will
     * be returned via onNewCellListener.
     *
     * @param onNewCellListener called asynchronously when new cell created.
     */
    // TODO: test this:
    public static void makeFirstCell(OnNewCellListener onNewCellListener) {
        Cell syntheticCell = getSyntheticCell();
        syntheticCell.setOnNewCellListener(onNewCellListener);
    }
    public Cell(DNA dna) {
        this();
        this.dna = dna;
	}
    public Cell() {
        this.id = ++currentId;
    }
    public int getId() {
        return this.id;
    }
    public int getGeneration() {
        return this.generation;
    }
    public int getProteinCt() {
        return this.proteinList.size();
    }

    @Override
    public List<Protein> getProteinList() {
        return proteinList;
    }
    @Override
    public List<AminoAcid> getAminoAcidList() {
        return this.aminoAcidList;
    }
    @Override
    public List<Nucleotide> getNucleotideList() {
        return nucleotideList;
    }
    @Override
    public DNA getDNA() {
		return dna;
	}
	public void addProtein(Protein protein) {
        synchronized (proteinList) {
            proteinList.add(protein);
            proteinList.notifyAll();
        }
        MasterDesigner.print(toString() + "; proteinCt=" + proteinList.size());
        if (this.active) protein.start();
	}
	public void addAminoAcids(List<AminoAcid> aminoAcids) {
		synchronized (aminoAcidList) {
			aminoAcidList.addAll(aminoAcids);
			aminoAcidList.notifyAll();
		}
	}
	public void addNucleotides(List<Nucleotide> nucleotides) {
		synchronized (nucleotideList) {
			nucleotideList.addAll(nucleotides);
			nucleotideList.notifyAll();
		}
	}
	public void addRNA(RNA rna) {
		synchronized(rnaList) {
			rnaList.add(rna);
			rnaList.notifyAll();
		}
	}
    public void addFood(List<Food> foods) {
        synchronized (foodList) {
            foodList.addAll(foods);
            foodList.notifyAll();
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
				catch(InterruptedException e) {
                    MasterDesigner.print(Thread.currentThread().getName() +
                            " interrupted while waiting for nucleotide " + name);
                    Thread.currentThread().interrupt();
                    return null;
                }
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
                catch(InterruptedException e) {
                    MasterDesigner.print(Thread.currentThread().getName() +
                            " interrupted while waiting for some RNA");
                    Thread.currentThread().interrupt();
                    return null;
                }
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
				catch(InterruptedException e) {
                    MasterDesigner.print(Thread.currentThread().getName() +
                        " interrupted while waiting for amino acid for codon " +
                            codon);
                    Thread.currentThread().interrupt();
                    return null;
                }
			}
		}
	}
    public Food waitForFood(Object object) {
        synchronized (foodList) {
            while (true) {
                if (foodList.size() > 0) {
                    return foodList.remove(0);
                }
                MasterDesigner.print(Thread.currentThread().getName() +
                        " waiting for food ");
                try { foodList.wait(); }
                catch(InterruptedException e) {
                    MasterDesigner.print(Thread.currentThread().getName() +
                            " interrupted while waiting for food");
                    Thread.currentThread().interrupt();
                    return null;
                }
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
	 * Find nucleotide with specified name; if found, remove from list.
	 */
	private Nucleotide getNucleotideByName(String name) {
		for (int i = nucleotideList.size() - 1; i >= 0; i--) {
			Nucleotide nucleotide = nucleotideList.get(i);
			if (nucleotide.getName().equals(name)) {
				nucleotideList.remove(nucleotide);
				return nucleotide;
			}
		}
		MasterDesigner.print("no nucleotide found for " + name);
		return null;
	}

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
    public String toString() {
        return "cell.id=" + id + "; generation=" + generation;
    }
    @Override
    public boolean equals(Object any) {
        if (any instanceof Cell) {
            Cell that = (Cell) any;
            if (!that.dna.dnaToString().equals(this.dna.dnaToString())) return false;
            int proteinListSize = proteinList.size();
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
    public void setGeneration(int generation) {
        this.generation = generation;
    }
    public void setOnNewCellListener(OnNewCellListener onNewCellListener) {
        this.onNewCellListener = onNewCellListener;
    }
    public OnNewCellListener getOnNewCellListener() {
        return onNewCellListener;
    }
    public boolean isDivideCellRunning() {
        return this.divideCellRunning;
    }
    public void setDivideCellRunning(boolean divideCellRunning) {
        this.divideCellRunning = divideCellRunning;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
