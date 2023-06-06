package cn.bravedawn.web.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
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
     *
     * @param inputStream 输入流
     * @param filePath    文件存放的路径（包含文件名）
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
     *
     * @param fileName 文件名称
     * @return
     */
    public static String getFileSuffix(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }

    /**
     * 获取文件名称（不包含后缀名）
     *
     * @param fileName 文件名称
     * @return
     */
    public static String getFileTitle(String fileName) {
        return fileName.substring(0, fileName.lastIndexOf("."));
    }


    public static void downloadWithJavaNIO(String fileURL, String localFilename) throws IOException {
        int CONNECT_TIMEOUT = 10000;
        int READ_TIMEOUT = 10000;
        try {
            org.apache.commons.io.FileUtils.copyURLToFile(new URL(fileURL), new File(localFilename), CONNECT_TIMEOUT, READ_TIMEOUT);
        } catch (IOException e) {
            log.error("下载文件超时", e);
        }
    }


    public static void main(String[] args) throws IOException {
        downloadWithJavaNIO("https://raw.githubusercontent.com/depers/jasper-db/main/assert/final%E5%BC%95%E5%8F%91%E7%9A%84%E9%94%99%E8%AF%AF.png",
                "/Users/depers/Desktop/1.png");
    }
}
