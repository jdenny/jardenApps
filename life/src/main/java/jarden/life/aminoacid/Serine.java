package jarden.life.aminoacid;

import java.util.List;

import jarden.life.CellResource;
import jarden.life.ChainResource;
import jarden.life.Protein;
import jarden.life.nucleicacid.Codon;
import jarden.life.nucleicacid.Cytosine;
import jarden.life.nucleicacid.Uracil;

/**
 * Created by john.denny@gmail.com on 18/03/2017.
 */

public class Serine extends AminoAcid {
    @Override
    public CellResource action(CellResource resource) throws InterruptedException {
        ChainResource chainResource = (ChainResource) resource;
        Protein protein = getProtein();
        CellResource node = null;
        List<AminoAcid> body = protein.getBody();
        while (chainResource.hasNext()) {
            node = chainResource.next();
            //?? if (node.isStop()) break;
            for (AminoAcid aminoAcid: body) {
                if (Thread.interrupted()) {
                    throw new InterruptedException(
                            "Thread.interrupted detected in Serine.action()");
                }
                node = aminoAcid.action(node);
            }
        }
        return node;
    }
    @Override
    public boolean matchCodon(Codon codon) {
        return codon.getFirst() instanceof Uracil &&
                codon.getSecond() instanceof Cytosine &&
                codon.getThird() instanceof Uracil;
    }
    @Override
    public String getName() {
        return "Serine";
    }
}
