package jarden.life.test;

import static jarden.life.nucleicacid.NucleicAcid.promoterCode;
import static jarden.life.nucleicacid.NucleicAcid.terminatorCode;
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import jarden.life.Cell;
import jarden.life.Protein;
import jarden.life.aminoacid.AddAminoAcidToProtein;
import jarden.life.aminoacid.GetAminoAcidFromCodon;
import jarden.life.aminoacid.GetCodonFromRNA;
import jarden.life.aminoacid.GetGeneFromDNA;
import jarden.life.aminoacid.GetRNAFromGene;
import jarden.life.nucleicacid.DNA;


/**
 * Created by john.denny@gmail.com on 08/02/2017.
 */

public class TestCell {
    private Cell syntheticCell;

    @Before
    public void setUp() throws Exception {
        // build cell with 2 hand-built proteins, which
        // between them can build proteins from DNA
        String dnaStr =
                promoterCode + "TTGTCT" + terminatorCode
                        + promoterCode + "TTATTCTTT" + terminatorCode;
        DNA dna = GetGeneFromDNA.buildDNAFromString(dnaStr);
        syntheticCell = new Cell(dna);
        Protein rnaPolymerase = new Protein();
        rnaPolymerase.add(new GetGeneFromDNA(syntheticCell));
        rnaPolymerase.add(new GetRNAFromGene(syntheticCell));
        Protein ribosome = new Protein();
        ribosome.add(new GetCodonFromRNA(syntheticCell));
        ribosome.add(new GetAminoAcidFromCodon(syntheticCell));
        ribosome.add(new AddAminoAcidToProtein(syntheticCell));
        syntheticCell.addProtein(rnaPolymerase);
        syntheticCell.addProtein(ribosome);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void splitShouldCreateIdenticalCopy() {
        Cell daughterCell = syntheticCell.split();
        assertEquals(syntheticCell, daughterCell);
        Cell grandDaughterCell = daughterCell.split();
        assertEquals(syntheticCell, grandDaughterCell);
    }
}
