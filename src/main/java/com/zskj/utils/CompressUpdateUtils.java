package com.zskj.utils;

import net.sf.sevenzipjbinding.*;
import net.sf.sevenzipjbinding.impl.OutItemFactory;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;
import net.sf.sevenzipjbinding.impl.RandomAccessFileOutStream;
import net.sf.sevenzipjbinding.simple.ISimpleInArchive;
import net.sf.sevenzipjbinding.simple.ISimpleInArchiveItem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName: sevenziptest
 * @packageName: com.zskj.utils
 * @Description: 更新压缩包
 * @author: huayang.bai
 * @date: 2018-12-17 14:35
 */
public class CompressUpdateUtils {

    private boolean mkdirs;

    private CompressUpdateUtils() {

    }

    public static void main(String[] args) {
        try {
            getItemCompressFile();
            System.out.println("操作完成");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static List<Item> getItems() {
        List<Item> items = new ArrayList<>();
        items.add(new Item("abc/联网审计实施专项激励制度.docx", new File("/Users/baihuayang/Downloads/aaa/联网审计实施专项激励制度.docx")));
        items.add(new Item("abc/金审三期北京出差专项激励制度111.docx", new File("/Users/baihuayang/Downloads/aaa/金审三期北京出差专项激励制度.docx")));
        return items;
    }


    public static void compressUpdateFile() throws Exception {
        List<Item> items = getItems();

        RandomAccessFile randomAccessFile = new RandomAccessFile("/Users/baihuayang/Downloads/aaa/def.zip", "rw");
        IInStream iis = new RandomAccessFileInStream(randomAccessFile);
        IInArchive iInArchive = SevenZip.openInArchive(null, iis);
        IOutUpdateArchive<IOutItemAllFormats> outArchive = iInArchive.getConnectedOutArchive();
        outArchive.updateItems(new RandomAccessFileOutStream(randomAccessFile), items.size(), new AbstractIOutCreateCallback<IOutItemAllFormats>() {

            /**
             * 更改时，更改指定的文件
             * @param index
             * @param outItemFactory
             * @return
             * @throws SevenZipException
             */
            @Override
            public IOutItemAllFormats getItemInformation(int index, OutItemFactory<IOutItemAllFormats> outItemFactory) throws SevenZipException {
                // 修改索引为1的文件，除了指定修改的文件其他的属性不变
                if (index != 1) {
                    return outItemFactory.createOutItem(index);
                }

                IOutItemAllFormats outItem = outItemFactory.createOutItemAndCloneProperties(index);
                // 更改属性path
                outItem.setUpdateIsNewProperties(true);
                outItem.setPropertyPath(items.get(index).getName());
                // 更改数据
                outItem.setUpdateIsNewData(true);
                outItem.setDataSize(items.get(index).getFile().length());
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
    }


    static int indexAdd = -1;

    public static void compressAddFile() throws Exception {

        Item item = new Item("abc/add.docx", new File("/Users/baihuayang/Downloads/aaa/金审三期北京出差专项激励制度.docx"));

        RandomAccessFile randomAccessFile = new RandomAccessFile("/Users/baihuayang/Downloads/aaa/def.zip", "rw");
        IInStream iis = new RandomAccessFileInStream(randomAccessFile);
        IInArchive iInArchive = SevenZip.openInArchive(null, iis);
        // 把新增加的文件放到最后
        indexAdd = iInArchive.getNumberOfItems();
        IOutUpdateArchive<IOutItemAllFormats> outArchive = iInArchive.getConnectedOutArchive();
        // 把新增加的文件放到最后
        outArchive.updateItems(new RandomAccessFileOutStream(randomAccessFile), iInArchive.getNumberOfItems() + 1, new AbstractIOutCreateCallback<IOutItemAllFormats>() {
            /**
             * 更改时，更改指定的文件
             * @param index
             * @param outItemFactory
             * @return
             * @throws SevenZipException
             */
            @Override
            public IOutItemAllFormats getItemInformation(int index, OutItemFactory<IOutItemAllFormats> outItemFactory) throws SevenZipException {
                // 修改索引为1的文件，除了指定修改的文件其他的属性不变
                if (index == indexAdd) {
                    //添加新项
                    IOutItemAllFormats outItem = outItemFactory.createOutItem();
                    outItem.setPropertyPath(item.getName());
                    outItem.setDataSize(item.getFile().length());
                    return outItem;
                }
                return outItemFactory.createOutItem(index);
            }

            @Override
            public ISequentialInStream getStream(int index) throws SevenZipException {
                RandomAccessFile file = null;
                try {
                    file = new RandomAccessFile(item.getFile(), "rw");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                return new RandomAccessFileInStream(file);
            }
        });
    }

    static int removeIndex = 1;

    public static void compressRemoveFile() throws Exception {

        RandomAccessFile randomAccessFile = new RandomAccessFile("/Users/baihuayang/Downloads/aaa/def.zip", "rw");
        IInStream iis = new RandomAccessFileInStream(randomAccessFile);
        IInArchive iInArchive = SevenZip.openInArchive(null, iis);
        IOutUpdateArchive<IOutItemAllFormats> outArchive = iInArchive.getConnectedOutArchive();
        // 把新增加的文件放到最后
        outArchive.updateItems(new RandomAccessFileOutStream(randomAccessFile), iInArchive.getNumberOfItems() - 1, new AbstractIOutCreateCallback<IOutItemAllFormats>() {
            /**
             * 更改时，更改指定的文件
             * @param index
             * @param outItemFactory
             * @return
             * @throws SevenZipException
             */
            @Override
            public IOutItemAllFormats getItemInformation(int index, OutItemFactory<IOutItemAllFormats> outItemFactory) throws SevenZipException {
                // 修改索引为1的文件，除了指定修改的文件其他的属性不变
                outItemFactory.createOutItem(index);
                if (index == removeIndex) {
                    IOutItemAllFormats outItem = outItemFactory.createOutItem();
                    outItem.setDataSize(null);
                }
                if (index < removeIndex) {
                    return outItemFactory.createOutItem(index);
                }
                return outItemFactory.createOutItem(index + 1);
            }

            @Override
            public ISequentialInStream getStream(int index) throws SevenZipException {
                return null;
            }
        });
    }


    public static void getItemCompressFile() throws Exception {
        RandomAccessFile randomAccessFile = new RandomAccessFile("/Users/baihuayang/Downloads/aaa/def.zip", "rw");
        IInStream iis = new RandomAccessFileInStream(randomAccessFile);
        IInArchive iInArchive = SevenZip.openInArchive(null, iis);
        ISimpleInArchive simpleInterface = iInArchive.getSimpleInterface();
        ISimpleInArchiveItem[] archiveItems = simpleInterface.getArchiveItems();

        String baseDir = "/Users/baihuayang/Downloads/bbb/";

        for (int i = 0; i < archiveItems.length; i++) {
            ISimpleInArchiveItem archiveItem = archiveItems[i];
            if (!archiveItem.isFolder()) {
                File f = new File(archiveItem.getPath());
                String fName = f.getName();
                String parent = f.getParent();
                File path = new File(baseDir + parent);
                if (!path.exists()) {
                    path.mkdirs();
                }
                RandomAccessFile file = new RandomAccessFile(baseDir + parent + File.separator + fName, "rw");
                RandomAccessFileOutStream outStream = new RandomAccessFileOutStream(file);
                ExtractOperationResult result = archiveItem.extractSlow(outStream);
                System.out.println(result);
            }
        }
    }
}


