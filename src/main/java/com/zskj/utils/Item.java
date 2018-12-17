package com.zskj.utils;

import java.io.File;

/**
 * @ProjectName: sevenziptest
 * @packageName: com.zskj.utils
 * @Description:
 * @author: huayang.bai
 * @date: 2018-12-17 11:23
 */
public class Item {

    private String name;

    private File file;


    public Item() {
    }

    public Item(String name, File file) {
        this.name = name;
        this.file = file;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    @Override
    public String toString() {
        return "Item{" +
                "name='" + name + '\'' +
                ", file=" + file.getAbsolutePath() +
                '}';
    }
}
