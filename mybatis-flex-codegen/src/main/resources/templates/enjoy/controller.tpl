package #(globalConfig.controllerPackage);

import org.springframework.web.bind.annotation.RequestMapping;
#if(globalConfig.restStyleController)
import org.springframework.web.bind.annotation.RestController;
#else
import org.springframework.stereotype.Controller;
#end
#if(globalConfig.controllerSupperClass)
import #(table.buildControllerImport())
#end

#if(globalConfig.restStyleController)
@RestController
#else
@Controller
#end
@RequestMapping("/#(table.getEntityJavaFileName())")
#if(globalConfig.controllerSupperClass)
public class #(table.table.buildControllerClassName()) extends #(table.buildControllerName()) {

}
#else
public class #(table.buildControllerClassName()) {

}
#end