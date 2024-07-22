package TankGame.src.ResourceHandler;

import javax.sound.sampled.*;
import java.io.IOException;

public class Audio implements LineListener {
    private AudioInputStream audioInputStream;
    private Clip sound;

    public Audio(String audioName, float volume) { //negative value to decrease volume
        try {
            audioInputStream = ResourceManager.getAudio(audioName);
            sound = AudioSystem.getClip();
            sound.addLineListener(this);
            sound.open(audioInputStream);

            FloatControl volumeControl = (FloatControl) sound.getControl(FloatControl.Type.MASTER_GAIN);
            volumeControl.setValue(volume);

            sound.start();
        } catch(Exception e) {
            System.out.println("Error with playing sound!");
            e.printStackTrace();
        }
    }

    @Override
    public void update(LineEvent event) {
        if (event.getType() == LineEvent.Type.STOP) {
            event.getLine().close();
            closeAudioStream();
        }
    }

    private void closeAudioStream() {
        try {
            if (audioInputStream != null) {
                audioInputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loopAudio() {
        if(sound != null) {
            sound.setFramePosition(0);
            sound.loop(sound.LOOP_CONTINUOUSLY);
        }
    }

    public void stopAudio() {
        if(sound != null) {
            sound.stop();
            sound.close();
        }
    }
}