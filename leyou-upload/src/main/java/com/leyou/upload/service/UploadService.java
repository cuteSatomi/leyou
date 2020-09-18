package com.leyou.upload.service;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
public class UploadService {

    // 定义白名单，只要contentType在此集合中就是合法的
    private static final List<String> CONTENT_TYPES = Arrays.asList("image/gif", "image/jpeg");

    // 引入酸辣粉logger
    private static final Logger LOGGER = LoggerFactory.getLogger(UploadService.class);

    @Autowired
    private FastFileStorageClient storageClient;

    public String uploadImage(MultipartFile file) {

        // 获取文件的名称
        String originalFilename = file.getOriginalFilename();

        // 校验文件类型
        // 获取contentType
        String contentType = file.getContentType();
        if (!CONTENT_TYPES.contains(contentType)) {
            // contentType不在白名单中，即参数不合法返回null
            LOGGER.info("文件类型不合法: {}", originalFilename);
            return null;
        }

        try {
            // 校验文件内容
            BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
            if (bufferedImage == null) {
                LOGGER.info("文件内容不合法: {}", originalFilename);
                return null;
            }

            // 保存到文件服务器，F盘的image文件夹下
            // String ext = StringUtils.substringAfterLast(originalFilename, "."); fdfs的上传，有问题暂时注释掉
            // StorePath storePath = this.storageClient.uploadFile(file.getInputStream(), file.getSize(), ext, null);
            file.transferTo(new File("F:\\image\\" + originalFilename));

            // 返回保存成功的url，进行回显
            // 文件服务器名称加上文件名
            // return "http://image.leyou.com/" + storePath.getFullPath();
            return "http://image.leyou.com/" + originalFilename;
        } catch (IOException e) {
            LOGGER.info("服务器内部错误: {}", originalFilename);
            e.printStackTrace();
        }
        // 出现异常返回null
        return null;
    }
}
