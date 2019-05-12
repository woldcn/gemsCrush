package classes;

import java.text.SimpleDateFormat;

public class Clock {

    private long startTime;//开始，继续，重新开始的时间
    private long totalTime;//耗时
    private long pauseTime;//暂停前的耗时
    private long bgmTime;//bgm开始的时间

    private final SimpleDateFormat sdf=new SimpleDateFormat();//时间格式

    Clock() {
        this.startTime = System.currentTimeMillis();
        this.pauseTime = 0;
        this.bgmTime = 0;
        sdf.applyPattern("mm:ss");//设置时间格式
    }

    public void setStartTime() {
        this.startTime = System.currentTimeMillis();
    }

    public void setPauseTime() {
        this.pauseTime = this.pauseTime + System.currentTimeMillis() - this.startTime;
    }

    public void setTotalTime() {
        this.totalTime = this.pauseTime + System.currentTimeMillis() - this.startTime;
    }

    public long getTotalTime() {
        return this.totalTime;
    }

    //耗时转为特定格式的字符串
    public String getCountTime() {
        return sdf.format(this.totalTime);
    }

    public void restart() {
        this.startTime = System.currentTimeMillis();
        this.pauseTime = 0;
    }

    //是否重新播放bgm
    public boolean rePlayBgm() {
        if (totalTime - bgmTime > 78000) {
            bgmTime = totalTime;
            return true;
        }
        return false;
    }

}
