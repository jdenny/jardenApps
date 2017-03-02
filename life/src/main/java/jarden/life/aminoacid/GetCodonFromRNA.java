package jarden.life.aminoacid;

import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import jarden.life.Cell;
import jarden.life.Protein;
import jarden.life.nucleicacid.Adenine;
import jarden.life.nucleicacid.Codon;
import jarden.life.nucleicacid.RNA;
import jarden.life.nucleicacid.Uracil;

/*
 * Get some RNA from cell; for each action, get next codon from rna.
 */
public class GetCodonFromRNA extends AminoAcid {
	private RNA rna;
	private int index;
	
    @Override
	public Codon action(Object object) throws InterruptedException {
        Cell cell = getCell();
        List<Protein> proteinList = cell.getProteinList();
        Lock proteinListLock = cell.getProteinListLock();
        Condition needMoreProteins = cell.getNeedMoreProteins();
        int geneSize = cell.getGeneSize();
        // get the RNA first, as if successful, we will release the lock on
        // rnaList and then put a lock on proteinList; otherwise we
        // would be increasing the risk of deadlock.
        if (rna == null || index >= rna.size()) {
            rna = getCell().waitForRNA();
            index = 0;
        }
        try {
            proteinListLock.lockInterruptibly();
            while (true) {
                int proteinSize = proteinList.size();
                if (proteinSize < (geneSize * 2)) {
                    // we can make more proteins
                    Codon codon = rna.get(index++);
                    if (codon.isStop()) {
                        rna = null;
                    }
                    return codon; // even if it is a stop()!
                } else {
                    cell.logId("waiting for needMoreProteins");
                    needMoreProteins.await();
                }
            }
        } finally {
            proteinListLock.unlock();
        }
	}
    @Override
	public String getName() {
		return "GetCodonFromRNA";
	}
    @Override
	public boolean matchCodon(Codon codon) {
        return codon.getFirst() instanceof Uracil &&
                codon.getSecond() instanceof Uracil &&
                codon.getThird() instanceof Adenine;
	}
    @Override
    public boolean hasMore() {
        return rna != null && index < rna.size();
    }
    @Override
    public void reset() {
        this.rna = null;
    }
}
