package com.irsec.harbour.ship.utils;

import cn.hutool.http.HttpUtil;
import com.irsec.harbour.ship.data.bean.UploadResultBean;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.core.io.InputStreamResource;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;


/**
 * @Auther: Jethro
 * @Date: 2019/9/23 09:19
 * @Description:
 */
@Slf4j
public class GoFastUtils {

    public static UploadResultBean uploadImgOkHttp(byte[] isr, String uploadImgUrl) {
        if (isr == null) {
            return null;
        }
        String md5 = getMD5(isr);
        UploadResultBean uploadBean = null;
        try {

            OkHttpClient httpClient = new OkHttpClient();
            MultipartBody multipartBody = new MultipartBody.Builder().
                    setType(MultipartBody.FORM)
                    .addFormDataPart("file", UUIDTool.newUUID() + ".jpg",
                            okhttp3.RequestBody.create(MediaType.parse("multipart/form-data;charset=utf-8"),
                                    isr))
                    .addFormDataPart("output", "json")
                    .addFormDataPart("path", "xmship")
                    .addFormDataPart("scene", "face")
                    .addFormDataPart("md5",md5)
                    .build();

            Request request = new Request.Builder()
                    .url(uploadImgUrl + "/upload")
                    .post(multipartBody)
                    .build();

            Response response = httpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                okhttp3.ResponseBody body = response.body();
                if (body != null) {
                    String strBody = body.string();
                    uploadBean = JsonUtil.JsonToBean(strBody, UploadResultBean.class);
                }
            } else {
                log.error("upload failed,resp ： {}", response.body().string());
            }
        } catch (IOException e) {
            log.error("okhttp 上传文件时出现错误", e);
            return null;
        }
        return uploadBean;
    }

    public static UploadResultBean uploadImgHuTool(byte[] faceImg, String uploadImgUrl) {
        String result = "";
        UploadResultBean uploadResultBean = null;
        String md5 = getMD5(faceImg);
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(faceImg);
            InputStreamResource isr = new InputStreamResource(in, UUIDTool.newUUID() + ".jpg");
            Map<String, Object> params = new HashMap<>();
            params.put("file", isr);
            params.put("md5", md5);
            params.put("path", "xmship");
            params.put("scene", "image");
            params.put("output", "json");

            result = HttpUtil.post(uploadImgUrl + "/upload", params);
            if (!StringUtils.isEmpty(result)) {
                uploadResultBean = JsonUtil.JsonToBean(result, UploadResultBean.class);
            }
        } catch (Exception e) {
            log.error("hutool 上传文件时出现错误", e);
            e.printStackTrace();
        }
        return uploadResultBean;
    }

    public static String getMD5(byte[] file) {
        try {
            MessageDigest MD5 = MessageDigest.getInstance("MD5");

            MD5.update(file, 0, file.length);
            byte[] byteArray = MD5.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : byteArray) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }






}


