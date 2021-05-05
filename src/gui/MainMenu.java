package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import pack.Game;
import pack.Scene;

public class MainMenu {

	private Btn[] GameMenubtns;
	private ActionListener al;

	public MainMenu() {
		GameMenubtns = new Btn[5];

		// GameMenubtns[0] = new Btn(Game.getFrameWidth()/2-150, 300, 300, 80,
		// "Play",Color.LIGHT_GRAY, Color.DARK_GRAY, true, 0);
		// GameMenubtns[1] = new Btn(Game.getFrameWidth()/2-150, 400, 300, 80,
		// "Retry",Color.LIGHT_GRAY, Color.DARK_GRAY, true, 0);
		// GameMenubtns[2] = new Btn(Game.getFrameWidth()/2-150, 500, 300, 80,
		// "Options",Color.LIGHT_GRAY, Color.DARK_GRAY, true, 0);
		// GameMenubtns[3] = new Btn(Game.getFrameWidth()/2-150, 600, 300, 80,
		// "Launcher",Color.LIGHT_GRAY, Color.DARK_GRAY, true, 0);

		GameMenubtns[0] = new Btn(Game.getFrameWidth() / 2 - 150, Game.getFrameHeight() / 10 * 2, 300, 80, "Coop-Modus",
				Color.LIGHT_GRAY, Color.DARK_GRAY, true, 0, Scene.mainMenu, 0);
		GameMenubtns[1] = new Btn(Game.getFrameWidth() / 2 - 150, Game.getFrameHeight() / 10 * 3, 300, 80,
				"Multiplayer", Color.LIGHT_GRAY, Color.DARK_GRAY, true, 0, Scene.mainMenu, 1);
		GameMenubtns[2] = new Btn(Game.getFrameWidth() / 2 - 150, Game.getFrameHeight() / 10 * 4, 300, 80,
				"Singleplayer", Color.LIGHT_GRAY, Color.DARK_GRAY, true, 0, Scene.mainMenu, 2);
		GameMenubtns[3] = new Btn(Game.getFrameWidth() / 2 - 150, Game.getFrameHeight() / 10 * 5, 300, 80, "Options",
				Color.LIGHT_GRAY, Color.DARK_GRAY, true, 0, Scene.mainMenu, 3);
		GameMenubtns[4] = new Btn(Game.getFrameWidth() / 2 - 150, Game.getFrameHeight() / 10 * 6, 300, 80, "Credits",
				Color.LIGHT_GRAY, Color.DARK_GRAY, true, 0, Scene.mainMenu, 3);
		GameMenubtns[0].addActionListener(al);
		GameMenubtns[1].addActionListener(al);
		GameMenubtns[2].addActionListener(al);
		GameMenubtns[3].addActionListener(al);
		GameMenubtns[4].addActionListener(al);

		al = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Btn tBtn = (Btn) e.getSource();
				switch (tBtn.getID()) {
				case 0:

					break;
				case 1:

					break;
				case 2:

					break;
				case 3:

					break;
				case 4:
					System.out.println("Credits");
					break;
				default:
					break;
				}
			}
		};
	}

	public void render(Graphics g) {
//		g.setColor(new Color(150, 150, 150));
//		g.fillRect(0, 0, Game.getFrameWidth(), Game.getFrameHeight());

//		g.setColor(Color.red);
//		g.fillRect(100, Game.getFrameHeight()/2, 1800, 10);

		for (int i = 0; i < GameMenubtns.length; i++) {
			GameMenubtns[i].render(g);
		}
	}

}
