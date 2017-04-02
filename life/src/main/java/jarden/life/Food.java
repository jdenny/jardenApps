package jarden.life;

import java.util.LinkedList;
import java.util.List;

import jarden.life.aminoacid.AminoAcid;
import jarden.life.nucleicacid.Nucleotide;

/**
 * Created by john.denny@gmail.com on 20/02/2017.
 */

public class Food implements ChainResource {
    public List<AminoAcid> aminoAcidList = new LinkedList<>();
    public List<Nucleotide> nucleotideList = new LinkedList<>();
    @Override
    public String getName() {
        return "Food";
    }
    @Override
    public boolean hasNext() {
        return aminoAcidList.size() > 0 || nucleotideList.size() > 0;
    }
    @Override
    public CellResource next() {
        if (aminoAcidList.size() > 0) return aminoAcidList.remove(0);
        else return nucleotideList.remove(0);
    }
    @Override
    public TargetResource getTargetResource() {
        return null;
    }
}

