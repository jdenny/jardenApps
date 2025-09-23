package chemistry;

/**
 * Created by john.denny@gmail.com on 20/09/2025.
 * electron shells: number, letter, maximum in shell (2 * n * n)
 * (1 k 2), (2 l 8), (3 m 18), (4 n 32)
 */
public class Atom {
    private int protons;
    private int neutrons;
    private int electrons;
    private int electronsInOuterShell;
    private int electronsOuterShortage;
    private Atom bonded;
    public Atom(int prot, int neut, int elec) {
        this.protons = prot;
        this.neutrons = neut;
        this.electrons = elec;
        calculateOuterShell();
    }
    public int getElectronsInOuterShell() {
        return this.electronsInOuterShell;
    }
    public int getElectronsOuterShortage() {
        return this.electronsOuterShortage;
    }
    public boolean doesIonicBond (Atom that) {
        return ((electronsInOuterShell == 1 && that.electronsOuterShortage == 1) ||
                (electronsOuterShortage == 1 && that.electronsInOuterShell == 1));
    }

    private void calculateOuterShell() {
        // return (electrons <= 2) ? electrons : ((electrons <= 10) ? (electrons - 2) : (electrons - 10));
        if (electrons > 86) {
            electronsInOuterShell = electrons - 86;
            electronsOuterShortage = 118 - electrons;
        } else if (electrons > 54) {
            electronsInOuterShell = electrons - 54;
            electronsOuterShortage = 86 - electrons;
        } else if (electrons > 36) {
            electronsInOuterShell = electrons - 36;
            electronsOuterShortage = 54 - electrons;
        } else if (electrons > 18) {
            electronsInOuterShell = electrons - 18;
            electronsOuterShortage = 36 - electrons;
        } else if (electrons > 10) {
            electronsInOuterShell = electrons - 10;
            electronsOuterShortage = 18 - electrons;
        } else if (electrons > 2) {
            electronsInOuterShell = electrons - 2;
            electronsOuterShortage = 10 - electrons;
        } else {
            electronsInOuterShell = electrons;
            electronsOuterShortage = 2 - electrons;
        }
    }
    /*
    public boolean isMetal() {

    }

     */
    public static void main(String[] args) {

    }
}
