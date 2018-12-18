package com.zskj.utils;

import net.sf.sevenzipjbinding.*;
import net.sf.sevenzipjbinding.impl.OutItemFactory;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;
import net.sf.sevenzipjbinding.impl.RandomAccessFileOutStream;
import net.sf.sevenzipjbinding.simple.ISimpleInArchive;
import net.sf.sevenzipjbinding.simple.ISimpleInArchiveItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ProjectName: sevenziptest
 * @packageName: com.zskj.utils
 * @Description: 使用sevenzip实现文件的压缩和解压缩
 * @author: huayang.bai
 * @date: 2018-12-18 14:29
 */
public class CompressUtils {


    private final static Logger LOGGER = LoggerFactory.getLogger(CompressUtils.class);


    static {
        sevenZipJBindingInitCheck();
    }

    /**
     * 私有化构造方法
     */
    private CompressUtils() {
    }

    public static void main(String[] args) {
        String sourceFilePath = "/Users/baihuayang/Downloads/abc.zip";
        String targetFilePath = "/Users/baihuayang/Downloads/dd";
        uncompress(sourceFilePath, targetFilePath);
    }

    /**
     * 检验压缩环境
     */
    public static void sevenZipJBindingInitCheck() {
        try {
            SevenZip.initSevenZipFromPlatformJAR();
            LOGGER.info("SevenZip压缩环境正常！！！");
        } catch (SevenZipNativeInitializationException e) {
            LOGGER.error("SevenZip压缩环境异常,异常信息是：{}", e.getMessage());
            e.printStackTrace();
        }
    }


    /**
     * 解压
     *
     * @param sourceFile
     * @param targetFile
     * @return
     */
    public static boolean uncompress(File sourceFile, File targetFile) {
        boolean result = false;
        if (checkSourceFile(sourceFile) && checkTargetFile(targetFile)) {
            try (RandomAccessFile randomAccessFile = new RandomAccessFile(sourceFile, "rw");
                 RandomAccessFileInStream fis = new RandomAccessFileInStream(randomAccessFile);
                 IInArchive iInArchive = SevenZip.openInArchive(null, fis)) {
                ISimpleInArchive simpleInterface = iInArchive.getSimpleInterface();
                ISimpleInArchiveItem[] archiveItems = simpleInterface.getArchiveItems();
                for (int i = 0; i < archiveItems.length; i++) {
                    ISimpleInArchiveItem archiveItem = archiveItems[i];
                    String filePath = targetFile.getAbsolutePath() + File.separator + archiveItem.getPath();
                    File itemFile = new File(filePath);
                    if (!itemFile.getParentFile().exists()) {
                        itemFile.getParentFile().mkdirs();
                    }
                    RandomAccessFile file = new RandomAccessFile(itemFile, "rw");
                    RandomAccessFileOutStream outStream = new RandomAccessFileOutStream(file);
                    archiveItem.extractSlow(outStream);
                    file.close();
                    outStream.close();
                }
                LOGGER.info("解压成功！");
                result = true;
            } catch (Exception e) {
                LOGGER.error("解压{}文件发生异常:{}", sourceFile.getAbsolutePath(), e.getMessage());
            }
        }
        return result;
    }

    /**
     * 解压操作
     *
     * @param sourceFilePath
     * @param targetFilePath
     * @return
     */
    public static boolean uncompress(String sourceFilePath, String targetFilePath) {
        return uncompress(new File(sourceFilePath), new File(targetFilePath));
    }


    /**
     * 压缩成zip文件
     *
     * @param sourceFile
     * @param targetFile
     * @return
     * @throws Exception
     */
    public static boolean compressZip(File sourceFile, File targetFile) {
        return compress(sourceFile, targetFile, ArchiveFormat.ZIP);
    }

    /**
     * 压缩成zip文件
     *
     * @param sourceFilePath
     * @param targetFilePath
     * @return
     * @throws Exception
     */
    public static boolean compressZip(String sourceFilePath, String targetFilePath) {
        return compress(new File(sourceFilePath), new File(targetFilePath), ArchiveFormat.ZIP);
    }


    /**
     * 压缩成7z文件
     *
     * @param sourceFile
     * @param targetFile
     * @return
     * @throws Exception
     */
    public static boolean compress7z(File sourceFile, File targetFile) {
        return compress(sourceFile, targetFile, ArchiveFormat.SEVEN_ZIP);
    }

    /**
     * 压缩成7z文件
     *
     * @param sourceFilePath
     * @param targetFilePath
     * @return
     * @throws Exception
     */
    public static boolean compress7z(String sourceFilePath, String targetFilePath) {
        return compress(new File(sourceFilePath), new File(targetFilePath), ArchiveFormat.SEVEN_ZIP);
    }

