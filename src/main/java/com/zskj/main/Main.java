package com.zskj.main;

import com.zskj.utils.CompressUtils;

/**
 * @ProjectName: sevenziptest
 * @packageName: com.zskj.main
 * @Description:
 * @author: huayang.bai
 * @date: 2018-12-14 16:06
 */
public class Main {


    public static void main(String[] args) throws Exception {
        Integer numberOfItemsInArchive = CompressUtils.getNumberOfItemsInArchive("/Users/baihuayang/Downloads/abc.zip");
        System.out.println(numberOfItemsInArchive);
    }

}
