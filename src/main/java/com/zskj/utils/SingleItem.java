package com.zskj.utils;

import java.util.Arrays;

/**
 * @ProjectName: sevenziptest
 * @packageName: com.zskj.utils
 * @Description:
 * @author: huayang.bai
 * @date: 2018-12-14 17:28
 */
public class SingleItem {
    private String name;

    private byte[] content;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "SingleItem{" +
                "name='" + name + '\'' +
                ", content=" + Arrays.toString(content) +
                '}';
    }
}