    /**
     * 压缩成指定格式的文件
     *
     * @param sourceFile
     * @param targetFile
     * @param archiveFormat
     * @return
     * @throws Exception
     */
    private static boolean compress(File sourceFile, File targetFile, ArchiveFormat archiveFormat) {
        boolean result = false;
        if (checkTargetFile(targetFile, archiveFormat)) {
            List<Item> items = getItems(sourceFile);
            try (IOutCreateArchive<IOutItemAllFormats> iArchive = SevenZip.openOutArchive(archiveFormat);
                 RandomAccessFile randomAccessFile = new RandomAccessFile(targetFile, "rw")
            ) {
                RandomAccessFileOutStream fos = new RandomAccessFileOutStream(randomAccessFile);
                // 设置默认的压缩级别
                if (iArchive instanceof IOutFeatureSetLevel) {
                    ((IOutFeatureSetLevel) iArchive).setLevel(3);
                }
                // 设置压缩的过程中，使用最大使用多少线程，默认是是-1,0表示：将线程计数与可用处理器的计数相匹配
                if (iArchive instanceof IOutFeatureSetMultithreading) {
                    ((IOutFeatureSetMultithreading) iArchive).setThreadCount(0);
                }
                iArchive.createArchive(fos, items.size(), new IOutCreateCallback<IOutItemAllFormats>() {
                    @Override
                    public void setTotal(long total) throws SevenZipException {
                    }

                    @Override
                    public void setCompleted(long complete) throws SevenZipException {
                    }

                    @Override
                    public void setOperationResult(boolean operationResultOk) throws SevenZipException {
                    }

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
                        outItem.setPropertyPath(items.get(index).getPath());
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
                result = true;
                LOGGER.info("压缩成功！！！");
                fos.close();
            } catch (Exception e) {
                LOGGER.error("文件{}压缩失败：{}", sourceFile.getAbsolutePath(), e.getMessage());
                e.printStackTrace();
            }
        }
        return result;
    }


    /**
     * 检车源文件格式是否正确
     *
     * @param sourceFile
     * @return
     */
    private static boolean checkSourceFile(File sourceFile) {
        String absolutePath = sourceFile.getAbsolutePath();
        String suffix = absolutePath.substring(absolutePath.lastIndexOf(".") + 1);
        List<ArchiveFormat> archiveFormatsList = Arrays.asList(ArchiveFormat.values());
        List<String> suffixList = archiveFormatsList.stream().map((archiveFormat) ->
                archiveFormat.getMethodName().toUpperCase()
        ).collect(Collectors.toList());
        if (suffixList.contains(suffix.toUpperCase())) {
            return true;
        }
        LOGGER.error("源文件类型不正确:{}", absolutePath);
        return false;
    }

    /**
     * 判断目标文件夹是否存在
     *
     * @param targetFile
     * @return
     */
    private static boolean checkTargetFile(File targetFile) {
        if (!targetFile.exists()) {
            LOGGER.error("目标文件夹不存在:{}", targetFile.getAbsolutePath());
            return false;
        }
        if (targetFile.isFile()) {
            LOGGER.error("目标不能是文件:{}", targetFile.getAbsolutePath());
            return false;
        }
        return true;
    }

    /**
     * 检查目标文件
     *
     * @param targetFile
     * @return
     */
    private static boolean checkTargetFile(File targetFile, ArchiveFormat archiveFormat) {
        if (targetFile.isDirectory()) {
            LOGGER.error("压缩之后的文件不能是文件夹：{}", targetFile.getAbsolutePath());
            return false;
        }
        String absolutePath = targetFile.getAbsolutePath();
        String suffix = absolutePath.substring(absolutePath.lastIndexOf(".") + 1);
        if (!archiveFormat.getMethodName().equalsIgnoreCase(suffix)) {
            LOGGER.error("对不起！目标文件格式{}和压缩格式{}不匹配！！！", suffix, archiveFormat.name());
            return false;
        }
        File parentFile = targetFile.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        return true;
    }

    /**
     * 获取源路径下的所有item
     *
     * @param sourceFile
     * @return
     */
    private static List<Item> getItems(File sourceFile) {
        List<Item> items = new ArrayList<>(0);
        if (sourceFile.isFile()) {
            /***源文件是一个文件***/
            items.add(new Item(sourceFile.getName(), sourceFile));
        }
        if (sourceFile.isDirectory()) {
            /***源文件是一个文件夹***/
            initItems(items, sourceFile, sourceFile.getAbsolutePath());
        }
        return items;
    }

    /**
     * 递归调用初始化Items
     *
     * @param items
     * @param sourceFile
     * @param basePath
     */
    private static void initItems(List<Item> items, File sourceFile, String basePath) {
        /****过滤掉隐藏文件*****/
        File[] files = sourceFile.listFiles((dir, name) -> !name.startsWith("."));
        if (files != null && files.length > 0) {
            for (int i = 0; i < files.length; i++) {
                if (files[i].isFile()) {
                    String absolutePath = files[i].getAbsolutePath();
                    String fileName = absolutePath.substring(absolutePath.indexOf(basePath) + basePath.length() + 1);
                    items.add(new Item(fileName, files[i]));
                } else {
                    initItems(items, files[i], basePath);
                }
            }
        }
    }


    private static class Item {
        /**
         * 被压缩的文件或文件夹在压缩包的中路径名
         */
        private String path;
        /**
         * 被压缩的文件或文件夹
         */
        private File file;

        public Item() {
        }

        public Item(String path, File file) {
            this.path = path;
            this.file = file;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
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
                    "path='" + path + '\'' +
                    ", file=" + file.getAbsolutePath() +
                    '}';
        }
    }

}
