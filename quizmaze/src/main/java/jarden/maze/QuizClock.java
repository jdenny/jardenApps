package jarden.maze;

import jarden.clock.ClockListener;
import jarden.timer.Timer;
import jarden.timer.TimerListener;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;

public class QuizClock extends Canvas implements TimerListener {
	private static final long serialVersionUID = 1L;
	private int seconds;
	private int diameter;
	private Timer timer;
	private ClockListener clockListener;
	
	public QuizClock(int diameter, ClockListener clockListener) {
		this.diameter = diameter;
		this.clockListener = clockListener;
		seconds = 60;
		setSize(diameter + 4, diameter + 4);
	}
	public int adjust(int secs) {
		seconds += secs;
		if (seconds > 60) seconds = 60;
		else if (seconds < 0) {
			if (clockListener != null) {
				clockListener.onLost(); // notify owner of timeout
			}
			seconds = 0;
			stop();
		}
		repaint();
		return seconds;
	}
	public void paint(Graphics g) {
		g.setColor(Color.green);
		g.fillOval(0, 0, diameter, diameter);
		if (seconds < 60) {
			g.setColor(Color.red);
			g.fillArc(0, 0, diameter, diameter, 90,
				6 * (seconds - 60));
		}
	}
	public void stop() {
		if (timer != null) timer.stop();
	}
	public void reset() {
		seconds = 60;
		repaint();
		//! seconds = 60; // seems to be a bug in repaint!
		stop();
		timer = new Timer(this, 30);
	}
	public void update(Graphics g) {
		paint(g);
	}
	@Override
	public void onTimerEvent() {
		adjust(-1);
		if (clockListener != null) {
			clockListener.onClockTick(); // notify owner each tick
		}
	}
}
