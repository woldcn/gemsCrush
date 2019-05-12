/*
 * Project Name: EE2311 Project - Gems Crush
 * Student Name:
 * Student ID:
 * 
 */
package classes;

import game.GameConsole;
import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Random;

public class GemsCrush {

    // create the game console for drawing         
    // singleton, always return the same instance
    private final GameConsole console = GameConsole.getInstance();
    //初始化分数
    private String score = "0";
    //声明宝石图片的路径数组
    private final String[] colors = {"/assets/gemBlue.png", "/assets/gemGreen.png", "/assets/gemOrange.png",
        "/assets/gemPurple.png", "/assets/gemRed.png", "/assets/gemWhite.png", "/assets/gemYellow.png"};
    //声明所有宝石
    private final Gem[][] gems = new Gem[8][8];
    //初始化三个按钮的图标和位置
    private final Button startPauseButton = new Button("/assets/pause1.png", 80, 320);
    private final Button restartButton = new Button("/assets/restart1.png", 80, 400);
    private final Button saveButton = new Button("/assets/save1.png", 80, 480);
    //初始化计时器
    Clock clock = new Clock();
    //初始化游戏标志变量为开始（进行）
    boolean start = true;

    public GemsCrush() {
        // make the console visible
        console.show();
    }

    public static void main(String[] args) {
        // a more OO approach to write the main method
        GemsCrush game = new GemsCrush();
        game.startGame();
    }

    public void startGame() {
        //设置背景图片
        console.setBackground("/assets/board.png");
        //初始化背景音乐对象
        Sound bgm = new Sound("/assets/bgm.wav");
        bgm.play();//播放背景音乐
        //初始化所有方块(参数为真，表示读取文件后再初始化）
        initGems(true);
        //声明第一次和第二次被选中的方块
        Gem gem1 = null;
        Gem gem2 = null;
        //进入游戏循环
        while (true) {
            //若前一背景音乐流播放完毕，创建新的背景音乐流，实现循环播放
            if (clock.rePlayBgm()) {
                bgm = new Sound("/assets/bgm.wav");
                bgm.play();
            }
            // 获取输入
            Point point = console.getClickedPoint();
            if (point != null) { //当有鼠标点击时
                //初始化点击音乐对象
                Sound select = new Sound("/assets/select.wav");
                //检查各个按钮是否被点击
                if (startPauseButton.isAt(point) && start) {//暂停按钮被点击
                    select.play();//播放点击音效
                    bgm.stop();//暂停背景音乐
                    clock.setPauseTime();//计时器暂停
                    start = false;//设置游戏为暂停
                    //设置按钮点击效果
                    startPauseButton.setPic("/assets/pause2.png");
                    sleep(70);
                    startPauseButton.setPic("/assets/start1.png");
                } else if (startPauseButton.isAt(point) && start == false) {//开始按钮被点击
                    select.play();
                    bgm.play();//恢复背景音乐
                    clock.setStartTime();//设置新开始的时间
                    start = true;//设置游戏为进行
                    //设置按钮点击效果
                    startPauseButton.setPic("/assets/start2.png");
                    sleep(70);
                    startPauseButton.setPic("/assets/pause1.png");
                } else if (restartButton.isAt(point)) {//重新开始按钮被点击
                    start = true;
                    clock.restart();//重新计时
                    score = "0";//分数归零
                    //重新播放音乐
                    bgm.stop();//停止前一音乐流
                    bgm = new Sound("/assets/bgm.wav");
                    bgm.play();
                    initGems(false);//参数为假，表示不读取文件，直接初始化
                    //设置按钮点击效果
                    restartButton.setPic("/assets/restart2.png");
                    sleep(70);
                    restartButton.setPic("/assets/restart1.png");
                } else if (saveButton.isAt(point)) {//保存按钮被点击
                    select.play();
                    writeObjectToFile();//写入文件
                    //设置按钮点击效果
                    saveButton.setPic("/assets/save2.png");
                    sleep(70);
                    saveButton.setPic("/assets/save1.png");
                }

                //检查每个宝石是否被选中，若有两个被选中，则结束检查
                for (int i = 0; i < 8; i++) {
                    for (int j = 0; j < 8; j++) {
                        if (gems[i][j].isAt(point) && start) {
                            if (gem1 == null) {
                                gem1 = gems[i][j];
                                gems[i][j].toggleFocus();//设置宝石被点击了
                                select.play();
                            } else {
                                gem2 = gems[i][j];
                                gems[i][j].toggleFocus();
                                select.play();
                                break;
                            }
                        }
                    }
                    if (gem2 != null) {
                        break;
                    }
                }
                //当有两个宝石被选中时
                if (gem1 != null && gem2 != null) {
                    //当选中的两个宝石相邻时
                    if (Math.abs(gem1.getPosX() - gem2.getPosX()) == 1 && (gem1.getPosY() - gem2.getPosY() == 0)
                            || Math.abs(gem1.getPosY() - gem2.getPosY()) == 1 && (gem1.getPosX() - gem2.getPosX()) == 0) {
                        swapGems(gem1, gem2);//交换两个宝石的图片和类型

                        while (check() == true) {//当检查可消除时
                            refresh();
                            remove();//消除
                            slide();//滑落和填充 
                        }
                    } else {//当选择的两个宝石不相邻时,取消选中
                        gems[gem1.getPosY()][gem1.getPosX()].toggleFocus();//设置宝石再次被点击，取消选中
                        gems[gem2.getPosY()][gem2.getPosX()].toggleFocus();
                    }
                    //重置两个宝石选择
                    gem1 = null;
                    gem2 = null;
                }

            }
            //刷新画面
            refresh();

            // the idle time affects the no. of iterations per second which 
            // should be larger than the frame rate
            // for fps at 25, it should not exceed 40ms
            console.idle(10);
        }

    }

