package com.xinyang.app.web.controller;

import com.alibaba.fastjson.JSON;
import com.xinyang.app.web.domain.form.RemoteCommentForm;
import com.xinyang.app.web.domain.support.SimpleResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;

@RestController
@CrossOrigin("*")
@RequestMapping("/proxy")
public class NewsController {

    @GetMapping("/news/recommend")
    public Object newsList(@PageableDefault(page = 0,size = 8) Pageable pageable) {
        String url = "http://39.107.228.75:9001/news/recommend?page="+pageable.getPageNumber()+"&size="+pageable.getPageSize();
        try{
            return this.sendHttpGet(url);
        }catch (Exception e){
            return SimpleResponse.fail(e.getMessage());
        }
    }

    @GetMapping("/category")
    public Object  category(){
        String url = "http://39.107.228.75:9001/category?page=0&size=8&classId=2904e1433c9541939e36f8b45e1abbdc";
        try{
            return this.sendHttpGet(url);
        }catch (Exception e){
            return SimpleResponse.fail(e.getMessage());
        }
    }

    @GetMapping("/news/category")
    public Object newsList(@PageableDefault(page = 0,size = 8) Pageable pageable,String typeId) {
        String url = "http://39.107.228.75:9001/news/category?page="+pageable.getPageNumber()+"&size="+pageable.getPageSize()+"&typeId="+typeId;
        try{
            return this.sendHttpGet(url);
        }catch (Exception e){
            return SimpleResponse.fail(e.getMessage());
        }
    }


    @GetMapping("/comment/list")
    public Object commentList(@PageableDefault(page = 0,size = 6) Pageable pageable,String id){
        String url = "http://39.107.228.75:9001/comment/list?page="+pageable.getPageNumber()+"&size="+pageable.getPageSize()+"&id="+id;
        try{
            return this.sendHttpGet(url);
        }catch (Exception e){
            return SimpleResponse.fail(e.getMessage());
        }
    }

    @GetMapping("/news/views")
    public Object newsViews(String id){
        String url = "http://39.107.228.75:9001/news/views?id="+id;
        try{
            return this.sendHttpGet(url);
        }catch (Exception e){
            return SimpleResponse.fail(e.getMessage());
        }
    }

    @PostMapping("/comment/article")
    public Object writeComment(@RequestBody RemoteCommentForm remoteCommentForm) {
        String url = "http://39.107.228.75:9001/comment/article";
        CloseableHttpResponse httppHttpResponse = null;
        CloseableHttpClient httpClient = null;
        try{
            httpClient = HttpClients.createDefault();

            HttpPost httpPost = new HttpPost(url);

            StringEntity entity = new StringEntity(JSON.toJSONString(remoteCommentForm), HTTP.UTF_8);

            entity.setContentType("application/json");


            httpPost.setEntity(
                entity
            );

            httppHttpResponse = httpClient.execute(httpPost);

          return   JSON.parseObject(EntityUtils.toString(httppHttpResponse.getEntity()));
        }catch (Exception e){
            return SimpleResponse.fail(e.getMessage());
        }finally {
            if(httppHttpResponse !=null){
                try {
                    httppHttpResponse.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if(httpClient !=null){
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @GetMapping("/news/fabulous")
    public Object fabulous(String id){
        String url = "http://39.107.228.75:9001/news/fabulous?id="+id;
        try{
            return this.sendHttpGet(url);
        }catch (Exception e){
            return SimpleResponse.fail(e.getMessage());
        }
    }

    @GetMapping("/shop")
    public Object shop(@PageableDefault(page = 0,size = 8) Pageable pageable){
        String url = "http://39.107.228.75:9001/shop?page="+pageable.getPageNumber()+"&size="+pageable.getPageSize();
        try{
            return this.sendHttpGet(url);
        }catch (Exception e){
            return SimpleResponse.fail(e.getMessage());
        }
    }

    @GetMapping("/post/list")
    public Object post(@PageableDefault(page = 0,size = 8) Pageable pageable){
        String url = "http://39.107.228.75:9001/post/list?page="+pageable.getPageNumber()+"&size="+pageable.getPageSize();
        try{
            return this.sendHttpGet(url);
        }catch (Exception e){
            return SimpleResponse.fail(e.getMessage());
        }
    }

    @GetMapping("/video/list")
    public Object video(@PageableDefault(page = 0,size = 8) Pageable pageable,String categoryId){
        String url = "http://39.107.228.75:9001/video/list?page="+pageable.getPageNumber()+"&size="+pageable.getPageSize()+"&categoryId="+categoryId;
        try{
            return this.sendHttpGet(url);
        }catch (Exception e){
            return SimpleResponse.fail(e.getMessage());
        }
    }




    private Object  sendHttpGet(String url) throws IOException {
        URL weChatUrl = new URL(url);
        // 打开和URL之间的连接
        URLConnection connection = weChatUrl.openConnection();
        // 设置通用的请求属性
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        // 建立实际的连接
        connection.connect();

        StringBuffer accessToken = new StringBuffer();
        try(
                Reader in = new InputStreamReader(connection.getInputStream());
                BufferedReader br =new BufferedReader(in)
        ){
            String line;
            while ((line = br.readLine()) != null) {
                accessToken.append(line);
            }

            return   JSON.parseObject(accessToken.toString());
        }
    }
}
