package Sound;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class SoundPlayer {
	/**
	 * SoundPlayer spielt die Soundfile file in einem Thread ab.
	 * 
	 * @param file
	 */
	public SoundPlayer(String file) {
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				Clip clip = null;
				try {
					clip = AudioSystem.getClip();
					clip.open(AudioSystem.getAudioInputStream(getClass().getResource(file)));
					clip.start();
				} catch (Throwable t) {
				}
				while (clip.getFramePosition() != clip.getFrameLength()) {
				}
				clip.close();
				System.out.println(">ENDE clip"); 
			}
		});
		thread.start();
	}

	/**
	 * SoundPlayer spielt die Soundfile file in einem Thread ab.
	 * 
	 * @param file
	 * @param loop
	 */
	public SoundPlayer(String file, boolean loop) {
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				while (loop) {
					Clip clip = null;
					try {
						clip = AudioSystem.getClip();
						clip.open(AudioSystem.getAudioInputStream(getClass().getResource(file)));
						clip.start();
					} catch (Throwable t) {
					}
					while (clip.getFramePosition() != clip.getFrameLength()) {
					}
					clip.close();
					System.out.println(">ENDE clip");
				}
			}
		});
		thread.start();
	}

}
