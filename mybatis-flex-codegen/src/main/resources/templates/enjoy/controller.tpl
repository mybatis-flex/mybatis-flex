package #(packageConfig.controllerPackage);

import org.springframework.web.bind.annotation.RequestMapping;
#if(controllerConfig.restStyle)
import org.springframework.web.bind.annotation.RestController;
#else
import org.springframework.stereotype.Controller;
#end
#if(controllerConfig.supperClass)
import #(controllerConfig.buildSuperClassImport())
#end

/**
 * #(table.getComment()) 控制层。
 *
 * @author #(javadocConfig.getAuthor())
 * @since #(javadocConfig.getSince())
 */
#if(controllerConfig.restStyle)
@RestController
#else
@Controller
#end
@RequestMapping("/#(table.getEntityJavaFileName())")
#if(controllerConfig.supperClass)
public class #(table.buildControllerClassName()) extends #(controllerConfig.buildSuperClassName()) {

}
#else
public class #(table.buildControllerClassName()) {

}
#end