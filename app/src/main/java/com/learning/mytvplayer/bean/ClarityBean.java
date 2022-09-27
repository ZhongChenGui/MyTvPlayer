package com.learning.mytvplayer.bean;

import java.util.List;

/**
 * Created by howe.zhong
 * on 2022/9/26  11:26
 */
public class ClarityBean {
    private List<String> mList;

    public ClarityBean(List<String> list) {
        mList = list;
    }

    public List<String> getList() {
        return mList;
    }

    public void setList(List<String> list) {
        mList = list;
    }
}
