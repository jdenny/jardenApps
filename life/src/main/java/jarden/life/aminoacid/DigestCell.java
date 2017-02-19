package jarden.life.aminoacid;

import jarden.life.Cell;
import jarden.life.MasterDesigner;
import jarden.life.nucleicacid.Adenine;
import jarden.life.nucleicacid.Codon;
import jarden.life.nucleicacid.Cytosine;
import jarden.life.nucleicacid.Uracil;

/**
 * Created by john.denny@gmail.com on 13/02/2017.
 */

public class DigestCell extends AminoAcid {
    public DigestCell(Cell cell) {
        super(cell);
    }
    public Object action(Object object) {
        System.out.println("DigestCell.action(" + object + "); does nothing, yet!");
        synchronized (this) { // temporary bodge
            try { wait(); }
            catch(InterruptedException e) {
                MasterDesigner.print(Thread.currentThread().getName() +
                        " DigestCell interrupted while doing nothing");
                Thread.currentThread().interrupt();
                return null;
            }
            return null;
        }
    }
    public String getName() {
        return "DigestCell";
    }
    public boolean matchCodon(Codon codon) {
        return codon.getFirst() instanceof Uracil &&
                codon.getSecond() instanceof Cytosine &&
                codon.getThird() instanceof Adenine;
    }
}
