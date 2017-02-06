package temp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by john.denny@gmail.com on 02/02/2017.
 */

public class Life3 implements ProcessListener {
    char[] rnaHack = {
            // lysine, arginine, stop:
            'a', 'a', 'a', 'a', 'g', 'g', 'u', 'g', 'a'
    };
    List<AminoAcid> aminoAcidPool = new ArrayList<>();
    AminoAcid johnGetAminoAcid = new JohnGetAminoAcid();
    AminoAcid johnGetNextCodon = new JohnGetNextCodon();

    public static void main(String[] args) {
        new Life3();
    }
    public Life3() {
        aminoAcidPool.add(new Lysine());
        aminoAcidPool.add(new Arginine());
        List<Nucleotide> firstRna = new ArrayList<>();
        for (char c : rnaHack) firstRna.add(new Nucleotide(c));
        boolean useRibosome = true;
        if (useRibosome) buildManualRibosome(firstRna);
        else simulateRibosome(firstRna);
    }
    private void buildManualRibosome(List<Nucleotide> firstRna) {
        Protein manualRibosome = new Protein();
        manualRibosome.addElement(johnGetNextCodon);
        manualRibosome.addElement(johnGetAminoAcid);
        Chain rnaChain = new Chain();
        rnaChain.elements = firstRna;
        Protein protein = new Protein();
        manualRibosome.processElement(protein, rnaChain,
                result -> {
                    System.out.println("result=" + result);
                    protein.processElement(null, null, this);
                });

        System.out.println("protein started");
    }
    /*
      A: get next codon(chain, index)
      if < 3 or stop: break
      link to matching tRNA
      get amino acid from tRNA
      add amino acid to protein
      repeat from A:
      start protein
     */
    private void simulateRibosome(List<Nucleotide> rna) {
        Protein protein = new Protein();
        if (rna.size() < 3) {
            System.out.println("rna.length:" + rna.size() +
                " < 3, so exiting");
            return;
        }
        Chain rnaChain = new Chain();
        rnaChain.elements = rna;
        Object codon;
        while ((codon = johnGetNextCodon.processElement(rnaChain)) != null) {
            AminoAcid aminoAcid = (AminoAcid) johnGetAminoAcid.processElement(codon);
            protein.addElement(aminoAcid);
        }
        new Thread(protein).start();
    }

    @Override
    public void onProcessComplete(Object result) {
        System.out.println("2nd result = " + result);
    }
}

class Nucleotide {
    char code;

    public Nucleotide(char code) {
        this.code = code;
    }
}

class Chain {
    List elements;
    int index;
    boolean atEnd = false;
    public void addElement(Object element) {
        this.elements.add(element);
    }
}

class Codon {
    char a;
    char b;
    char c;
}


interface AminoAcid {
    Object processElement(Object element);
}

class JohnGetNextCodon implements AminoAcid {
    @Override
    public Object processElement(Object element) {
        Chain rnaChain = (Chain) element;
        if (rnaChain.index + 3 >= rnaChain.elements.size()) {
            rnaChain.atEnd = true;
            return null;
        }
        Codon codon = new Codon();
        codon.a = ((Nucleotide) rnaChain.elements.get(rnaChain.index++)).code;
        codon.b = ((Nucleotide) rnaChain.elements.get(rnaChain.index++)).code;
        codon.c = ((Nucleotide) rnaChain.elements.get(rnaChain.index++)).code;
        if (codon.a == 'u' && codon.b == 'g' && codon.c == 'a') {
            rnaChain.atEnd = true;
            return null;
        }
        return codon;
    }
}

class JohnGetAminoAcid implements AminoAcid {
    @Override
    public Object processElement(Object element) {
        Codon codon = (Codon) element;
        if (codon.a == 'a' && codon.b == 'a' && codon.c == 'a') {
            return new Lysine();
        }
        if (codon.a == 'a' && codon.b == 'g' && codon.c == 'g') {
            return new Arginine();
        }
        return null;
    }
}

class Lysine implements AminoAcid {
    @Override
    public Object processElement(Object element) {
        System.out.println("Lysine here");
        return this;
    }
}

class Arginine implements AminoAcid {
    @Override
    public Object processElement(Object element) {
        System.out.println("Arginine here");
        return this;
    }
}

interface ProcessListener {
    void onProcessComplete(Object result);
}

class Protein extends Chain implements Runnable {
    private Object target;
    private Object element;
    private ProcessListener processListener;

    public Protein() {
        elements = new ArrayList<AminoAcid>();
    }
    public void processElement(Object target, Object element,
                               ProcessListener processListener) {
        this.target = target;
        this.element = element;
        this.processListener = processListener;
        new Thread(this).start();
    }

    @Override
    public void run() {
        Object currentElement;
        List<AminoAcid> aminoAcids = elements;
        do {
            currentElement = element;
            for (AminoAcid aminoAcid: aminoAcids) {
                currentElement = aminoAcid.processElement(currentElement);
                if (currentElement == null) break;
            }
            if (currentElement == null) break;
            if (target != null && currentElement != null && target instanceof Chain) {
                ((Chain)target).addElement(currentElement);
            }
        } while (element instanceof Chain && !((Chain)element).atEnd);
        processListener.onProcessComplete(currentElement);
    }
}
