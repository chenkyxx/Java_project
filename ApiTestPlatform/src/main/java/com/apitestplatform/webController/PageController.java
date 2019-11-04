/**
 * Copyright (C), 2018-2019, 重庆智汇航安智能科技研究院有限公司
 * FileName: PageController
 * Author:   Original Dream
 * Date:     2019/9/20 14:15
 * Description:
 */
package com.apitestplatform.webController;


import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.io.*;

@Controller
public class PageController {
    String path = "C:\\chenkeyun\\Tools\\test1000\\idphoto";

    @RequestMapping(value = "/index",method = RequestMethod.GET)
    public String getPage(){
        return "index1.html";
    }

//    @GetMapping("/img/{name}")
//    public String img(HttpServletRequest httpServletRequest,
//                      HttpServletResponse httpServletResponse, @PathVariable("name")String name) throws Exception{
//
//        String path = "C:\\chenkeyun\\Tools\\test1000\\idphoto";
//        File file = new File(path+File.separator+name);
//        FileInputStream fileInputStream  = new FileInputStream(file);
//        FileOutputStream fileOutputStream = new FileOutputStream(file);
//        byte[] img = fileInputStream.;
//        httpServletResponse.setContentType("image/png");
//        OutputStream os = httpServletResponse.getOutputStream();
//        os.write(img);
//        os.flush();
//        os.close();
//        return "success";
//    }


    @RequestMapping(value = "/img/{name}", produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    public ResponseEntity<Resource> showImage(@PathVariable("name")String name) {
        try{
            // 获取文件的下载
            byte[] downloadFile = getImageByte(path+File.separator+name);
            if (downloadFile == null) {
                return ResponseEntity.notFound().build();
            }
            Resource resource = new InputStreamResource(new ByteArrayInputStream(downloadFile));
            // inline表示内嵌的意思，attachment表示附件，提示下载的意思
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"default.jpg\"")
                    .body(resource);
        }catch (Exception e){
            return ResponseEntity.notFound().build();
        }
    }


    public byte[] getImageByte(String filepath) {
        try {

            FileInputStream inputStream = new FileInputStream(filepath);
            int i = inputStream.available();
            //byte数组用于存放图片字节数据
            byte[] buff = new byte[i];
            inputStream.read(buff);
            inputStream.close();
            return buff;
        }
        catch (Exception e){
            System.err.println("图片错误或者不存在");
            return null;
        }
    }
}
