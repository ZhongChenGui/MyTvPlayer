package com.learning.mytvplayer.bean;

import java.util.List;

/**
 * Created by howe.zhong
 * on 2022/9/23  14:56
 */
public class EpisodeBean {
    private List<List<String>> list;

    public EpisodeBean(List<List<String>> list) {
        this.list = list;
    }

    public List<List<String>> getList() {
        return list;
    }

    public void setList(List<List<String>> list) {
        this.list = list;
    }
}
