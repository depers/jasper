package cn.bravedawn.web.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author : depers
 * @program : jasper
 * @description:
 * @date : Created in 2023/5/25 11:47
 */
public class FileUtils {


    private static final Logger log = LoggerFactory.getLogger(FileUtils.class);


    /**
     * 将文件保存到本地
     * @param inputStream 输入流
     * @param filePath 文件存放的路径（包含文件名）
     */
    public static void storeFile(InputStream inputStream, String filePath) {
        try {
            Files.write(Path.of(filePath), inputStream.readAllBytes());
        } catch (IOException e) {
            log.error("本地存储文件失败", e);

        }
    }


    /**
     * 获取文件的后缀名
     * @param fileName 文件名称
     * @return
     */
    public static String getFileSuffix(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }

    /**
     * 获取文件名称（不包含后缀名）
     * @param fileName 文件名称
     * @return
     */
    public static String getFileTitle(String fileName) {
        return fileName.substring(0, fileName.lastIndexOf("."));
    }


    public static void downloadWithJavaNIO(String fileURL, String localFilename) throws IOException {
        URL url = new URL(fileURL);
        try (ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(localFilename);
             FileChannel fileChannel = fileOutputStream.getChannel()) {

            fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
            fileOutputStream.close();
        }
    }
}
