package jarden.maze;

import java.util.LinkedList;

/** 
 * ArrayList of Points, holding trail of Player.
 * E.g. so baddy can chase after me.
 */
public class MoveTrail {
	private LinkedList<Point> pointList = new LinkedList<Point>();
	
	public synchronized void addMove(Point point) {
		Point pointI;
		for (int i = 0; i < pointList.size(); i++) {
			pointI = pointList.get(i);
			if (point.equals(pointI)) {
				// Me has returned to same point;
				// throw away subsequent moves:
				int deleteCt = pointList.size() - i - 1;
				for (int j=0; j < deleteCt; j++) {
					pointList.removeLast();
				}
				return;
			}
		}
		pointList.add(point);
	}
	public synchronized Point getFirstMove() {
		return pointList.remove();
	}
	public boolean isEmpty() {
		return pointList.isEmpty();
	}
	public void clear() {
		pointList.clear();
	}
	public int size() {
		return pointList.size();
	}
}