    //延时刷新画面
    public void sleep(int time) {
        long paseTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - paseTime < time) {
            refresh();
        }
    }

    //刷新画面
    public void refresh() {
        if (console.shouldUpdate()) {
            //清除画布
            console.clear();
            //计时
            if (start == true) {
                clock.setTotalTime();
            }
            //重绘时间和分数
            console.drawText(60, 150, "[TIME]", new Font("Helvetica", Font.BOLD, 20), Color.white);
            console.drawText(60, 180, clock.getCountTime(), new Font("Helvetica", Font.PLAIN, 20), Color.white);

            console.drawText(60, 250, "[SCORE]", new Font("Helvetica", Font.BOLD, 20), Color.white);
            console.drawText(60, 280, score, new Font("Helvetica", Font.PLAIN, 20), Color.white);

            //显示每个方块
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    gems[i][j].display();
                }
            }
            //显示各个按钮
            saveButton.disPlay();
            startPauseButton.disPlay();
            restartButton.disPlay();

            console.update();
        }
    }

    //初始化宝石
    public void initGems(boolean bool) {
        File file = new File("assets/data.dat");
        if (file.exists() && bool) {//如果文件存在则读取文件
            readObjectFromFile(file);
        } else {//若文件不存在，则随机初始化
            Random random = new Random();

            do {
                for (int i = 0; i < 8; i++) {
                    for (int j = 0; j < 8; j++) {
                        int type = random.nextInt(7);
                        gems[i][j] = new Gem("", type, j, i);

                    }
                }
            } while (check() == true);//保证初始化的宝石不可消除
        }
        //初始化的动画
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                int type;
                type = gems[i][j].getType();
                gems[i][j] = new Gem(colors[type], type, j, i);
                sleep(10);
            }
        }
    }

    //交换相邻宝石的图片和类型
    public void swapGems(Gem gem1, Gem gem2) {
        Gem tempGem = gem1;
        int x1, x2, y1, y2;
        float x, y;
        x1 = gem1.getPosX();
        x2 = gem2.getPosX();
        y1 = gem1.getPosY();
        y2 = gem2.getPosY();
        x = (float) ((x2 + x1) / 2.0);
        y = (float) ((y2 + y1) / 2.0);
        //交换动画
        for (int i = 1; i < 5; i++) {
            gems[y1][x1].setX((float) (x1 + (x - x1) / 2.0 * i) * 65 + 240);
            gems[y1][x1].setY((float) (y1 + (y - y1) / 2.0 * i) * 65 + 40);
            gems[y2][x2].setX((float) (x2 + (x - x2) / 2.0 * i) * 65 + 240);
            gems[y2][x2].setY((float) (y2 + (y - y2) / 2.0 * i) * 65 + 40);
            sleep(100);
        }
        gems[y1][x1] = new Gem(gem2.getFile(), gem2.getType(), x1, y1);
        gems[y2][x2] = new Gem(tempGem.getFile(), tempGem.getType(), x2, y2);

    }

    //检查是否消除
    public boolean check() {
        boolean flag = false;//是否可消除标志
        int nx, ny, nnx, nny;
        int[] dx = {0, 0, -1, 1};
        int[] dy = {-1, 1, 0, 0};
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                for (int k = 0; k < 4; k++) {
                    nx = j + dx[k];
                    ny = i + dy[k];
                    nnx = nx + dx[k];
                    nny = ny + dy[k];
                    //检查任意相邻的三个
                    if (nx >= 0 && nx < 8 && ny >= 0 && ny < 8 && gems[ny][nx].getType() == gems[i][j].getType() && gems[i][j].getType() != -1
                            && nnx >= 0 && nnx < 8 && nny >= 0 && nny < 8 && gems[nny][nnx].getType() == gems[i][j].getType()) {
                        gems[i][j].setRemove(true);//若可消除，则设置宝石状态为消除
                        gems[ny][nx].setRemove(true);
                        gems[i][j].setSelected(true);//设置宝石为选择状态
                        gems[ny][nx].setSelected(true);
                        flag = true;
                    }
                }
            }
        }
        return flag;
    }

    //消除
    public void remove() {
        sleep(200);
        Sound match = new Sound("/assets/match.wav");
        match.play();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (gems[i][j].isRemove()) {
                    //闪几下。。。
                    for (int k = 0; k < 3; k++) {
                        gems[i][j].setPic("");
                        sleep(30);
                        gems[i][j].setPic("/assets/Stars.png");
                        sleep(30);
                    }
                    //消除
                    gems[i][j] = new Gem("", -1, j, i);
                    gems[i][j].setRemove(false);
                    score = String.valueOf(Integer.parseInt(score) + 10);
                }
            }
        }
    }

    //滑落和填充
    public void slide() {
        //初始化滑落音效对象
        Sound fall = new Sound("/assets/fall.wav");
        for (int i = 7; i >= 0; i--) {
            for (int j = 0; j < 8; j++) {
                if (gems[i][j].getType() == -1) {
                    int ny = i;
                    int nny = ny - 1;
                    while (nny >= 0) {
                        if (gems[nny][j].getType() == -1) {
                            nny--;
                        } else {
                            for (int k = 1; k < 5; k++) {//滑落的动画
                                gems[nny][j].setY((float) ((nny + (ny - nny) / 4.0 * k) * 65 + 40));
                                sleep(20);
                                fall.play();
                            }
                            gems[ny][j] = new Gem(gems[nny][j].getFile(), gems[nny][j].getType(), j, ny);
                            gems[nny][j] = new Gem("", -1, j, nny);
                            ny--;
                            nny--;
                        }
                    }
                }
            }
        }
        //随机填充滑落后的空格
        Random random = new Random();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j <8; j++) {
                if (gems[j][i].getType() == -1) {
                    int type = random.nextInt(7);
                    gems[j][i] = new Gem(colors[type], type, i, j);
                    sleep(100);
                }
            }
        }

    }

    //写入文件
    public void writeObjectToFile() {
        ArrayList<String> list = new ArrayList();
        //把宝石的状态序列化到list中
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                list.add(gems[i][j].getFile());
                list.add(String.valueOf(gems[i][j].getType()));
                list.add(String.valueOf(gems[i][j].getPosX()));
                list.add(String.valueOf(gems[i][j].getPosY()));
            }
        }
        File file = new File("assets/data.dat");
        try {
            FileOutputStream fileOut = new FileOutputStream(file);
            ObjectOutputStream objOut = new ObjectOutputStream(fileOut);
            objOut.writeObject(list);//把list写入文件
            objOut.flush();
            objOut.close();
            System.out.println("Write to file success!");
        } catch (IOException e) {
            System.out.println("Write to file failed!");
        }
    }

    //读取文件
    public Object readObjectFromFile(File file) {
        ArrayList<String> temp = null;
        try {
            FileInputStream fileIn = new FileInputStream(file);
            ObjectInputStream objIn = new ObjectInputStream(fileIn);
            temp = (ArrayList<String>) objIn.readObject();//通过temp获取文件中序列化后的list
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    gems[i][j] = new Gem("", Integer.parseInt(temp.get(i * 32 + j * 4 + 1)), Integer.parseInt(temp.get(i * 32 + j * 4 + 2)), Integer.parseInt(temp.get(i * 32 + j * 4 + 3)));
                }
            }
            objIn.close();
            System.out.println("Read from file success!");
        } catch (IOException e) {
            System.out.println("Read from file failed!");
        } catch (ClassNotFoundException e) {
        }
        return temp;
    }

}
