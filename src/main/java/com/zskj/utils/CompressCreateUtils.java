package com.zskj.utils;

import net.sf.sevenzipjbinding.*;
import net.sf.sevenzipjbinding.impl.OutItemFactory;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;
import net.sf.sevenzipjbinding.impl.RandomAccessFileOutStream;
import net.sf.sevenzipjbinding.util.ByteArrayStream;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName: sevenziptest
 * @packageName: com.zskj.utils
 * @Description: 新建压缩包
 * @author: huayang.bai
 * @date: 2018-12-14 16:06
 */
public class CompressCreateUtils {


    private CompressCreateUtils() {
    }


    public static void main(String[] args) throws Exception {
        compressFile();
        System.out.println("操作完成！！");
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


    public static void createArchive(SingleItem item) throws Exception {
        RandomAccessFile randomAccessFile = new RandomAccessFile("/Users/baihuayang/Downloads/aaa/def.zip", "rw");
        IOutCreateArchiveZip iArchiveZip = SevenZip.openOutArchiveZip();
        // 设置压缩级别
        iArchiveZip.setLevel(5);
        iArchiveZip.createArchive(new RandomAccessFileOutStream(randomAccessFile), 1, new AbstractIOutCreateCallback<IOutItemZip>() {
            @Override
            public IOutItemZip getItemInformation(int index, OutItemFactory<IOutItemZip> outItemFactory) throws SevenZipException {
                System.out.println("索引值是：" + index);
                IOutItemZip outItem = outItemFactory.createOutItem();
                outItem.setDataSize((long) item.getContent().length);
                // 设置文件名字
                outItem.setPropertyPath(item.getName());
                return outItem;
            }

            @Override
            public ISequentialInStream getStream(int index) throws SevenZipException {
                System.out.println("索引值是：" + index);
                return new ByteArrayStream(item.getContent(), true);
            }
        });
        iArchiveZip.close();
        randomAccessFile.close();
    }

    public static void compressZipFile() throws Exception {
        List<Item> items = getItems();
        RandomAccessFile randomAccessFile = new RandomAccessFile("/Users/baihuayang/Downloads/aaa/def.zip", "rw");
        IOutCreateArchiveZip iArchiveZip = SevenZip.openOutArchiveZip();
        // 设置压缩级别
        iArchiveZip.setLevel(5);
        iArchiveZip.createArchive(new RandomAccessFileOutStream(randomAccessFile), items.size(), new AbstractIOutCreateCallback<IOutItemZip>() {
            @Override
            public IOutItemZip getItemInformation(int index, OutItemFactory<IOutItemZip> outItemFactory) throws SevenZipException {
                int attr = PropID.AttributesBitMask.FILE_ATTRIBUTE_UNIX_EXTENSION;
                IOutItemZip outItem = outItemFactory.createOutItem();
                File file = items.get(index).getFile();
                if (file.isFile()) {
                    outItem.setDataSize(file.length());
                    attr |= 0x81a4 << 16; // permissions: -rw-r--r--
                }
                if (file.isDirectory()) {
                    outItem.setPropertyIsDir(true);
                    attr |= PropID.AttributesBitMask.FILE_ATTRIBUTE_DIRECTORY;
                    attr |= 0x81ED << 16; // permissions: drwxr-xr-x
                }
                outItem.setPropertyPath(items.get(index).getName());
                outItem.setPropertyAttributes(attr);
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
        iArchiveZip.close();
        randomAccessFile.close();
    }

    public static List<Item> getItems() {
        List<Item> items = new ArrayList<>();
        items.add(new Item("abc/联网审计实施专项激励制度.docx", new File("/Users/baihuayang/Downloads/aaa/联网审计实施专项激励制度.docx")));
        items.add(new Item("abc/金审三期北京出差专项激励制度.docx", new File("/Users/baihuayang/Downloads/aaa/金审三期北京出差专项激励制度.docx")));
        return items;
    }


    public static void compress7zFile() throws Exception {
        List<Item> items = getItems();
        RandomAccessFile randomAccessFile = new RandomAccessFile("/Users/baihuayang/Downloads/aaa/def.7z", "rw");
        IOutCreateArchive7z iArchive7z = SevenZip.openOutArchive7z();
        // 设置压缩级别
        iArchive7z.setLevel(5);
        iArchive7z.setSolid(true);
        iArchive7z.createArchive(new RandomAccessFileOutStream(randomAccessFile), items.size(), new AbstractIOutCreateCallback<IOutItem7z>() {
            /**
             * 注意：特定格式7z的压缩和zip的压缩getItemInformation()方法的实现不同，7z不需要相对复杂的属性属性计算，提供了一个很好的默认行为。
             * @param index
             * @param outItemFactory
             * @return
             * @throws SevenZipException
             */
            @Override
            public IOutItem7z getItemInformation(int index, OutItemFactory<IOutItem7z> outItemFactory) throws SevenZipException {
                IOutItem7z outItem = outItemFactory.createOutItem();
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
        iArchive7z.close();
        randomAccessFile.close();
    }

    /**
     * 使用通用的API实现压缩
     *
     * @throws Exception
     */
    public static void compressFile() throws Exception {
        List<Item> items = getItems();
        RandomAccessFile randomAccessFile = new RandomAccessFile("/Users/baihuayang/Downloads/aaa/def.zip", "rw");
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

        iArchive.createArchive(new RandomAccessFileOutStream(randomAccessFile), items.size(), new AbstractIOutCreateCallback<IOutItemAllFormats>() {
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
