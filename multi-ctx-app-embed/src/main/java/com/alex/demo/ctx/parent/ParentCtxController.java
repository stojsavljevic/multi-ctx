package com.alex.demo.ctx.parent;


import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
public class ParentCtxController {

    @Autowired
    @Qualifier("parent_bean")
    String parentBean;

    @Autowired(required = false)
    @Qualifier("child_bean")
    String childBean;

    @Value("${common.property:null}")
    String parentProperty;

    @Value("${custom.property.child:null}")
    String childProperty;



    // @formatter:off
	@GetMapping("/parent")
	public Map<String, String> getMessage() {
	    
	    Map<String, String> map = new HashMap<String, String>();
        map.put("parentBean", parentBean);
        map.put("childBean", childBean);
        map.put("parentProperty", parentProperty);
        map.put("childProperty", childProperty);
        
        return map;
    }
}
