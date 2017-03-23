package jarden.life;

import jarden.life.aminoacid.Alanine;
import jarden.life.aminoacid.AminoAcid;
import jarden.life.aminoacid.Arginine;
import jarden.life.aminoacid.Asparagine;
import jarden.life.aminoacid.AsparticAcid;
import jarden.life.aminoacid.CopyDNA;
import jarden.life.aminoacid.Cysteine;
import jarden.life.aminoacid.DigestFood;
import jarden.life.aminoacid.DivideCell;
import jarden.life.aminoacid.EatFood;
import jarden.life.aminoacid.GlutamicAcid;
import jarden.life.aminoacid.Glutamine;
import jarden.life.aminoacid.Glycine;
import jarden.life.aminoacid.Histidine;
import jarden.life.aminoacid.Isoleucine;
import jarden.life.aminoacid.Leucine;
import jarden.life.aminoacid.Lysine;
import jarden.life.aminoacid.Methionine;
import jarden.life.aminoacid.Phenylalanine;
import jarden.life.aminoacid.Polymerase;
import jarden.life.aminoacid.Proline;
import jarden.life.aminoacid.Serine;
import jarden.life.aminoacid.Threonine;
import jarden.life.aminoacid.Tryptophan;
import jarden.life.aminoacid.Tyrosine;
import jarden.life.aminoacid.Valine;
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
import static jarden.life.nucleicacid.Nucleotide.startAdenineCt;
import static jarden.life.nucleicacid.Nucleotide.startCode;
import static jarden.life.nucleicacid.Nucleotide.startCytosineCt;
import static jarden.life.nucleicacid.Nucleotide.startGuanineCt;
import static jarden.life.nucleicacid.Nucleotide.startThymineCt;
import static jarden.life.nucleicacid.Nucleotide.stopCode;

public class Cell implements Food {
    private final CellEnvironment cellEnvironment;
    private boolean divideCellRunning;
    private DNA dna;
    private int generation = 1;
    private int geneSize;
    private int hashCode = 0;
    private int id;
    private final List<AminoAcid> aminoAcidList = new LinkedList<>();
    private final List<Food> foodList = new LinkedList<>();
    private final List<Nucleotide> nucleotideList = new LinkedList<>();
    private final List<Protein> proteinList = new LinkedList<>();
    private final List<Regulator> regulatorList = new ArrayList<>();
    private final List<RNA> rnaList = new LinkedList<>();
    // TODO: when proper aminoAcids, replace length with 20
    // in same sequence as CellData.aminoAcidNames
    public static int[] aminoAcidFeedCounts = new int[CellData.aminoAcidNames.length];
    // indexed by nucleotide.getIndex(); 5 is number of nucleotide types
    public static int[] nucleotideFeedCounts = new int[5];

    private static boolean verbose = true;
    private static int currentId = 0;
    private static Cell syntheticCell;
    // used for building synthetic cell:
    private static String[] geneStrs = {
            "GATCCTTGTTGGTTC", // polymerase: AsparticAcid (data), Proline (regulator),
                // Cysteine (code), Tryptophan (awaitResource), Polymerase
            // ribosome:
            "TTA" +         // UUA, Leucine, turn on body mode
                    "TGG" + // UGG, Tryptophan, wait for resource
                    "GAT" + // GAU, AsparticAcid, turn on data mode
                    "CGT" + // CGU, Arginine, data: RNA
                    "TGT" + // UGU, Cysteine, turn on code mode
                    "TGG" + // UGG, Tryptophan, wait for resource
                    "TCT" + // UCU, Serine, loop
                    "GAA",  // GAA, GlutamicAcid, addAminoAcid resource to cell
            "TGC",       // eatFood: EatFood
            "GATTTTTGTTGGTCA", // digest: AsparticAcid (data), Phenylalanine (food),
                // Cysteine (code), Tryptophan (awaitResource), DigestFood
            "TCGTTGTAC"  // divide: WaitForEnoughProteins, CopyDNA, DivideCell
    };
    private final ReentrantLock aminoAcidListLock = new ReentrantLock();
    private final Condition aminoAcidAvailableCondition = aminoAcidListLock.newCondition();

