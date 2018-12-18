package com.zskj.main;

import java.io.File;
import java.io.IOException;

/**
 * @ProjectName: sevenziptest
 * @packageName: com.zskj.main
 * @Description:
 * @author: huayang.bai
 * @date: 2018-12-14 16:06
 */
public class Main {


    public static void main(String[] args) throws IOException {
        String str = "/Users/baihuayang/Downloads/aaa/abc";
        File file = new File(str);
        File parentFile = file.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        boolean newFile = file.createNewFile();
        System.out.println(newFile);
        System.out.println("判断是否是文件：" + file.isFile());
        System.out.println("判断是否是文件夹：" + file.isDirectory());

    }

}
