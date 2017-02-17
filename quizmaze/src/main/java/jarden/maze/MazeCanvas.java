package jarden.maze;

import jarden.timer.Timer;
import jarden.timer.TimerListener;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;

public class MazeCanvas extends Canvas implements TimerListener {
	private static final long serialVersionUID = 1L;
	private final int MEDIAMETER = 20;
	private final int BOXWIDTH = 24;
	private final int MAZEX = 2, MAZEY = 2;
	private final int BORDER = (BOXWIDTH - MEDIAMETER) / 2;
	private MazeListener mazeListener;
	private int mazeW;
	private int mazeH;
	private Timer timer;
	private int baddySleepTenths;
	private boolean baddyReachedMeStart;
	private Player baddy, me;
	private MoveTrail meMoves;
	private int maxX, maxY;
	private boolean[][][] gates; // [x][y][0=bottom, 1=right]
	private Font keyFont;

	public MazeCanvas(MazeListener mazeListener, int xSquares,
			int ySquares, int baddySleepTenths) {
		baddy = new Player(Color.red);
		me = new Player(Color.blue);
		meMoves = new MoveTrail();
		this.mazeListener = mazeListener;
		this.baddySleepTenths = baddySleepTenths;
		maxX = xSquares - 1;
		maxY = ySquares - 1;
		mazeW = (maxX + 1) * BOXWIDTH;
		mazeH = (maxY + 1) * BOXWIDTH;
		gates = new boolean[xSquares][ySquares][2];
		keyFont = new Font("Helvetica", Font.BOLD, 16);
		setSize(mazeW + 4, mazeH + 4);
		reset();
	}
	public void giveKey() {
		me.gotKey = true;
		paintPlayer(me);
	}
	/**
	 * Follow shortest path to Me's start place,
	 * then follow meMoves (the route Me follows).
	 */
	private void moveBaddy() {
		int distanceApart = 0;
		// first, get to where Me starts:
		if (!baddyReachedMeStart) {
			baddy.oldX = baddy.x;
			baddy.oldY = baddy.y;
			++baddy.y;
			distanceApart = maxY - baddy.y;
			if (baddy.y == maxY) {
				baddyReachedMeStart = true;
			}
		}
		else {
			// then follow meMoves:
			if (meMoves.isEmpty()) return;
			Point point = meMoves.getFirstMove();
			baddy.oldY = baddy.y;
			baddy.oldX = baddy.x;
			baddy.x = point.x;
			baddy.y = point.y;
		}
		paintPlayer(baddy);
		if (baddy.x == me.x && baddy.y == me.y) {
			mazeListener.onLost();
			stop();
		}
		distanceApart += meMoves.size();
		if (distanceApart < 5) {
			mazeListener.onLookOut();
		}
	}
	public void moveMe(int keyCode) {
		switch (keyCode) {
		case KeyEvent.VK_UP:
			if (me.y < 1) return;
			if (gates[me.x][me.y - 1][0]) {
				if (me.gotKey) {
					gates[me.x][me.y - 1][0] = false;
					repaint();
					me.gotKey = false; // just used it!
				}
				else return; // gate closed
			}
			me.oldX = me.x;
			me.oldY = me.y;
			--me.y;
			break;
		case KeyEvent.VK_DOWN:
			if (me.y >= maxY) return;
			if (gates[me.x][me.y][0]) {
				if (me.gotKey) {
					gates[me.x][me.y][0] = false;
					repaint();
					me.gotKey = false;
				}
				else return;
			}
			me.oldX = me.x;
			me.oldY = me.y;
			++me.y;
			break;
		case KeyEvent.VK_LEFT:
			if (me.x < 1) return;
			if (gates[me.x - 1][me.y][1]) {
				if (me.gotKey) {
					gates[me.x - 1][me.y][1] = false;
					repaint();
					me.gotKey = false;
				}
				else return;
			}
			me.oldX = me.x;
			me.oldY = me.y;
			--me.x;
			break;
		case KeyEvent.VK_RIGHT:
			if (me.x >= maxX) return;
			if (gates[me.x][me.y][1]) {
				if (me.gotKey) {
					gates[me.x][me.y][1] = false;
					repaint();
					me.gotKey = false;
				}
				else return;
			}
			me.oldX = me.x;
			me.oldY = me.y;
			++me.x;
			break;
		default:
			throw new IllegalStateException("unrecognised keyCode: " + keyCode); 
		}
		paintPlayer(me);
		if (baddy.x == me.x && baddy.y == me.y) {
			mazeListener.onLost();
			stop();
		}
		meMoves.addMove(new Point(me.x, me.y));
		if (me.x == 0 && me.y == 0) {
			mazeListener.onNextLevel();
		}
	}
	/*
	 * Leave last column blank. To leave last row blank instead,
	 * switch endpoint conditions on 'for' statements.
	 */
	private void newMaze() {
		for (int i = 0; i < maxX; i++) {
			for (int j = 0; j <= maxY; j++) {
				gates[i][j][0] = (Math.random() > 0.4);
				gates[i][j][1] = (Math.random() > 0.4);
			}
		}
	}
	public void paint(Graphics g) {
		g.setColor(Color.yellow);
		g.fillRect(MAZEX, MAZEY, BOXWIDTH, BOXWIDTH);
		g.setColor(Color.black);
		g.drawRect(MAZEX, MAZEY, mazeW, mazeH);
		for (int i = 0; i <= maxX; i++) {
			for (int j = 0; j <= maxY ; j++) {
				if (gates[i][j][0]) {
					g.drawLine( // bottom line
						MAZEX + i * BOXWIDTH,
						MAZEY + (j + 1) * BOXWIDTH,
						MAZEX + (i + 1) * BOXWIDTH,
						MAZEY + (j + 1) * BOXWIDTH);
				}
				if (gates[i][j][1]) {
					g.drawLine( // right line
						MAZEX + (i + 1) * BOXWIDTH,
						MAZEY + j * BOXWIDTH,
						MAZEX + (i + 1) * BOXWIDTH,
						MAZEY + (j + 1) * BOXWIDTH);
				}
			}
		}
		paintPlayer(me);
		paintPlayer(baddy);
	}
	private void paintPlayer(Player player) {
		Graphics g = getGraphics();
		if (g == null) return;
		if (player.oldX != -1) {
			g.setColor(getBackground());
			g.fillOval(
				MAZEX + BORDER + (player.oldX * BOXWIDTH),
				MAZEY + BORDER + (player.oldY * BOXWIDTH),
				MEDIAMETER, MEDIAMETER);
			player.oldX = player.oldY = -1;
		}
		g.setColor(player.colour);
		int tx, ty;
		g.fillOval(
			tx = MAZEX + BORDER + (player.x * BOXWIDTH),
			ty = MAZEY + BORDER + (player.y * BOXWIDTH),
			MEDIAMETER, MEDIAMETER);
		if (player.gotKey) {
			g.setColor(Color.white);
			g.setFont(keyFont);
			g.drawString("K", tx + (MEDIAMETER / 3),
				ty + MEDIAMETER - 5);
		}
	}
	public void stop() {
		if (timer != null) timer.stop();
	}
	public void reset() {
		newMaze();
		meMoves.clear();
		baddyReachedMeStart = false;
		me.gotKey = false;
		baddy.x = maxX;
		baddy.y = 0;
		baddy.oldX = baddy.oldY = -1;
		me.x = maxX;
		me.y = maxY;
		me.oldX = me.oldY = -1;
		repaint();
		if (timer != null) {
			timer.stop();
		}
		timer = new Timer(this, baddySleepTenths);
	}
	public void setBaddySleep(int baddySleepTenths) {
		this.baddySleepTenths = baddySleepTenths;
	}
	@Override
	public void onTimerEvent() {
		moveBaddy();
	}
}
