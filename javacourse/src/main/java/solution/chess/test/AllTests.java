package solution.chess.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ KingInvalidMoves.class, KingValidMoves.class,
		RookValidMoves.class, TestRook.class, RookInvalidMoves.class })
public class AllTests {

}
