package com.zskj.utils;

import net.sf.sevenzipjbinding.*;
import net.sf.sevenzipjbinding.impl.OutItemFactory;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;
import net.sf.sevenzipjbinding.impl.RandomAccessFileOutStream;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName: sevenziptest
 * @packageName: com.zskj.utils
 * @Description: 新建压缩包
 * @author: huayang.bai
 * @date: 2018-12-14 16:06
 */
public class CompressTracingUtils {


    private CompressTracingUtils() {
    }


    public static void main(String[] args) throws Exception {
        compressFile();
        System.out.println("操作完成！！");
    }


    public static List<Item> getItems() {
        List<Item> items = new ArrayList<>();
        items.add(new Item("abc/联网审计实施专项激励制度.docx", new File("/Users/baihuayang/Downloads/aaa/联网审计实施专项激励制度.docx")));
        items.add(new Item("abc/金审三期北京出差专项激励制度.docx", new File("/Users/baihuayang/Downloads/aaa/金审三期北京出差专项激励制度.docx")));
        return items;
    }

    /**
     * 使用通用的API实现压缩
     *
     * @throws Exception
     */
    public static void compressFile() throws Exception {
        List<Item> items = getItems();
        RandomAccessFile randomAccessFile = new RandomAccessFile("/Users/baihuayang/Downloads/aaa/aaa.zip", "rw");
        ArchiveFormat archiveFormat = ArchiveFormat.ZIP;
        IOutCreateArchive<IOutItemAllFormats> iArchive = SevenZip.openOutArchive(archiveFormat);
        // 设置默认的压缩级别
        if (iArchive instanceof IOutFeatureSetLevel) {
            ((IOutFeatureSetLevel) iArchive).setLevel(5);
        }
        // 设置压缩的过程中，使用最大使用多少线程，默认是是-1
        if (iArchive instanceof IOutFeatureSetMultithreading) {
            ((IOutFeatureSetMultithreading) iArchive).setThreadCount(2);
        }
        //激活跟踪
        iArchive.setTrace(true);
        iArchive.createArchive(new RandomAccessFileOutStream(randomAccessFile), items.size(), new IOutCreateCallback<IOutItemAllFormats>() {

            private long total;

            @Override
            public void setOperationResult(boolean operationResultOk) throws SevenZipException {
                System.out.println("压缩结果:" + operationResultOk);
            }

            @Override
            public void setTotal(long total) throws SevenZipException {
                this.total = total;
            }

            @Override
            public void setCompleted(long complete) throws SevenZipException {
                Double completed = (complete + 0.0D) / this.total;
                System.out.println("completed:" + completed + ":" + new DecimalFormat("0.00").format(completed));
            }

            /**
             * 注意：特定格式7z的压缩和zip的压缩getItemInformation()方法的实现不同，7z不需要相对复杂的属性属性计算，提供了一个很好的默认行为。
             * @param index
             * @param outItemFactory
             * @return
             * @throws SevenZipException
             */
            @Override
            public IOutItemAllFormats getItemInformation(int index, OutItemFactory<IOutItemAllFormats> outItemFactory) throws SevenZipException {
                IOutItemAllFormats outItem = outItemFactory.createOutItem();
                File file = items.get(index).getFile();
                if (file.isFile()) {
                    outItem.setDataSize(file.length());
                }
                if (file.isDirectory()) {
                    outItem.setPropertyIsDir(true);
                }
                outItem.setPropertyPath(items.get(index).getName());
                return outItem;
            }

            @Override
            public ISequentialInStream getStream(int index) throws SevenZipException {
                RandomAccessFile file = null;
                try {
                    file = new RandomAccessFile(items.get(index).getFile(), "rw");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                return new RandomAccessFileInStream(file);
            }
        });
        iArchive.close();
        randomAccessFile.close();
    }

}
