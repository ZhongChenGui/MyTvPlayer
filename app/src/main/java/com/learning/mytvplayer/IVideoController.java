package com.learning.mytvplayer;

/**
 * Created by howe.zhong
 * on 2022/9/22  16:41
 */
public interface IVideoController {
    /**
     * 设置标题
     */
    void setTitle(String title);

    void setVideoPlayer(VideoPlayer videoPlayer);

    /**
     * 恢复初始状态
     */
    void reset();

    /**
     * 设置视频时长
     *
     * @param duration 视频时长
     */
    void setDuration(long duration);

    /**
     * 当播放器的播放状态发生变化，在此方法中国你更新不同的播放状态的UI
     *
     * @param playState 播放状态：
     *                  <ul>
     *                  <li>{@link VideoPlayer#STATE_IDLE}</li>
     *                  <li>{@link VideoPlayer#STATE_PREPARING}</li>
     *                  <li>{@link VideoPlayer#STATE_PREPARED}</li>
     *                  <li>{@link VideoPlayer#STATE_PLAYING}</li>
     *                  <li>{@link VideoPlayer#STATE_PAUSED}</li>
     *                  <li>{@link VideoPlayer#STATE_BUFFERING_PLAYING}</li>
     *                  <li>{@link VideoPlayer#STATE_BUFFERING_PAUSED}</li>
     *                  <li>{@link VideoPlayer#STATE_ERROR}</li>
     *                  <li>{@link VideoPlayer#STATE_COMPLETED}</li>
     *                  </ul>
     */
    void onPlayStateChanged(int playState);
}
