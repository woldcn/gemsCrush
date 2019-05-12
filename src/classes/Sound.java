
package classes;

import java.io.*;
import sun.audio.*;

public class Sound {

    private InputStream inputStream;

    private AudioStream audioStream;

    Sound(String soundPath) {
        this.inputStream = getClass().getResourceAsStream(soundPath);
        try {
            this.audioStream = new AudioStream(this.inputStream);
        } catch (IOException e) {
        }
    }

    public void play() {
        try {
            AudioPlayer.player.start(this.audioStream);
        } catch (Exception e) {
        }
    }
    
    public void stop(){
        AudioPlayer.player.stop(this.audioStream);
    }

}