    private final Lock foodListLock = new ReentrantLock();
    private final Condition foodAvailableCondition = foodListLock.newCondition();
    private final Condition needMoreFoodCondition = foodListLock.newCondition();

    private final ReentrantLock nucleotideListLock = new ReentrantLock();
    private final Condition nucleotideAvailableCondition = nucleotideListLock.newCondition();

    private Lock regulatorListLock = new ReentrantLock();
    private final Condition cellReadyToDivideCondition = regulatorListLock.newCondition();
    private final Condition rnaBelowTargetCondition = regulatorListLock.newCondition();

    private final Lock rnaListLock = new ReentrantLock();
    private final Condition rnaAvailableCondition = rnaListLock.newCondition();
    private boolean isNeedMoreFood = false;

    /*
    Current values of nucleotideTargets:
        0 50
        1 18
        2 19
        3 37
        4 14
         ---
         156
    Common pool-thread numbers:
        Cell 1                Cell 2
        1  polymerase
        2  ribsome
        3  eatFood
        4  digestFood
        5  divideCell

        6  polymerase     ->  12
        7  ribsome        ->  15    16
        8  eatFood        ->  16    17
        9  digestFood     ->  17    18
        -  divideCell     ->  18    19
        (thread 14 used by Timer)

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
     * @return synthetic cell
     * @throws InterruptedException
     */
    public static Cell makeSyntheticCell(CellEnvironment cellEnvironment) throws InterruptedException {
        String dnaStr = getDnaStr();
        for (int i = 0; i < dnaStr.length(); i++) {
            if (dnaStr.charAt(i) == 'T') {
                ++nucleotideFeedCounts[3]; // for DNA strand1
                ++nucleotideFeedCounts[0]; // for DNA strand2
                ++nucleotideFeedCounts[4]; // for RNA
            }
            else if (dnaStr.charAt(i) == 'C') {
                nucleotideFeedCounts[1] += 2; // 1 for DNA strand1, 1 for RNA
                ++nucleotideFeedCounts[2]; // for DNA strand2
            }
            else if (dnaStr.charAt(i) == 'A') {
                nucleotideFeedCounts[0] += 2; // 1 for DNA strand1, 1 for RNA
                ++nucleotideFeedCounts[3]; // for DNA strand2
            }
            else if (dnaStr.charAt(i) == 'G') {
                nucleotideFeedCounts[2] += 2; // 1 for DNA strand1, 1 for RNA
                ++nucleotideFeedCounts[1]; // for DNA strand2
            }
        }
        // start-codon part of DNA, but not part of RNA, but above loop has already
        // added them for RNA, so make suitable adjustments:
        int geneSize = geneStrs.length;
        nucleotideFeedCounts[0] -= (geneSize * startAdenineCt);
        nucleotideFeedCounts[1] -= (geneSize * startCytosineCt);
        nucleotideFeedCounts[2] -= (geneSize * startGuanineCt);
        nucleotideFeedCounts[4] -= (geneSize * startThymineCt); // thymine becomes uracil in RNA
        Cell synCell = new Cell(buildDNAFromString(dnaStr), cellEnvironment);
        // create resources for 1 daughter cell:
        for (int i = 0; i < 2; i++) {
            CellFood.addNucleotides(synCell.nucleotideList);
        }
        Protein protein;
        // so we can test a cell without all the proteins pulling the rug from
        // beneath our feet!
        boolean startAllProteins = true;
        boolean[] startProteins = {
                true, // rnaPolymerase
                true, // genericRibosome
                false, // proteinDigest
                false, // eatFood
                false  // proteinDivide
        };
        for (int i = 0; i < geneStrs.length; i++) {
            String geneStr = geneStrs[i];
            protein = new Protein(synCell);
            for (int j = 0; (j+3) <= geneStr.length(); j+=3) {
                String codonStr = geneStr.substring(j, j+3);
                protein.addAminoAcid(makeAminoAcid(codonStr));
                AminoAcid aminoAcid = makeAminoAcid(codonStr);
                synCell.aminoAcidList.add(aminoAcid);
                ++aminoAcidFeedCounts[aminoAcid.getIndex()];
            }
            protein.setRegulator(synCell.regulatorList.get(i));
            if (!startAllProteins && !startProteins[i]) {
                protein.activate = false;
            }
            synCell.addProtein(protein);
        }
        // we're adding an extra Alanine; this can be used to show
        // how many times a cell has eaten & digested
        ++aminoAcidFeedCounts[0];
        return synCell;
    }
    /*
    Used by makeSyntheticCell to build proteins according to the DNA
     */
    private static AminoAcid makeAminoAcid(String codonStr) {
        if      (codonStr.equals("GCT")) return new Alanine();
        else if (codonStr.equals("CGT")) return new Arginine();
        else if (codonStr.equals("AAT")) return new Asparagine();
        else if (codonStr.equals("GAT")) return new AsparticAcid();
        else if (codonStr.equals("TTG")) return new CopyDNA();
        else if (codonStr.equals("TGT")) return new Cysteine();
        else if (codonStr.equals("TCA")) return new DigestFood();
        else if (codonStr.equals("TAC")) return new DivideCell();
        else if (codonStr.equals("TGC")) return new EatFood();
        else if (codonStr.equals("GAA")) return new GlutamicAcid();
        else if (codonStr.equals("CAA")) return new Glutamine();
        else if (codonStr.equals("GGT")) return new Glycine();
        else if (codonStr.equals("CAT")) return new Histidine();
        else if (codonStr.equals("ATT")) return new Isoleucine();
        else if (codonStr.equals("TTA")) return new Leucine();
        else if (codonStr.equals("AAA")) return new Lysine();
        else if (codonStr.equals("ATG")) return new Methionine();
        else if (codonStr.equals("TTT")) return new Phenylalanine();
        else if (codonStr.equals("TTC")) return new Polymerase();
        else if (codonStr.equals("CCT")) return new Proline();
        else if (codonStr.equals("TCT")) return new Serine();
        else if (codonStr.equals("ACT")) return new Threonine();
        else if (codonStr.equals("TGG")) return new Tryptophan();
        else if (codonStr.equals("TAT")) return new Tyrosine();
        else if (codonStr.equals("GTT")) return new Valine();
        else if (codonStr.equals("TCG")) return new WaitForEnoughProteins();
        else throw new IllegalArgumentException("unrecognised codonStr: " +
                codonStr);
    }

