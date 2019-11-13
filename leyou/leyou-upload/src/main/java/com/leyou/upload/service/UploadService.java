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
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
public class UploadService {
    private static final List<String> CONTENT_TYPES = Arrays.asList("image/gif","image/jpeg");

    private static final Logger LOGGER = LoggerFactory.getLogger(UploadService.class);

    @Autowired
    private FastFileStorageClient StorageClient;

    public String uploadImage(MultipartFile file) {

        //校验文件类型
        String filename = file.getOriginalFilename();

        String contentType = file.getContentType();
        if (!CONTENT_TYPES.contains(contentType)){
            LOGGER.info("文件类型不合法:{}",filename);
            return null;
        }
        // 校验文件内容

        try {
            BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
            if (bufferedImage == null){
                LOGGER.info("文件内容不合法:{}",filename);
                return null;
            }

            // 保存到服务器

            //file.transferTo(new File("F:\\TestDevelop\\image"+filename));
            String ext = StringUtils.substringAfterLast(filename, ".");
            StorePath storePath = this.StorageClient.uploadFile(file.getInputStream(), file.getSize(), ext, null);

            return "http://image.leyou.com/"+storePath.getFullPath();
            //返回url 进行回显
            //return "http://leyou.image.com/"+filename;
        } catch (IOException e) {
            LOGGER.error("服务器内部错误{}", filename);
            e.printStackTrace();
        }
        return null;
    }
}
