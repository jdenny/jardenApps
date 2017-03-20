package jarden.life;

/**
 * Created by john.denny@gmail.com on 20/03/2017.
 */

public interface ChainResource extends CellResource {
    boolean hasNext();
    CellResource next();
}
