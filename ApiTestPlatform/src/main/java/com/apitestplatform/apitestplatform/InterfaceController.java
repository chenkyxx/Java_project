package com.apitestplatform.apitestplatform;

import org.apache.coyote.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;


import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class InterfaceController {
    private static String realPath = "F:\\travel\\";
    private static Logger log = LoggerFactory.getLogger(InterfaceController.class);
    private static RestTemplate session = new RestTemplate();

    @RequestMapping(value = "/downloadFile",method = RequestMethod.GET)
    public void downLoad(@RequestParam String filePath, HttpServletResponse response, boolean isOnLine)  {
        log.info("开始进入downloadFile下载照片文件接口方法");
        File f = new File(realPath+filePath);
        isOnLine = false;
        try {

            if (!f.exists()) {
                log.error("File not found!");
                response.sendError(404, "File not found!");
                return;
            }
            String fileName = f.getName();
            fileName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");

            BufferedInputStream br = new BufferedInputStream(new FileInputStream(f));
            byte[] buf = new byte[1024];
            int len = 0;
            response.reset(); // 非常重要
            if (isOnLine) { // 在线打开方式
                URL u = new URL("file:///" + realPath+filePath);
                response.setContentType(u.openConnection().getContentType());
                response.setHeader("Content-Disposition", "inline; filename=" + fileName);
                // 文件名应该编码成UTF-8
            } else { // 纯下载方式
                response.setContentType("application/x-msdownload");
                response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
                log.info("start file input stream return");
            }
            OutputStream out = response.getOutputStream();
            while ((len = br.read(buf)) > 0)
                out.write(buf, 0, len);
            br.close();
            out.close();
        }
        catch (Exception e){
            System.err.println(e.toString());
            return;
        }
    }

    @RequestMapping(value = "/getFilenames")
    public ArrayList<String> getListfilenames(){
        log.info("start get pictures name");
        ArrayList<String> list = new ArrayList<>();
        File file = new File("F:\\travel");
        File[] filelist = file.listFiles();
        try {

            for (int i = 0; i < filelist.length;i++){
                if(filelist[i].isFile()){
                    String name = filelist[i].getName();
                    list.add(name);
                }
            }
            return list;
        }catch (Exception e){
            log.error(e.toString()+"发生了异常");
            return null;
        }
    }


    @RequestMapping(value = "/getRealIp", produces = MediaType.ALL_VALUE,
    method = RequestMethod.GET)
    @ResponseBody
    public  String getRealIp()  {
        String url = "http://txt.go.sohu.com/ip/soip";
        String regex = "\\d+.\\d+.\\d+.\\d+";
        String object = session.getForObject(url, String.class);
        Pattern compile = Pattern.compile(regex);
        Matcher matcher = compile.matcher(object);
        while (matcher.find()){
            String ip = matcher.group();
            return ip;
        }
        return null;
    }

    public static void main(String[] args) {

        String url = "http://txt.go.sohu.com/ip/soip";
        String regex = "\\d+.\\d+.\\d+.\\d+";
        String object = session.getForObject(url, String.class);
        Pattern compile = Pattern.compile(regex);
        Matcher matcher = compile.matcher(object);
        while (matcher.find()){
            String ip = matcher.group();
            System.out.println(ip);

        }
    }


}
