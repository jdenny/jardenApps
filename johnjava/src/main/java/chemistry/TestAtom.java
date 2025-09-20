package chemistry;

/**
 * Created by john.denny@gmail.com on 20/09/2025.
 */
public class TestAtom {
    public static void main(String[] args) {
        testOuterShell();
        testBonding();
    }
    private static void testOuterShell() {
        int[] outers = {
                1, 2,
                1, 2, 3, 4, 5, 6, 7, 8,
                1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18,
                1, 2, 3
        };
        Atom atom;
        int outer;
        for (int i = 1; i <= 30; i++) {
            atom = new Atom(1, 1, i);
            outer = atom.getElectronsInOuterShell();
            if (outer != outers[i-1]) {
                System.out.println("i=" + i + "; outer=" + outer);
            }
        }
        System.out.println("finished testOuterShell()");

    }
    private static void testBonding() {
        Atom sodium = new Atom(11, 12, 11);
        Atom hydrogen = new Atom(1, 0, 1);
        Atom chlorine = new Atom(17, 18, 17);
        Atom helium = new Atom(2, 2, 2);
        boolean doesBond = sodium.doesIonicBond(hydrogen);
        System.out.println("does sodium bond with hydrogen? " + doesBond);
        doesBond = sodium.doesIonicBond(chlorine);
        System.out.println("does sodium bond with chlorine? " + doesBond);
        doesBond = sodium.doesIonicBond(helium);
        System.out.println("does sodium bond with helium? " + doesBond);
        doesBond = hydrogen.doesIonicBond(chlorine);
        System.out.println("does hydrogen bond with chlorine? " + doesBond);
        doesBond = hydrogen.doesIonicBond(helium);
        System.out.println("does hydrogen bond with helium? " + doesBond);
        doesBond = chlorine.doesIonicBond(helium);
        System.out.println("does chlorine bond with helium? " + doesBond);
        System.out.println("finished testBonding()");
    }
}
