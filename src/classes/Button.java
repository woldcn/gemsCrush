package classes;

import game.GameConsole;
import java.awt.Image;
import java.awt.Point;
import javax.swing.ImageIcon;

public class Button {

    private final int x;//绝对位置
    private final int y;
    private final int W = 64;//图片尺寸
    private final int H = 64;
    private Image pic;

    public Button(String file, int x, int y) {
        this.x = x;
        this.y = y;
        this.pic = new ImageIcon(this.getClass().getResource(file)).getImage();
    }
    
    public void setPic(String file){
        this.pic=new ImageIcon(this.getClass().getResource(file)).getImage();
    }

    public void disPlay() {
        GameConsole.getInstance().drawImage(x, y, pic);
    }
    
    //是否被点击
    public boolean isAt(Point point) {
        if (point != null) {
            return (point.x > x && point.x <= (W + x) && point.y > y && point.y <= (H + y));
        } else {
            return false;
        }
    }
}
