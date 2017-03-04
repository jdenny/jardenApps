package jarden.life;

import jarden.life.aminoacid.AddAminoAcidToProtein;
import jarden.life.aminoacid.AminoAcid;
import jarden.life.aminoacid.CopyDNA;
import jarden.life.aminoacid.DigestFood;
import jarden.life.aminoacid.DivideCell;
import jarden.life.aminoacid.EatFood;
import jarden.life.aminoacid.GetAminoAcidFromCodon;
import jarden.life.aminoacid.GetCodonFromRNA;
import jarden.life.aminoacid.Polymerase;
import jarden.life.aminoacid.WaitForEnoughProteins;
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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static jarden.life.CellData.nucleotideNames;
import static jarden.life.nucleicacid.Nucleotide.promoterAdenineCt;
import static jarden.life.nucleicacid.Nucleotide.promoterCode;
import static jarden.life.nucleicacid.Nucleotide.promoterThymineCt;
import static jarden.life.nucleicacid.Nucleotide.terminatorCode;

public class Cell implements Food {
    private static boolean verbose = true;
    private static Cell syntheticCell;
    private static int currentId = 0;
    private static String[] geneStrs = {
            "TGG",       // polymerase: Polymerase // was: TTGTCT - FindNextGene, GetRNAFromGene
            "TTATTCTTT", // ribosome: GetCodonFromRNA, GetAminoAcidFromCodon, AddAminoAcidToProtein
            "UGC",       // eatFood: EatFood
            "TCA",       // digest: DigestFood
            "TCGTGTTAC"  // divide: WaitForEnoughProteins, CopyDNA, DivideCell
    };
    private static String dnaStr = getDnaStr();

    private final Lock proteinListLock = new ReentrantLock();
    private final Condition cellReadyToDivide = proteinListLock.newCondition();
    private final Condition needMoreProteins = proteinListLock.newCondition();

    private final Lock aminoAcidListLock = new ReentrantLock();
    private final Condition aminoAcidAvailable = aminoAcidListLock.newCondition();

    private final Lock foodListLock = new ReentrantLock();
    private final Condition foodAvailable = foodListLock.newCondition();
    private final Condition needMoreFood = foodListLock.newCondition();

    private final Lock nucleotideListLock = new ReentrantLock();
    private final Condition nucleotideAvailable = nucleotideListLock.newCondition();

    private final Lock rnaListLock = new ReentrantLock();
    private final Condition rnaAvailable = rnaListLock.newCondition();
    private final Condition needMoreRNA = rnaListLock.newCondition();

    private final Lock dnaIndexLock = new ReentrantLock();

    private final CellEnvironment cellEnvironment;
    private int id;
    private int generation = 1;
    private DNA dna;
    private int dnaIndex = 0;
    private final List<Protein> proteinList = new LinkedList<>();
	private final List<AminoAcid> aminoAcidList = new LinkedList<>();
	private final List<Nucleotide> nucleotideList = new LinkedList<>();
	private final List<RNA> rnaList = new LinkedList<>();
    private final List<Food> foodList = new LinkedList<>();
    private int hashCode = 0;
    private boolean divideCellRunning;
    private int geneSize;
    // TODO: when proper aminoAcids, replace length with 20
    // in same sequence as aminoAcid list
    private int[] aminoAcidTargets = new int[CellData.aminoAcidNames.length];
    // indexed by nucleotide.getIndex(); 5 is number of nucleotide types
    private int[] nucleotideTargets = new int[5];
    private int[] nucleotideActuals = new int[5];
    /*
    Common thread numbers:
        Cell 1                Cell 2
        5  polymerase
        6  ribsome
        7  eatFood
        8  digestFood
        9  divideCell

        10 polymerase     ->  14
        11 ribsome        ->  15
        12 eatFood        ->  16
        13 digestFood     ->  17
        -  divideCell     ->  18

	Current implementation of codonTable.
	See Nucleotide for real-life codonTable.
	    (Promoter               UAUAAU)
		AddAminoAcidToProtein	UUU
		GetAminoAcidFromCodon	UUC
		GetCodonFromRNA			UUA
		             			UUG
		             			UCU
		            			UCC
		Stop					UAA, UAG, UGA
		WaitForEnoughProteins   UCG
		CopyDNA                 UGU
		DivideCell              UAC
		DigestFood              UCA
		EatFood                 UGC
		Polymerase              UGG
     */

