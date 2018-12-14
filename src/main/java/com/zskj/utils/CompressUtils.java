package com.zskj.utils;

import net.sf.sevenzipjbinding.*;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;
import net.sf.sevenzipjbinding.simple.ISimpleInArchive;
import net.sf.sevenzipjbinding.simple.ISimpleInArchiveItem;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * @ProjectName: sevenziptest
 * @packageName: com.zskj.utils
 * @Description:
 * @author: huayang.bai
 * @date: 2018-12-14 16:06
 */
public class CompressUtils {


    private CompressUtils() {
    }


    public static void main(String[] args) {
        sevenZipJBindingInitCheck();
        String archiveFile = "/Users/baihuayang/Downloads/abc.zip";
        Integer numberOfItemsInArchive = getNumberOfItemsInArchive(archiveFile);
        System.out.println(numberOfItemsInArchive);
        getListItemsSimple(archiveFile);
    }


    /**
     * 检查sevenzip是否成功
     */
    public static void sevenZipJBindingInitCheck() {
        try {
            SevenZip.initSevenZipFromPlatformJAR();
            System.out.println("sevenzip压缩检查成功");
        } catch (SevenZipNativeInitializationException e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取归档文件中的文件个数
     *
     * @param archiveFile
     * @return
     */
    public static Integer getNumberOfItemsInArchive(String archiveFile) {
        IInArchive inArchive = getInArchive(archiveFile);
        try {
            return inArchive.getNumberOfItems();
        } catch (SevenZipException e) {
            e.printStackTrace();
        }
        return 0;
    }


    public static IInArchive getInArchive(String archiveFile) {
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(archiveFile, "r");
             RandomAccessFileInStream randomAccessFileInStream = new RandomAccessFileInStream(randomAccessFile);
             //打开一个压缩包，ArchiveFormat.ZIP可以为null，如果为null的情况下，会自动选择
             IInArchive iInArchive = SevenZip.openInArchive(ArchiveFormat.ZIP, randomAccessFileInStream)) {
            return iInArchive;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (SevenZipException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void getListItemsSimple(String archiveFile) {
        IInArchive inArchive = getInArchive(archiveFile);
        ISimpleInArchive simpleInterface = inArchive.getSimpleInterface();
        try {
            ISimpleInArchiveItem[] archiveItems = simpleInterface.getArchiveItems();
            System.out.println(archiveItems.length);
            if (archiveItems != null && archiveItems.length > 0) {
                for (int i = 0; i < archiveItems.length; i++) {
                    ISimpleInArchiveItem archiveItem = archiveItems[i];
                    String path = archiveItem.getPath();
                    System.out.println(path);
                }
            }
        } catch (SevenZipException e) {
            e.printStackTrace();
        }

    }


}