    /**
     * Construct new Cell, from DNA. Proteins are added separately.
     * @param dna DeoxyriboNucleic Acid
     * @param cellEnvironment where the cell gets its food from
     */
    public Cell(DNA dna, CellEnvironment cellEnvironment) {
        this.id = ++currentId;
        this.dna = dna;
        this.cellEnvironment = cellEnvironment;
        analyseDNA();
    }
    private static String getDnaStr() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String geneStr: geneStrs) {
            stringBuilder.append(startCode); // part of DNA, but not part of RNA
            stringBuilder.append(geneStr);
            stringBuilder.append(stopCode); // part of RNA
        }
        return stringBuilder.toString();
    }
    // set regulators. Note: no proteins running yet in this cell,
    // so no worries with threads or locks
    private void analyseDNA() {
        int index = 0;
        while ((index = Polymerase.getNextStartIndex(dna, index)) >= 0) {
            index += 3; // move past start-codon
            regulatorList.add(new Regulator(index, regulatorList.size()));
        }
        geneSize = regulatorList.size();
        if (geneSize == 0) {
            throw new IllegalStateException("DNA contains no start-gene");
        }
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
     * Get UI data for this cell.
     * @return UI data for this cell.
     */
    public CellData getCellData() {
        CellData cellData = new CellData();
        cellData.cellId = this.id;
        cellData.rnaCt = rnaList.size();
        // Proteins:
        HashMap<String, Integer> proteinNameCountMap = new HashMap<>();
        regulatorListLock.lock();
        try {
            for (Protein protein : proteinList) {
                String name = protein.toString();
                Integer ct = proteinNameCountMap.get(name);
                ct = (ct == null) ? 1 : (ct + 1);
                proteinNameCountMap.put(name, ct);
            }
        } finally {
            regulatorListLock.unlock();
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
        regulatorListLock.lockInterruptibly();
        try {
            proteinList.add(protein);
            logId("addProtein(); proteinCt=" + proteinList.size());
            protein.getRegulator().incrementProteinCt();
            if (cellReadyToDivide()) {
                cellReadyToDivideCondition.signalAll();
            }
        } finally {
            regulatorListLock.unlock();
        }
        if (protein.activate) {
            protein.start(cellEnvironment.getThreadPoolExecutor());
        }
	}
	public void addAminoAcids(List<AminoAcid> aminoAcids) throws InterruptedException {
        aminoAcidListLock.lockInterruptibly();
        try {
            aminoAcidList.addAll(aminoAcids);
            aminoAcidAvailableCondition.signalAll();
        } finally {
            aminoAcidListLock.unlock();
        }
	}
	public void addNucleotides(List<Nucleotide> nucleotides) throws InterruptedException {
        nucleotideListLock.lockInterruptibly();
        try {
            for (Nucleotide nucleotide: nucleotides) {
                nucleotideList.add(nucleotide);
            }
            nucleotideAvailableCondition.signalAll();
        } finally {
            nucleotideListLock.unlock();
        }
	}
	public void addRNA(RNA rna) throws InterruptedException {
        rnaListLock.lockInterruptibly();
        try {
            rnaList.add(rna);
            rnaAvailableCondition.signalAll();
        } finally {
            rnaListLock.unlock();
        }
	}
    public void addFood(Food food) throws InterruptedException {
        foodListLock.lockInterruptibly();
        try {
            foodList.add(food);
            isNeedMoreFood = false;
            foodAvailableCondition.signalAll();
        } finally {
            foodListLock.unlock();
        }
    }

    /**
     * Get Regulator for gene that needs to be used to
     * build a protein, i.e. not enough proteins for this
     * gene have been built or are currently being built.
     * @return regulator for gene to be used to build a protein
     * @throws InterruptedException
     */
    public Regulator waitForRnaBelowTarget() throws InterruptedException {
        Regulator regulator;
        regulatorListLock.lockInterruptibly();
        try {
            while ((regulator = getRegulatorOfRnaBelowTarget()) == null) {
                logId("waiting for rnaBelowTargetCondition");
                rnaBelowTargetCondition.await();
            }
            regulator.incrementRnaCt();
            return regulator;
        } finally {
            regulatorListLock.unlock();
        }
    }
    // TODO: maintain an index into the regulatorList itself,
    // to save always starting from the beginning - maybe?
    /**
     * Return regulator of gene that should be used to create
     * a protein, or null if all genes have produced the target
     * number of proteins. Not thread safe, so call with
     * regulatorList locked.
     */
    private Regulator getRegulatorOfRnaBelowTarget() {
        for (Regulator regulator: regulatorList) {
            if (regulator.rnaBelowTarget()) {
                return regulator;
            }
        }
        return null;
    }
    public boolean cellReadyToDivide() throws InterruptedException {
        regulatorListLock.lockInterruptibly();
        try {
            for (Regulator regulator: regulatorList) {
                if (regulator.proteinsBelowTarget()) {
                    return false;
                }
            }
            return true;
        } finally {
            regulatorListLock.unlock();
        }
    }
    /**
     * Wait for a nucleotide suitable to make a base-pair with supplied
     * nucleotide.
     * @param nucleotide
     * @param isForDna if true, look for dna base-pair, else rna base-pair.
     * @return nucleotide that can form a base pair with supplied
     * nucleotide.
     * @throws InterruptedException
     */
    public Nucleotide waitForNucleotide(Nucleotide nucleotide, boolean isForDna)
            throws InterruptedException {
        Nucleotide bondingNucleotide;
        nucleotideListLock.lockInterruptibly();
        try {
            while ((bondingNucleotide = getNucleotide(nucleotide, isForDna)) == null) {
                nucleotideListLock.unlock();
                foodListLock.lockInterruptibly();
                try {
                    isNeedMoreFood = true;
                    needMoreFoodCondition.signalAll();
                } finally {
                    foodListLock.unlock();
                }
                nucleotideListLock.lockInterruptibly();
                logId("waiting for " + (isForDna?"DNA":"RNA") +
                        " nucleotide to bond with " + nucleotide);
                nucleotideAvailableCondition.await();
            }
            return bondingNucleotide;
        } finally {
            while (nucleotideListLock.isHeldByCurrentThread()) {
                nucleotideListLock.unlock();
            }
        }
    }
	public RNA waitForRNA() throws InterruptedException {
        RNA rna;
        rnaListLock.lockInterruptibly();
        try {
            while ((rna = getRNA()) == null) {
                logId("waiting for some RNA");
                rnaAvailableCondition.await();
            }
            return rna;
        } finally {
            rnaListLock.unlock();
        }
	}
    public AminoAcid waitForAminoAcid(Codon codon) throws InterruptedException {
        AminoAcid aminoAcid;
        aminoAcidListLock.lockInterruptibly();
        try {
            while ((aminoAcid = getAminoAcidByCodon(codon)) == null) {
                aminoAcidListLock.unlock();
                foodListLock.lockInterruptibly();
                try {
                    isNeedMoreFood = true;
                    needMoreFoodCondition.signalAll();
                } finally {
                    foodListLock.unlock();
                }
                aminoAcidListLock.lockInterruptibly();
                logId("waiting for amino acid for codon " + codon);
                aminoAcidAvailableCondition.await();
            }
            return aminoAcid;
        } finally {
            while (aminoAcidListLock.isHeldByCurrentThread()) {
                aminoAcidListLock.unlock();
            }
        }
    }
	public AminoAcid waitForAminoAcidOld(Codon codon) throws InterruptedException {
        if (aminoAcidList.size() < geneSize) {
            foodListLock.lockInterruptibly();
            try {
                // see comment in waitForNucleotide()
                needMoreFoodCondition.signalAll();
            } finally {
                foodListLock.unlock();
            }
        }
        AminoAcid aminoAcid;
        aminoAcidListLock.lockInterruptibly();
        try {
            while ((aminoAcid = getAminoAcidByCodon(codon)) == null) {
                logId("waiting for amino acid for codon " + codon);
                aminoAcidAvailableCondition.await();
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
                foodAvailableCondition.await();
            }
            return foodList.remove(0);
        } finally {
            foodListLock.unlock();
        }
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
		logId("no amino acid found for " + codon);
		return null;
	}
	/*
	 * Find nucleotide suitable for bonding with passed nucleotide;
	 * if found, remove from list.
	 * Not thread-safe, so only call if nucleotideList is locked
	 */
	private Nucleotide getNucleotide(Nucleotide nucleotide, boolean isForDna) {
        for (Nucleotide freeNucleotide: nucleotideList) {
            if (isForDna && freeNucleotide.dnaMatch(nucleotide) ||
                    (!isForDna) && freeNucleotide.rnaMatch((nucleotide))) {
                nucleotideList.remove(freeNucleotide);
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
                    dna.add(new Adenine(), new Thymine());
                    break;
                case 'T':
                    dna.add(new Thymine(), new Adenine());
                    break;
                case 'C':
                    dna.add(new Cytosine(), new Guanine());
                    break;
                case 'G':
                    dna.add(new Guanine(), new Cytosine());
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
    public Condition getCellReadyToDivideCondition() {
        return cellReadyToDivideCondition;
    }
    @Override
    public String getName() {
        return "Cell";
    }
    public Lock getRnaListLock() {
        return rnaListLock;
    }
    public Condition getRnaBelowTargetCondition() {
        return rnaBelowTargetCondition;
    }
    public Condition getNeedMoreFoodCondition() {
        return needMoreFoodCondition;
    }
    public Lock getFoodListLock() {
        return foodListLock;
    }
    public int getGeneSize() {
        return geneSize;
    }

    public boolean needMoreFood() {
        return foodList.size() == 0 && isNeedMoreFood;
    }

    public CellShortData getCellShortData() {
        return new CellShortData(id, generation, proteinList.size());
    }
    public List<Regulator> getRegulatorList() {
        return regulatorList;
    }
    public Lock getRegulatorListLock() {
        return regulatorListLock;
    }
}
