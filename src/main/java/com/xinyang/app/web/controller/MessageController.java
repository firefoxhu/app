package com.xinyang.app.web.controller;
import com.xinyang.app.web.domain.form.MessageForm;
import com.xinyang.app.web.domain.support.SimpleResponse;
import com.xinyang.app.web.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin("*")
@RequestMapping("/message")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @GetMapping("/list")
    public SimpleResponse messageList(HttpServletRequest request, @PageableDefault(page = 0,size = 8,sort = {"isRead","createTime"},direction = Sort.Direction.DESC) Pageable pageable){
        try {
            return SimpleResponse.success(messageService.messageList(request,pageable));
        }catch (Exception e){
            return SimpleResponse.error(e.getMessage());
        }
    }

    @PostMapping("/readed")
    public SimpleResponse  updateReaded(@RequestBody MessageForm messageForm){
        try {
            return SimpleResponse.success(messageService.updateReaded(messageForm.getMessageId()));
        }catch (Exception e){
            return SimpleResponse.error(e.getMessage());
        }
    }

    @PostMapping("/remove")
    public SimpleResponse  remove(HttpServletRequest request,@RequestBody MessageForm messageForm){
        try {
            return SimpleResponse.success(messageService.remove(request,messageForm.getMessageId()));
        }catch (Exception e){
            return SimpleResponse.error(e.getMessage());
        }
    }

    @GetMapping("/unReaderCount")
    public SimpleResponse  unReaderCount(HttpServletRequest request){
        try {
            return SimpleResponse.success(messageService.unReaderCount(request));
        }catch (Exception e){
            return SimpleResponse.error(e.getMessage());
        }
    }

}