    public static Cell getSyntheticCell(CellEnvironment cellEnvironment) throws InterruptedException {
        if (syntheticCell == null) {
            syntheticCell = makeSyntheticCell(cellEnvironment);
        }
        return syntheticCell;
    }

    /**
     * This is the God cell. Cells are able to reproduce, by dividing, but how is the
     * first cell made? Here we form it "out of dust from the ground" - Genesis 2:7.
     * @param cellEnvironment
     * @return
     * @throws InterruptedException
     */
    public static Cell makeSyntheticCell(CellEnvironment cellEnvironment) throws InterruptedException {
        Cell synCell = new Cell(buildDNAFromString(dnaStr), cellEnvironment);
        synCell.analyseDNA(); // set targets
        // create resources for 1 daughter cell of 4 proteins:
        for (int i = 0; i < 1; i++) {
            synCell.aminoAcidList.add(new AddAminoAcidToProtein());
            synCell.aminoAcidList.add(new CopyDNA());
            synCell.aminoAcidList.add(new DigestFood());
            synCell.aminoAcidList.add(new DivideCell());
            synCell.aminoAcidList.add(new EatFood());
            synCell.aminoAcidList.add(new GetAminoAcidFromCodon());
            synCell.aminoAcidList.add(new GetCodonFromRNA());
            synCell.aminoAcidList.add(new Polymerase());
            synCell.aminoAcidList.add(new WaitForEnoughProteins());
        }
        List<Nucleotide> newNucleotides = new ArrayList<>();
        for (int i = 0; i < synCell.nucleotideTargets[0]; i++) {
            newNucleotides.add(new Adenine());
        }
        for (int i = 0; i < synCell.nucleotideTargets[1]; i++) {
            newNucleotides.add(new Cytosine());
        }
        for (int i = 0; i < synCell.nucleotideTargets[2]; i++) {
            newNucleotides.add(new Guanine());
        }
        for (int i = 0; i < synCell.nucleotideTargets[3]; i++) {
            newNucleotides.add(new Thymine());
        }
        for (int i = 0; i < synCell.nucleotideTargets[4]; i++) {
            newNucleotides.add(new Uracil());
        }
        synCell.addNucleotides(newNucleotides);
        Protein rnaPolymerase = new Protein(synCell);
        rnaPolymerase.add(new Polymerase());
        Protein ribosome = new Protein(synCell);
        ribosome.add(new GetCodonFromRNA());
        ribosome.add(new GetAminoAcidFromCodon());
        ribosome.add(new AddAminoAcidToProtein());
        Protein proteinDigest = new Protein(synCell);
        proteinDigest.add(new DigestFood());
        Protein eatFood = new Protein(synCell);
        eatFood.add(new EatFood());
        Protein proteinDivide = new Protein(synCell);
        proteinDivide.add(new WaitForEnoughProteins());
        proteinDivide.add(new CopyDNA());
        proteinDivide.add(new DivideCell());
        synCell.addProtein(rnaPolymerase);
        synCell.addProtein(ribosome);
        synCell.addProtein(eatFood);
        synCell.addProtein(proteinDigest);
        synCell.addProtein(proteinDivide);
        return synCell;
    }
    private Cell(DNA dna, CellEnvironment cellEnvironment) {
        this.id = ++currentId;
        this.dna = dna;
        this.cellEnvironment = cellEnvironment;
    }
    public Cell(DNA dna, Cell parentCell) {
        this(dna, parentCell.getCellEnvironment());
        nucleotideTargets = parentCell.nucleotideTargets;
    }
    private static String getDnaStr() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String geneStr: geneStrs) {
            stringBuilder.append(promoterCode); // part of DNA, but not part of RNA
            stringBuilder.append(geneStr);
            stringBuilder.append(terminatorCode); // part of RNA
        }
        return stringBuilder.toString();
    }
    private void analyseDNA() {
        geneSize = geneStrs.length;
        for (int i = 0; i < dnaStr.length(); i++) {
            if (dnaStr.charAt(i) == 'T') {
                ++nucleotideTargets[3]; // for DNA
                ++nucleotideTargets[4]; // for RNA
            }
            else if (dnaStr.charAt(i) == 'C') nucleotideTargets[1] += 2; // 1 for DNA, 1 for RNA
            else if (dnaStr.charAt(i) == 'A') nucleotideTargets[0] += 2;
            else if (dnaStr.charAt(i) == 'G') nucleotideTargets[2] += 2;
        }
        // promoters part of DNA, but not part of RNA, but above loop has already added them
        // for RNA
        nucleotideTargets[4] -= (geneSize * promoterThymineCt);
        nucleotideTargets[0] -= (geneSize * promoterAdenineCt);
    }
    /**
     * General purpose log method, static so can be called from anywhere.
     * @param s message to be printed, if verbose is true.
     */
    public static void log(String s) {
        if (verbose) {
            System.out.println(Thread.currentThread().getName() + " " + s);
        }
    }
    /**
     * Version of log that also logs the cell id.
     * @param s message to be printed, if verbose is true.
     */
    public void logId(String s) {
        if (verbose) {
            System.out.println("Cell-" + id + " " +
                    Thread.currentThread().getName() + " " + s);
        }
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
    public CellEnvironment getCellEnvironment() {
        return cellEnvironment;
    }

    /**
     * Return true if the cell has enough amino acids.
     * Current algorithm is: enough for 1 of each protein.
     * @return
     */
    public boolean enoughAminoAcids() {

//        // analyse proteins in cell as originally built
//        for (int i = 0; i < geneSize; i++) {
//            Protein protein = proteinList.get(i);
//        }
        return true;

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
        proteinListLock.lock();
        try {
            for (Protein protein : proteinList) {
                String name = protein.toString();
                Integer ct = proteinNameCountMap.get(name);
                ct = (ct == null) ? 1 : (ct + 1);
                proteinNameCountMap.put(name, ct);
            }
        } finally {
            proteinListLock.unlock();
        }
        cellData.proteinNameCts = new NameCount[proteinNameCountMap.size()];
        Set<String> keys = proteinNameCountMap.keySet();
        int j = 0;
        for (String key: keys) {
            cellData.proteinNameCts[j] = new NameCount(key,
                    proteinNameCountMap.get(key));
            ++j;
        };
        // Amino Acids:
        int aminoAcidNamesLength = CellData.aminoAcidNames.length;
        cellData.aminoAcidCts = new int[aminoAcidNamesLength];
        aminoAcidListLock.lock();
        try {
            for (AminoAcid aminoAcid : aminoAcidList) {
                String aminoAcidName = aminoAcid.getName();
                for (int i = 0; i < aminoAcidNamesLength; i++) {
                    if (aminoAcidName.equals(CellData.aminoAcidNames[i])) {
                        ++cellData.aminoAcidCts[i];
                        break;
                    }
                }
            }
        } finally {
            aminoAcidListLock.unlock();
        }
        // Nucleotides:
        cellData.nucleotideCts = new int[nucleotideNames.length];
        nucleotideListLock.lock();
        try {
            for (Nucleotide nucleotide : nucleotideList) {
                if (nucleotide instanceof Adenine) ++cellData.nucleotideCts[0];
                else if (nucleotide instanceof Cytosine) ++cellData.nucleotideCts[1];
                else if (nucleotide instanceof Guanine) ++cellData.nucleotideCts[2];
                else if (nucleotide instanceof Thymine) ++cellData.nucleotideCts[3];
                else if (nucleotide instanceof Uracil) ++cellData.nucleotideCts[4];
                else throw new IllegalStateException("unknown nucleotide: " + nucleotide);
            }
        } finally {
            nucleotideListLock.unlock();
        }
        return cellData;
    }
	public void addProtein(Protein protein) throws InterruptedException {
        proteinListLock.lockInterruptibly();
        try {
            proteinList.add(protein);
            if (cellReadyToDivide()) {
                cellReadyToDivide.signalAll();
            }
        } finally {
            proteinListLock.unlock();
        }
        logId("addProtein(); proteinCt=" + proteinList.size());
        Thread proteinThread = protein.getThread();
        if (proteinThread == null || !proteinThread.isAlive()) {
            protein.start();
        }
	}
	public void addAminoAcids(List<AminoAcid> aminoAcids) throws InterruptedException {
        aminoAcidListLock.lockInterruptibly();
        try {
            aminoAcidList.addAll(aminoAcids);
            aminoAcidAvailable.signalAll();
        } finally {
            aminoAcidListLock.unlock();
        }
	}
	public void addNucleotides(List<Nucleotide> nucleotides) throws InterruptedException {
        nucleotideListLock.lockInterruptibly();
        try {
            for (Nucleotide nucleotide: nucleotides) {
                nucleotideList.add(nucleotide);
                ++nucleotideTargets[nucleotide.getIndex()];
            }
            nucleotideAvailable.signalAll();
        } finally {
            nucleotideListLock.unlock();
        }
	}
	public void addRNA(RNA rna) throws InterruptedException {
        rnaListLock.lockInterruptibly();
        try {
            rnaList.add(rna);
            rnaAvailable.signalAll();
        } finally {
            rnaListLock.unlock();
        }
	}
    public void addFood(Food food) throws InterruptedException {
        foodListLock.lockInterruptibly();
        try {
            foodList.add(food);
            foodAvailable.signalAll();
        } finally {
            foodListLock.unlock();
        }
    }

    /**
     * Wait for a nucleotide suitable to make a base-pair with supplied
     * nucleotide.
     * @param nucleotide
     * @param dna if true, look for dna base-pair, else rna base-pair.
     * @return nucleotide that can form a base pair with supplied
     * nucleotide.
     * @throws InterruptedException
     */
    public Nucleotide waitForNucleotide(Nucleotide nucleotide, boolean dna)
            throws InterruptedException {
        int index = nucleotide.getIndex();
        if (nucleotideActuals[index] < nucleotideTargets[index]) {
            foodListLock.lockInterruptibly();
            try {
                // better than needMoreResources.signalAll(), as that
                // would only activate Digest, which can't start until
                // there is food available; perhaps the required nucleotide
                // is being digested this very moment, but it's better to
                // be overfed than underfed
                needMoreFood.signalAll();
            } finally {
                foodListLock.unlock();
            }
        }
        Nucleotide bondingNucleotide;
        nucleotideListLock.lockInterruptibly();
        try {
            while ((bondingNucleotide = getNucleotide(nucleotide, dna)) == null) {
                logId("waiting for nucleotide to bond with " + nucleotide);
                nucleotideAvailable.await();
            }
            return bondingNucleotide;
        } finally {
            nucleotideListLock.unlock();
        }
	}
	public RNA waitForRNA() throws InterruptedException {
        RNA rna;
        rnaListLock.lockInterruptibly();
        try {
            while ((rna = getRNA()) == null) {
                logId("waiting for some RNA");
                rnaAvailable.await();
            }
            if (rnaList.size() < geneSize) needMoreRNA.signalAll();
            return rna;
        } finally {
            rnaListLock.unlock();
        }
	}
	public AminoAcid waitForAminoAcid(Codon codon) throws InterruptedException {
        if (aminoAcidList.size() < geneSize) {
            foodListLock.lockInterruptibly();
            try {
                // see comment in waitForNucleotide()
                needMoreFood.signalAll();
            } finally {
                foodListLock.unlock();
            }
        }
        AminoAcid aminoAcid;
        aminoAcidListLock.lockInterruptibly();
        try {
            while ((aminoAcid = getAminoAcidByCodon(codon)) == null) {
                logId("waiting for amino acid for codon " + codon);
                aminoAcidAvailable.await();
            }
            return aminoAcid;
        } finally {
            aminoAcidListLock.unlock();
        }
	}
    public Food waitForFood() throws InterruptedException {
        foodListLock.lockInterruptibly();
        try {
            while (foodList.size() == 0) {
                logId("waiting for food ");
                foodAvailable.await();
            }
            return foodList.remove(0);
        } finally {
            foodListLock.unlock();
        }
    }
    public boolean cellReadyToDivide() {
        return proteinList.size() >= (geneSize * 2);
    }

    /*
    Not thread-safe, so only call if rnaList is locked
     */
	private RNA getRNA() {
		int size = rnaList.size();
		if (size == 0) return null;
		RNA rna = rnaList.remove(0);
		return rna;
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
	/*
	 * Find aminoAcid with specified name; if found, remove from list.
	 * Not thread-safe, so only call if aminoAcidList locked
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
	 * Find nucleotide suitable for bonding with passed nucleotide;
	 * if found, remove from list.
	 * Not thread-safe, so only call if nucleotideList is locked
	 */
	private Nucleotide getNucleotide(Nucleotide nucleotide, boolean dna) {
		for (int i = nucleotideList.size() - 1; i >= 0; i--) {
			Nucleotide freeNucleotide = nucleotideList.get(i);
            if (dna && freeNucleotide.dnaMatch(nucleotide) ||
                    freeNucleotide.rnaMatch((nucleotide))) {
                nucleotideList.remove(freeNucleotide);
                --nucleotideActuals[freeNucleotide.getIndex()];
                return freeNucleotide;
            }
		}
		logId("no bonding nucleotide found for " + nucleotide);
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
    public List<RNA> getRNAList() {
        return rnaList;
    }
    public DNA getDNA() {
        return dna;
    }
    public void setGeneration(int generation) {
        this.generation = generation;
    }
    public boolean isDivideCellRunning() {
        return this.divideCellRunning;
    }
    public void setDivideCellRunning(boolean divideCellRunning) {
        this.divideCellRunning = divideCellRunning;
    }
    public Lock getProteinListLock() {
        return proteinListLock;
    }
    public Condition getCellReadyToDivide() {
        return cellReadyToDivide;
    }
    public Condition getNeedMoreProteins() {
        return needMoreProteins;
    }
    @Override
    public String getName() {
        return "Cell";
    }
    public int getDnaIndex() {
        return dnaIndex;
    }
    public Lock getDnaIndexLock() {
        return dnaIndexLock;
    }
    public void setDnaIndex(int dnaIndex) {
        this.dnaIndex = dnaIndex;
    }
    public Lock getRnaListLock() {
        return rnaListLock;
    }
    public Condition getNeedMoreRNA() {
        return needMoreRNA;
    }
    public Condition getNeedMoreFood() {
        return needMoreFood;
    }
    public Lock getFoodListLock() {
        return foodListLock;
    }
    public int getGeneSize() {
        return geneSize;
    }

    /**
     * See if any of the cell's resources - nucleotides & aminoAcids -
     * are below their target levels.
     * @return true if any individual nucleotide or aminoAcid is
     * below its target level.
     */
    public boolean needMoreResources() {
        if (aminoAcidList.size() < geneSize) return true;
        for (int i = 0; i < nucleotideTargets.length; i++) {
            if (nucleotideActuals[i] < nucleotideTargets[i]) return true;
        }
        return false;
    }
    public boolean needMoreFood() {
        return foodList.size() == 0 && needMoreResources();
    }
}
