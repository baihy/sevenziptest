package com.zskj.utils;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;

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


    /**
     * 获取归档文件中的文件个数
     *
     * @param archiveFile
     * @return
     */
    public static Integer getNumberOfItemsInArchive(String archiveFile) throws Exception {
        RandomAccessFile randomAccessFile = new RandomAccessFile(archiveFile, "r");
        RandomAccessFileInStream randomAccessFileInStream = new RandomAccessFileInStream(randomAccessFile);
        IInArchive iInArchive = SevenZip.openInArchive(ArchiveFormat.ZIP, randomAccessFileInStream);
        int numberOfItems = iInArchive.getNumberOfItems();
        randomAccessFileInStream.close();
        iInArchive.close();
        return numberOfItems;
    }


}
