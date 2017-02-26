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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static jarden.life.CellData.nucleotideNames;
import static jarden.life.nucleicacid.NucleicAcid.promoterCode;
import static jarden.life.nucleicacid.NucleicAcid.terminatorCode;

public class Cell implements Food {
    private static boolean verbose = true;
    private static int adenineFor1Cell = 11;
    private static int cytosineFor1Cell = 4;
    private static int guanineFor1Cell = 1;
    private static int thymineFor1Cell = 29;
    private static int uracilFor1Cell = 17;
    public static String[] nucleotidesFor1Cell = {
            String.valueOf(adenineFor1Cell),
            String.valueOf(cytosineFor1Cell),
            String.valueOf(guanineFor1Cell),
            String.valueOf(thymineFor1Cell),
            String.valueOf(uracilFor1Cell)
    };
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
    private CellListener cellListener;
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
    public static void log(String s) {
        if (verbose) {
            System.out.println(Thread.currentThread().getName() + " " + s);
        }
    }
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
            synCell.aminoAcidList.add(new AddAminoAcidToProtein());
            synCell.aminoAcidList.add(new GetAminoAcidFromCodon());
            synCell.aminoAcidList.add(new GetCodonFromRNA());
            synCell.aminoAcidList.add(new FindNextGene());
            synCell.aminoAcidList.add(new GetRNAFromGene());
            synCell.aminoAcidList.add(new DigestFood());
            synCell.aminoAcidList.add(new DivideCell());
            synCell.aminoAcidList.add(new AddAminoAcidToProtein());
            synCell.aminoAcidList.add(new GetAminoAcidFromCodon());
        }
        for (int i = 0; i < uracilFor1Cell; i++) {
            synCell.nucleotideList.add(new Uracil());
        }
        for (int i = 0; i < adenineFor1Cell; i++) {
            synCell.nucleotideList.add(new Adenine());
        }
        for (int i = 0; i < cytosineFor1Cell; i++) {
            synCell.nucleotideList.add(new Cytosine());
        }
        for (int i = 0; i < guanineFor1Cell; i++) {
            synCell.nucleotideList.add(new Guanine());
        }
        Protein rnaPolymerase = new Protein(synCell);
        rnaPolymerase.add(new FindNextGene());
        rnaPolymerase.add(new GetRNAFromGene());
        Protein ribosome = new Protein(synCell);
        ribosome.add(new GetCodonFromRNA());
        ribosome.add(new GetAminoAcidFromCodon());
        ribosome.add(new AddAminoAcidToProtein());
        Protein proteinDigest = new Protein(synCell);
        proteinDigest.add(new DigestFood());
        Protein proteinDivide = new Protein(synCell);
        proteinDivide.add(new DivideCell());
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
     * @param cellListener called asynchronously when new cell created.
     */
    // TODO: test this:
    public static void makeFirstCell(CellListener cellListener) {
        Cell syntheticCell = getSyntheticCell();
        syntheticCell.setCellListener(cellListener);
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

    /**
     * Get UI data for this cell.
     * @return UI data for this cell.
     */
    public CellData getCellData() {
        CellData cellData = new CellData();
        cellData.cellId = this.id;
        // Proteins:
        HashMap<String, Integer> proteinNameCountMap = new HashMap<>();
        synchronized (proteinList) {
            for (Protein protein : proteinList) {
                String name = protein.toString();
                Integer ct = proteinNameCountMap.get(name);
                ct = (ct == null) ? 1 : (ct + 1);
                proteinNameCountMap.put(name, ct);
            }
        }
        cellData.proteinNameCts = new CellData.ProteinNameCount[proteinNameCountMap.size()];
        Set<String> keys = proteinNameCountMap.keySet();
        int j = 0;
        for (String key: keys) {
            cellData.proteinNameCts[j] = new CellData.ProteinNameCount(key,
                    proteinNameCountMap.get(key));
            ++j;
        };
        // Amino Acids:
        int aminoAcidNamesLength = CellData.aminoAcidNames.length;
        cellData.aminoAcidCts = new int[aminoAcidNamesLength];
        synchronized (aminoAcidList) {
            for (AminoAcid aminoAcid : aminoAcidList) {
                String aminoAcidName = aminoAcid.getName();
                for (int i = 0; i < aminoAcidNamesLength; i++) {
                    if (aminoAcidName.equals(CellData.aminoAcidNames[i])) {
                        ++cellData.aminoAcidCts[i];
                        break;
                    }
                }
            }
        }
        // Nucleotides:
        cellData.nucleotideCts = new int[nucleotideNames.length];
        synchronized (nucleotideList) {
            for (Nucleotide nucleotide : nucleotideList) {
                if (nucleotide instanceof Adenine) ++cellData.nucleotideCts[0];
                else if (nucleotide instanceof Cytosine) ++cellData.nucleotideCts[1];
                else if (nucleotide instanceof Guanine) ++cellData.nucleotideCts[2];
                else if (nucleotide instanceof Thymine) ++cellData.nucleotideCts[3];
                else if (nucleotide instanceof Uracil) ++cellData.nucleotideCts[4];
                else throw new IllegalStateException("unknown nucleotide: " + nucleotide);
            }
        }
        return cellData;
    }
    private void cellChanged() {
        if (cellListener != null) {
            cellListener.onCellUpdated(id);
        }
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
    public List<RNA> getRNAList() {
        return rnaList;
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
        log(toString() + "; proteinCt=" + proteinList.size());
        Thread proteinThread = protein.getThread();
        if (this.active && (proteinThread == null || !proteinThread.isAlive())) {
            protein.start();
        }
        cellChanged();
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
				log("waiting for nucleotide " + name);
				try { nucleotideList.wait(); }
				catch(InterruptedException e) {
                    log("interrupted while waiting for nucleotide " + name);
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
				log("waiting for some RNA");
				try { rnaList.wait(); }
                catch(InterruptedException e) {
                    log("interrupted while waiting for some RNA");
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
				log("waiting for amino acid for codon " + codon);
				try { aminoAcidList.wait(); }
				catch(InterruptedException e) {
                    log("interrupted while waiting for amino acid for codon " +
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
                log("waiting for food ");
                try { foodList.wait(); }
                catch(InterruptedException e) {
                    log("interrupted while waiting for food");
                    Thread.currentThread().interrupt();
                    return null;
                }
            }
        }
    }
    /*
    Not thread-safe, so only call from synchronized method
     */
	private RNA getRNA() {
		int size = rnaList.size();
		if (size == 0) return null;
		RNA rna = rnaList.remove(0);
		return rna;
	}
    // not thread-safe, so only call from synchronized method
	public void printNucleotides() {
		System.out.print("Cell's nucleotides:");
		for (Nucleotide nucleotide: nucleotideList) {
			System.out.print(" " + nucleotide.getCode());
		}
        System.out.println();
    }
    // not thread-safe, so only call from synchronized method
	public void printRNA() {
		System.out.println("Cell's rna:");
		for (RNA rna: rnaList) {
			System.out.println("   " + rna);
		}
	}
    /*
    Not thread-safe, so only call from synchronized method
     */
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
    public synchronized void printCell() {
        this.printNucleotides();
        this.printRNA();
        this.printProteins();
        this.printThreads();
    }
	/*
	 * Find aminoAcid with specified name; if found, remove from list.
	 * Not thread-safe, so only call from synchronized method
	 */
	private AminoAcid getAminoAcidByCodon(Codon codon) {
        for (AminoAcid aminoAcid : aminoAcidList) {
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
	 * Not thread-safe, so only call from synchronized method
	 */
	private Nucleotide getNucleotideByName(String name) {
		for (int i = nucleotideList.size() - 1; i >= 0; i--) {
			Nucleotide nucleotide = nucleotideList.get(i);
			if (nucleotide.getName().equals(name)) {
				nucleotideList.remove(nucleotide);
				return nucleotide;
			}
		}
		log("no nucleotide found for " + name);
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
        return "cell" + id + "; gen" + generation;
    }
    @Override
    public boolean equals(Object any) {
        if (any instanceof Cell) {
            return id == ((Cell) any).id;
        } else return false;
    }

    /**
     * Is 'any' a perfect copy of 'this'.
     * @param any other object
     * @return true if 'any' has the same dna and proteins as 'this'
     */
    public boolean isCopy(Object any) {
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
    public void setCellListener(CellListener cellListener) {
        this.cellListener = cellListener;
    }
    public CellListener getCellListener() {
        return cellListener;
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
