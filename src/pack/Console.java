package pack;

import java.awt.Button;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedList;

import javax.swing.JFrame;

public class Console {

	public JFrame frame;
	private TextArea textArea;
	private TextField textEingabe;
	private LinkedList<String> msgList; // last msgs
	private int textInList = 0;

	public Console() {
		createFrame();
		msgList = new LinkedList<String>();
//		frame.setVisible(true);
//		textEingabe.requestFocus();
	}

	public void showConsole() {
		frame.setVisible(true);
		textEingabe.requestFocus();
	}

	// console clear !!!

	private void createFrame() {
		frame = new JFrame("console");
		frame.setSize(520, 180);
		frame.setLayout(null);
		textArea = new TextArea(null, 1, 1, TextArea.SCROLLBARS_VERTICAL_ONLY);
		textArea.setBounds(1, 1, 480, 110);
		textArea.setText("");
		textArea.setEditable(false);
		// textArea.setCaretPosition(textArea.getRows());
		frame.add(textArea);

		textEingabe = new TextField();
		textEingabe.setBounds(1, 115, 420, 22);
		textEingabe.addActionListener(acl);
		frame.add(textEingabe);
		textEingabe.requestFocus();

		Button btnSende = new Button("Senden");
		btnSende.setBounds(420, 115, 60, 22);
		btnSende.addActionListener(acl);
		frame.add(btnSende);
		textEingabe.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {

				if (e.getKeyCode() == KeyEvent.VK_UP) {
//					String[] last = textArea.getText().split("\n");
//					textEingabe.setText(last[last.length - 1]); // mehr als nur last...
					if (textEingabe.getText().length() < 1)
						textEingabe.setText(msgList.get(textInList));
					else if (textEingabe.getText().contentEquals(msgList.get(textInList))) {
						if (textInList >= 1)
							textInList--;
					} else
						textInList = 0;
					textEingabe.setText(msgList.get(textInList));

					textEingabe.setCaretPosition(textEingabe.getText().length());

				} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
					if (textEingabe.getText().contentEquals(msgList.get(textInList))) {
						if (textInList < msgList.size() - 1)
							textInList++;
					} else
						textInList = msgList.size() - 1;
					textEingabe.setText(msgList.get(textInList));

					textEingabe.setCaretPosition(textEingabe.getText().length());
				}
			}

			@Override
			public void keyTyped(KeyEvent e) {

			}

			@Override
			public void keyReleased(KeyEvent e) {

			}

		});
	}

	public void addTextToTextArea(String text) {
		textArea.setText(textArea.getText() + "\n" + text);
		textArea.setCaretPosition(textArea.getText().length());
	}

	ActionListener acl = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (textEingabe.getText().length() >= 1) {
				String text = textEingabe.getText();
				System.out.println(text);
				addTextToTextArea(text);
				if (msgList.size() >= 1) {
					if (!text.equals(msgList.getLast()))
						msgList.add(text);
				} else
					msgList.add(text);
				textInList = msgList.size() - 1;

				textEingabe.setText(null);
				textEingabe.requestFocus();
				if (text.charAt(0) == '#') {
					comands(text);
				} else
					Game.tcpserver.sendeString("#msg#" + text);
			}
		}
	};

	private void comands(String text) {
		if (text.equals("#exit"))
			System.exit(0);
		else if (text.equals("#cl"))
			textArea.setText(""); // null?
		else if (text.equals("#cla")) {
			textArea.setText(""); // null?
			msgList.clear();
		} else if (text.equals("#requestShips")) {
			Game.tcpserver.sendeString("#requestships");
			Game.mapPlayerTwo.hiddenShips = null;
		} else if (text.equals("#showhiddnships")) {
			Game.mapPlayerTwo.showHiddenShips = !Game.mapPlayerTwo.showHiddenShips;
			System.out.println("!!showhiddnships!!");
		} else if (text.equals("#hack")) {
			Game.tcpserver.sendeString("#sendhiddnships");
			Game.mapPlayerTwo.showHiddenShips = true;
		} else if (text.contains("#send")) {
			String[] temp = text.split("#");
			Game.tcpserver.sendeString("#" + temp[2]);
		} else if (text.contains("#hacksoff")) {
			Game.tcpserver.hacks = !Game.tcpserver.hacks;
		}
	}

//	public void show() {
//		frame.setVisible(!frame.isVisible());
//	}

}
