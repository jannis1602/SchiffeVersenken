package input;

import java.awt.event.KeyEvent;

import pack.Console;
import pack.Game;
import pack.State;
import pack.TcpServer.Modus;

import java.awt.event.KeyListener;

public class KeyInput implements KeyListener {

	Game game;
	private boolean consoleKeys = false;

	public KeyInput(Game game) {
		this.game = game;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
//		System.out.println(key);

		if (consoleKeys && key == KeyEvent.VK_F6) {
			System.out.println("OpenConsole...");
			if (Game.console == null)
				Game.console = new Console();
			else
				Game.console.showConsole();
			System.out.println("OpenConsole...");
		}
		if (key == KeyEvent.VK_F5) {
			consoleKeys = true;
		}
		if (!(key == KeyEvent.VK_F5 || key == KeyEvent.VK_F6)) {
			consoleKeys = false;
//			System.out.println("errorKey: " + key);
		}

		if (key == KeyEvent.VK_H)
			Game.mapPlayerOne.showSchiffe = !Game.mapPlayerOne.showSchiffe;

		if (Game.state == State.vorbereitung) {
			if (key == KeyEvent.VK_R) {
				Game.vorbereitungsMenue.selectedSchiff.rotate();
			}
			if (key == KeyEvent.VK_TAB) {
				if (Game.vorbereitungsMenue.selectedSchiff == null && !Game.vorbereitungsMenue.startSchiffe.isEmpty()) {
					Game.vorbereitungsMenue.selectedSchiff = Game.vorbereitungsMenue.startSchiffe.getFirst();
					Game.vorbereitungsMenue.startSchiffe.removeFirst();
				}
			}
			if (key == KeyEvent.VK_ENTER) {
				if (Game.tcpserver.modus == Modus.singleplayer) {
					if (Game.vorbereitungsMenue.startSchiffe.size() == 0
							&& Game.vorbereitungsMenue.selectedSchiff == null) {
						Game.mapPlayerOne.addAllShips();
						Game.playerOneReady = true;
						Game.state = State.player1;
					}
				} else if (Game.vorbereitungsMenue.startSchiffe.size() == 0
						&& Game.vorbereitungsMenue.selectedSchiff == null) {
					Game.mapPlayerOne.addAllShips();
					Game.state = State.wait;
					Game.tcpserver.sendeString("#game#ready");
					Game.playerOneReady = true;
					if (Game.playerOneReady) { // unnötig?
						if (Game.tcpserver.modus == Modus.server) // server soll erst dran sein!
							Game.state = State.player2;
						else
							Game.state = State.player1;
					}
				}
			}
		}
		switch (Game.state) {
		case player1:

			break;
		case player2:
			break;
		case wait:

			break;
		case menu:

		default:
			break;
		}
	}

	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

}
