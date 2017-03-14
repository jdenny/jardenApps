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
import java.util.concurrent.Future;
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
    private static boolean verbose = true;
    private static Cell syntheticCell;
    private static int currentId = 0;
    private static String[] geneStrs = {
            "TGG",       // polymerase: Polymerase; was: TTGTCT - FindNextGene, GetRNAFromGene
            "TAT",       // ribosome: Ribosome; was TTATTCTTT - GetCodonFromRNA, GetAminoAcidFromCodon, AddAminoAcidToProtein
            "TGC",       // eatFood: EatFood
            "TCA",       // digest: DigestFood
            "TCGTGTTAC"  // divide: WaitForEnoughProteins, CopyDNA, DivideCell
    };
    private final Lock aminoAcidListLock = new ReentrantLock();
    private final Condition aminoAcidAvailableCondition = aminoAcidListLock.newCondition();

    private final Lock foodListLock = new ReentrantLock();
    private final Condition foodAvailableCondition = foodListLock.newCondition();
    private final Condition needMoreFoodCondition = foodListLock.newCondition();

    private final Lock nucleotideListLock = new ReentrantLock();
    private final Condition nucleotideAvailableCondition = nucleotideListLock.newCondition();

    private final Lock proteinListLock = new ReentrantLock();

    private Lock regulatorListLock = new ReentrantLock();
    private final Condition cellReadyToDivideCondition = regulatorListLock.newCondition();
    private final Condition rnaBelowTargetCondition = regulatorListLock.newCondition();

    private final Lock rnaListLock = new ReentrantLock();
    private final Condition rnaAvailableCondition = rnaListLock.newCondition();

    private final CellEnvironment cellEnvironment;
    private int id;
    private int generation = 1;
    private DNA dna;
    private final List<Regulator> regulatorList = new ArrayList<>();
    private final List<Protein> proteinList = new LinkedList<>();
	private final List<AminoAcid> aminoAcidList = new LinkedList<>();
	private final List<Nucleotide> nucleotideList = new LinkedList<>();
	private final List<RNA> rnaList = new LinkedList<>();
    private final List<Food> foodList = new LinkedList<>();
    private int hashCode = 0;
    private boolean divideCellRunning;
    private int geneSize = 5;
    // TODO: when proper aminoAcids, replace length with 20
    // in same sequence as aminoAcid list
    private int[] aminoAcidTargets = new int[CellData.aminoAcidNames.length];
    // indexed by nucleotide.getIndex(); 5 is number of nucleotide types
    private int[] nucleotideTargets = new int[5];
    private int[] nucleotideActuals = new int[5];
    /*
    Current values of nucleotideTargets:
        0 55
        1 20
        2 20
        3 37
        4 24
         ---
         156
    Common thread numbers:
        Cell 1                Cell 2
        5  polymerase
        6  ribsome
        7  eatFood
        8  digestFood
        9  divideCell

        10 polymerase     ->  14 or 15
        11 ribsome        ->  15    16
        12 eatFood        ->  16    17
        13 digestFood     ->  17    18
        -  divideCell     ->  18    19
        (thread 14 used by Timer)

	Current implementation of codonTable.
	See Nucleotide for real-life codonTable.
		                    	UUU
		                    	UUC
		            			UUA
		             			UUG
		             			UCU
		            			UCC
		Stop					UAA
		Start                   UGA
		WaitForEnoughProteins   UCG
		CopyDNA                 UGU
		DivideCell              UAC
		DigestFood              UCA
		EatFood                 UGC
		Polymerase              UGG
		Ribosome                UAU
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
        Cell synCell = new Cell(buildDNAFromString(getDnaStr()), cellEnvironment);
        // create resources for 1 daughter cell of 5 proteins:
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
        int adenineFor2Cells = synCell.nucleotideTargets[0] * 2;
        for (int i = 0; i < adenineFor2Cells; i++) {
            newNucleotides.add(new Adenine());
        }
        int cytosineFor2Cells = synCell.nucleotideTargets[1];
        for (int i = 0; i < cytosineFor2Cells; i++) {
            newNucleotides.add(new Cytosine());
        }
        int guanineFor2Cells = synCell.nucleotideTargets[2];
        for (int i = 0; i < guanineFor2Cells; i++) {
            newNucleotides.add(new Guanine());
        }
        int thymineFor2Cells = synCell.nucleotideTargets[3];
        for (int i = 0; i < thymineFor2Cells; i++) {
            newNucleotides.add(new Thymine());
        }
        int uracilFor2Cells = synCell.nucleotideTargets[4];
        for (int i = 0; i < uracilFor2Cells; i++) {
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
        boolean startAllProteins = false;
        if (!startAllProteins) {
            ribosome.activate = false;
            proteinDigest.activate = false;
            eatFood.activate = false;
            proteinDivide.activate = false;
        }
        /*
        This is a bit flakey! The index values on the 'get' must match
        the order of the gene in the DNA - see geneStrs
         */
        rnaPolymerase.setRegulator(synCell.regulatorList.get(0));
        ribosome.setRegulator(synCell.regulatorList.get(1));
        eatFood.setRegulator(synCell.regulatorList.get(2));
        proteinDigest.setRegulator(synCell.regulatorList.get(3));
        proteinDivide.setRegulator(synCell.regulatorList.get(4));
        synCell.addProtein(rnaPolymerase);
        synCell.addProtein(ribosome);
        synCell.addProtein(eatFood);
        synCell.addProtein(proteinDigest);
        synCell.addProtein(proteinDivide);
        return synCell;
    }

    /**
     *
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
    // set targets & regulators. Note: no proteins running yet in this cell,
    // so no worries with threads or locks
    private void analyseDNA() {
        // new bits:
        String dnaStr = dna.dnaToString();
        int index = 0;
        while ((index = Polymerase.getNextStartIndex(dna, index)) >= 0) {
            index += 3; // move past start-codon
            regulatorList.add(new Regulator(index));
        }
        geneSize = regulatorList.size();
        if (geneSize == 0) {
            throw new IllegalStateException("DNA contains no start-gene");
        }
        // end of new bits
        //!! geneSize = geneStrs.length;
        for (int i = 0; i < dnaStr.length(); i++) {
            if (dnaStr.charAt(i) == 'T') {
                ++nucleotideTargets[3]; // for DNA strand1
                ++nucleotideTargets[0]; // for DNA strand2
                ++nucleotideTargets[4]; // for RNA
            }
            else if (dnaStr.charAt(i) == 'C') {
                nucleotideTargets[1] += 2; // 1 for DNA strand1, 1 for RNA
                ++nucleotideTargets[2]; // for DNA strand2
            }
            else if (dnaStr.charAt(i) == 'A') {
                nucleotideTargets[0] += 2; // 1 for DNA strand1, 1 for RNA
                ++nucleotideTargets[3]; // for DNA strand2
            }
            else if (dnaStr.charAt(i) == 'G') {
                nucleotideTargets[2] += 2; // 1 for DNA strand1, 1 for RNA
                ++nucleotideTargets[1]; // for DNA strand2
            }
        }
        // promoters part of DNA, but not part of RNA, but above loop has already added them
        // for RNA
        nucleotideTargets[0] -= (geneSize * startAdenineCt);
        nucleotideTargets[1] -= (geneSize * startCytosineCt);
        nucleotideTargets[2] -= (geneSize * startGuanineCt);
        nucleotideTargets[4] -= (geneSize * startThymineCt); // thymine becomes uracil in RNA
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
        cellData.rnaCt = rnaList.size();
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
            protein.getRegulator().incrementProteinCt();
            if (cellReadyToDivide()) {
                cellReadyToDivideCondition.signalAll();
            }
        } finally {
            proteinListLock.unlock();
        }
        logId("addProtein(); proteinCt=" + proteinList.size());
        Future future = protein.getFuture();
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
                ++nucleotideActuals[nucleotide.getIndex()];
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
            while ((regulator = getRegulatorBelowTarget()) == null) {
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
    private Regulator getRegulatorBelowTarget() {
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
            return getRegulatorBelowTarget() == null;
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
        int index = nucleotide.getIndex();
        if (nucleotideActuals[index] < nucleotideTargets[index]) {
            foodListLock.lockInterruptibly();
            try {
                // better than needMoreResources.signalAll(), as that
                // would only activate Digest, which can't start until
                // there is food available; perhaps the required nucleotide
                // is being digested this very moment, but it's better to
                // be overfed than underfed
                needMoreFoodCondition.signalAll();
            } finally {
                foodListLock.unlock();
            }
        }
        Nucleotide bondingNucleotide;
        nucleotideListLock.lockInterruptibly();
        try {
            while ((bondingNucleotide = getNucleotide(nucleotide, isForDna)) == null) {
                logId("waiting for " + (isForDna?"DNA":"RNA") +
                        " nucleotide to bond with " + nucleotide);
                nucleotideAvailableCondition.await();
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
                rnaAvailableCondition.await();
            }
            if (rnaList.size() < geneSize) rnaBelowTargetCondition.signalAll();
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
		for (int i = nucleotideList.size() - 1; i >= 0; i--) {
			Nucleotide freeNucleotide = nucleotideList.get(i);
            if (isForDna && freeNucleotide.dnaMatch(nucleotide) ||
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
    public Lock getProteinListLock() {
        return proteinListLock;
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
