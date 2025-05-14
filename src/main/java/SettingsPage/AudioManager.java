package SettingsPage;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class AudioManager {
    private Clip clip;
    private FloatControl volumeControl;

    public void playMusic(String path) {
        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File(path));
            clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY);

            // Get volume control
            volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            setVolume(0.5f); // Default to 50%

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void setVolume(float level) {
        if (volumeControl != null) {
            float min = volumeControl.getMinimum(); // typically -80.0
            float max = volumeControl.getMaximum(); // typically 6.0
            float volume = min + (max - min) * level;
            volumeControl.setValue(volume);
        }
    }

    public void playSoundEffect(String path) {
        new Thread(() -> {
            try {
                AudioInputStream soundStream = AudioSystem.getAudioInputStream(new File(path));
                Clip clip = AudioSystem.getClip();
                clip.open(soundStream);
                clip.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void stop() {
        if (clip != null) {
            clip.stop();
            clip.close();
        }
    }
}
