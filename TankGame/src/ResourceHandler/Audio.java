package TankGame.src.ResourceHandler;

import javax.sound.sampled.*;

public class Audio {
    private Clip sound;

    public Audio(String audioName, float volume) { //negative value to decrease volume
        try {
            AudioInputStream audioInputStream = ResourceManager.getAudio(audioName);
            sound = AudioSystem.getClip();
            sound.open(audioInputStream);

            FloatControl volumeControl = (FloatControl) sound.getControl(FloatControl.Type.MASTER_GAIN);
            volumeControl.setValue(volume);
        } catch(Exception e) {
            System.out.println("Error with playing sound!");
            e.printStackTrace();
        }
    }

    public void playAudio() {
        if(sound != null) {
            sound.stop();
            sound.setFramePosition(0);
            sound.start();
        }
    }


    public void loopAudio() {
        if(sound != null) {
            sound.stop();
            sound.setFramePosition(0);
            sound.loop(sound.LOOP_CONTINUOUSLY);
        }
    }

    public void stopAudio() {
        if(sound != null) {
            sound.stop();
        }
    }
}