
package classes;

import game.GameConsole;
import java.awt.Image;
import java.awt.Point;
import javax.swing.ImageIcon;


public class Gem{

    // the upper-left corner of the board, reference origin point
    public static final int X = 240;
    public static final int Y = 40;
    // the size of the gem
    public static final int W = 65;
    public static final int H = 65;

    // default position in 8x8 grid    
    private int posX = 0;
    private int posY = 0;
    private boolean selected = false;

    private Image pic;
    private final Image focus = new ImageIcon(this.getClass().getResource("/assets/focus.png")).getImage();
    
    //是否应该被消除
    private boolean isRemove = false;
    //图片文件的地址
    private String file;
    //宝石的类型（-1~6）,-1表示没有
    private int type;
    //绝对位置
    private int x;
    private int y;

    Gem(String file, int type, int x, int y) {
        this.file = file;
        this.type = type;
        this.pic = new ImageIcon(this.getClass().getResource(file)).getImage();
        this.posX = x;
        this.posY = y;
        this.x = this.posX * W + X;
        this.y = this.posY * W + Y;
    }

    public void display() {
        GameConsole.getInstance().drawImage(x, y, pic);
        if (selected) {
            GameConsole.getInstance().drawImage(x, y, focus);
        }
    }
    
    //是否被
    public boolean isAt(Point point) {
        if (point != null) {
            return (point.x > (posX * W + X) && point.x <= ((posX + 1) * W + X) && point.y > (posY * H + Y) && point.y <= ((posY + 1) * H + Y));
        } else {
            return false;
        }
    }

    public Image getPic() {
        return pic;
    }

    public void setPic(String file) {
        this.pic = new ImageIcon(this.getClass().getResource(file)).getImage();
    }

    public int getPosX() {
        return posX;
    }

    public void setPosX(int posX) {
        this.x = posX * W + X;
        this.posX = posX;
    }

    public int getPosY() {
        return posY;
    }

    public void setPosY(int posY) {
        this.y = posY * W + Y;
        this.posY = posY;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean getSelected() {
        return selected;
    }

    public void toggleFocus() {
        selected = !selected;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getFile() {
        return this.file;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setRemove(boolean remove) {
        this.isRemove = remove;
    }

    public boolean isRemove() {
        return isRemove;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public void setX(float x) {
        this.x = (int) x;
    }

    public void setY(float y) {
        this.y = (int) y;
    }

}
