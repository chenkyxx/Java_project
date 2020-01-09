package com.irsec.harbour.ship.controller;

import com.irsec.harbour.ship.data.dto.BaseInputDTO;
import com.irsec.harbour.ship.data.dto.OutputDTOBuilder;
import com.irsec.harbour.ship.data.dto.RowOutputDTO;
import com.irsec.harbour.ship.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Auther: Jethro
 * @Date: 2019/9/30 16:35
 * @Description: 文件下载接口
 */

@Controller
@RequestMapping("/api/v2/download")
@Slf4j
public class ResourceController {

    @Value("${download.path}")
    String filePath;

    @RequestMapping("/terminal/{filename}")
    public ResponseEntity<byte[]> down(@PathVariable String filename){
        HttpHeaders headers = new HttpHeaders();
        File file = new File(filePath+File.separator+filename);
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", file.getName());
        byte[] bytes = FileUtils.readFileToByteArray(file);
        return new ResponseEntity<byte[]>(bytes,
                headers, HttpStatus.CREATED);
    }


    @PostMapping("/filelist")
    @ResponseBody
    public ResponseEntity<String> fileList(@RequestBody BaseInputDTO params){
        String reqId = params.getReqId();
        OutputDTOBuilder responseBuilder = new OutputDTOBuilder(reqId);

        try{
            RowOutputDTO rowOutputDTO = new RowOutputDTO();
            List<File> fileList = FileUtils.loopFile(filePath);
            List<String> fileNames = new ArrayList<>();
            if(fileList != null ){
                for(File file : fileList){
                    fileNames.add(file.getName());
                }
            }
            rowOutputDTO.setData(fileNames);
            return responseBuilder.OK(rowOutputDTO);
        }catch (Exception e){
            log.error("获取文件列表时，出现错误",e);
            return responseBuilder.Error("获取文件列表时，出现错误");
        }

    }

    @PostMapping("/updateFile")
    public ResponseEntity<String> singleFileUpload(MultipartFile file) {
        //log.debug("传入的文件参数：{}", JSON.toJSONString(file, true));
        String reqId = System.currentTimeMillis()+"";
        OutputDTOBuilder responseBuilder = new OutputDTOBuilder(reqId);
        if (Objects.isNull(file) || file.isEmpty()) {
            log.error("文件为空");
            return responseBuilder.Error("上传文件为空");
        }
        String oldfileName = null;
        String backFilePath = filePath+"back";
        File oldfile = null;
        String fileName = null;

        try {
            fileName = file.getOriginalFilename();
            if(fileName.contains("MD5") || fileName.contains("VER")){
                List<File> fileList = FileUtils.loopFile(filePath);
                if(fileList != null  && fileList.size() != 0){
                    for(File f : fileList){
                        if(f.getName().contains("MD5") && fileName.contains("MD5")){
                            oldfile = f;
                            oldfileName = f.getName();
                            break;
                        }else if(f.getName().contains("VER") && fileName.contains("VER")){
                            oldfile = f;
                            oldfileName = f.getName();
                            break;
                        }
                    }
                    fileList.clear();
                }else{
                    oldfile = new File(filePath+File.separator+fileName);
                    oldfileName = fileName;
                }
                fileList = null;
            }else {
                oldfile = new File(filePath+File.separator+fileName);
            }
            //备份
            if(oldfile.exists()){
                //检查备份文件夹是否存在，不存在则创建
                File backpath = new File(backFilePath);
                if (!backpath.exists()) {
                    backpath.mkdirs();
                }else{
                    //1.删除之前的备份文件，每次更新的文件名是一样的，所以使用路径+名字则可定位文件
                    //MD5和VER名字不一样，需要重新获取
                    File oldBackFile = null;
                    if(fileName.contains("MD5") || fileName.contains("VER")){
                        List<File> fileList = FileUtils.loopFile(backFilePath);
                        if(fileList != null  && fileList.size() != 0){
                            //如果不等于空则遍历找到需要的文件
                            for(File f : fileList) {
                                if(f.getName().contains("MD5") && fileName.contains("MD5")){
                                    oldBackFile = f;
                                    break;
                                }else if(f.getName().contains("VER") && fileName.contains("VER")){
                                    oldBackFile = f;
                                    break;
                                }
                            }
                        }
                    }else{
                        oldBackFile = new File(backFilePath+File.separator+fileName);
                    }

                    if(oldBackFile != null) {
                        if (oldBackFile.exists()) {
                            log.info("删除备份文件夹中的文件，文件：{}", oldBackFile.getAbsolutePath());
                            oldBackFile.delete();
                        }
                    }
                        //2.先将原来的文件进行备份
                        backUpNewFile(oldfile.getAbsolutePath(), backFilePath+File.separator+oldfile.getName());
                        //3.再删除原来的文件文件
                        File newBackfile=new File(backFilePath+File.separator+oldfile.getName());
                        if(newBackfile.exists()){
                            //如果新备份的文件写入成功，则删除原来的文件
                            log.info("旧文件备份成功，旧文件路径：{} -> 新文件路径: {}", filePath + File.separator+oldfile.getName(), backFilePath+File.separator+oldfile.getName());
                            oldfile.delete();
                            log.info("删除旧的文件，文件：{}", oldfile.getAbsolutePath());
                        }else{
                            backUpNewFile(filePath+File.separator+fileName, backFilePath+File.separator+fileName);
                            oldfile.delete();
                        }
                }
            }
            //4.写入新文件
            byte[] bytes = file.getBytes();
            Path path = Paths.get(filePath+File.separator + fileName);
            //如果没有files文件夹，则创建
            if (!Files.isWritable(path)) {
                Files.createDirectories(Paths.get(filePath));
            }
            //文件写入指定路径
            Files.write(path, bytes);
            log.info("文件:{} 写入成功.",fileName);
            return responseBuilder.OK("文件:"+fileName+" 写入成功");
        } catch (Exception e) {
            e.printStackTrace();
            return responseBuilder.Error("上传失败");
        }
    }

    private void backUpNewFile(String s, String s1) throws FileNotFoundException,IOException {
        FileInputStream fis = new FileInputStream(s);
        FileOutputStream fos = new FileOutputStream(s1);
        byte[] lsy=new byte[fis.available()];
        fis.read(lsy);
        fos.write(lsy);
        fos.close();
        fis.close();
    }


}
