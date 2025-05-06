package io.github.bonigarcia.wdm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class controller {
    @Autowired
    private service service;


    @GetMapping("/test")
    public ResponseEntity<String> test1(){
        String result = service.test1();
        return new ResponseEntity<>(result,HttpStatus.OK);
    }

    @GetMapping("/test2")
    public ResponseEntity<String> test2(){
        service.test2();
        return new ResponseEntity<>("ok",HttpStatus.OK);
    }
}
