package com.alex.demo.ctx.child.first;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChildFirstCtxController {

    @Autowired
    @Qualifier("parent_bean")
    String parentBean;

    @Autowired
    @Qualifier("child_first_bean")
    String childFirstBean;

    @Autowired(required = false)
    @Qualifier("child_second_bean")
    String childSecondBean;

    @Value("${common.property}")
    String parentProperty;

    @Value("${custom.property.first}")
    String childFirstProperty;

    @Value("${custom.property.second:null}")
    String childSecondProperty;


    // @formatter:off
    @GetMapping("/")
    public Map<String, String> getMessage() {
        
        Map<String, String> map = new HashMap<String, String>();
        map.put("parentBean", parentBean);
        map.put("childFirstBean", childFirstBean);
        map.put("childSecondBean", childSecondBean);
        map.put("parentProperty", parentProperty);
        map.put("childFirstProperty", childFirstProperty);
        map.put("childSecondProperty", childSecondProperty);
        
        return map;
    }
}
