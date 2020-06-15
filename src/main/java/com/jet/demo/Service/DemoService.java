package com.jet.demo.Service;

import com.jet.demo.Utils.ChineseNameUtil;
import org.springframework.stereotype.Service;

@Service
public class DemoService {
    public static void main(String[] args){
        System.out.println(ChineseNameUtil.randomName(true,3));
    }
}
