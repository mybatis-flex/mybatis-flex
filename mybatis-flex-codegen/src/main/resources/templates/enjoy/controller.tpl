#set(tableComment = table.getComment())
#set(primaryKeyType = table.getPrimaryKey().getPropertySimpleType())
#set(entityClassName = table.buildEntityClassName())
#set(entityVarName = firstCharToLowerCase(entityClassName))
#set(serviceVarName = firstCharToLowerCase(table.buildServiceClassName()))
package #(packageConfig.controllerPackage);

import com.mybatisflex.core.paginate.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import #(packageConfig.entityPackage).#(entityClassName);
import #(packageConfig.servicePackage).#(table.buildServiceClassName());
#if(controllerConfig.restStyle)
import org.springframework.web.bind.annotation.RestController;
#else
import org.springframework.stereotype.Controller;
#end
#if(controllerConfig.superClass != null)
import #(controllerConfig.buildSuperClassImport());
#end
#if(withSwagger && swaggerVersion.getName() == "FOX")
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
#end
#if(withSwagger && swaggerVersion.getName() == "DOC")
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
#end
import java.util.List;

/**
 * #(tableComment) 控制层。
 *
 * @author #(javadocConfig.getAuthor())
 * @since #(javadocConfig.getSince())
 */
#if(controllerConfig.restStyle)
@RestController
#else
@Controller
#end
#if(withSwagger && swaggerVersion.getName() == "FOX")
@Api("#(tableComment)接口")
#end
#if(withSwagger && swaggerVersion.getName() == "DOC")
@Tag(name = "#(tableComment)接口")
#end
@RequestMapping("#(table.buildControllerRequestMappingPrefix())/#(firstCharToLowerCase(entityClassName))")
public class #(table.buildControllerClassName()) #if(controllerConfig.superClass)extends #(controllerConfig.buildSuperClassName()) #end {

    @Autowired
    private #(table.buildServiceClassName()) #(serviceVarName);

    /**
     * 添加#(tableComment)。
     *
     * @param #(entityVarName) #(tableComment)
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    #if(withSwagger && swaggerVersion.getName() == "FOX")
    @ApiOperation("保存#(tableComment)")
    #end
    #if(withSwagger && swaggerVersion.getName() == "DOC")
    @Operation(description="保存#(tableComment)")
    #end
    public boolean save(@RequestBody #if(withSwagger && swaggerVersion.getName() == "FOX")@ApiParam("#(tableComment)") #end #if(withSwagger && swaggerVersion.getName() == "DOC")@Parameter(description="#(tableComment)")#end #(entityClassName) #(entityVarName)) {
        return #(serviceVarName).save(#(entityVarName));
    }

    /**
     * 根据主键删除#(tableComment)。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    #if(withSwagger && swaggerVersion.getName() == "FOX")
    @ApiOperation("根据主键#(tableComment)")
    #end
    #if(withSwagger && swaggerVersion.getName() == "DOC")
    @Operation(description="根据主键#(tableComment)")
    #end
    public boolean remove(@PathVariable #if(withSwagger && swaggerVersion.getName() == "FOX")@ApiParam("#(tableComment)主键") #end #if(withSwagger && swaggerVersion.getName() == "DOC")@Parameter(description="#(tableComment)主键")#end #(primaryKeyType) id) {
        return #(serviceVarName).removeById(id);
    }

    /**
     * 根据主键更新#(tableComment)。
     *
     * @param #(entityVarName) #(tableComment)
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    #if(withSwagger && swaggerVersion.getName() == "FOX")
    @ApiOperation("根据主键更新#(tableComment)")
    #end
    #if(withSwagger && swaggerVersion.getName() == "DOC")
    @Operation(description="根据主键更新#(tableComment)")
    #end
    public boolean update(@RequestBody #if(withSwagger && swaggerVersion.getName() == "FOX")@ApiParam("#(tableComment)主键") #end #if(withSwagger && swaggerVersion.getName() == "DOC")@Parameter(description="#(tableComment)主键")#end#(entityClassName) #(entityVarName)) {
        return #(serviceVarName).updateById(#(entityVarName));
    }

    /**
     * 查询所有#(tableComment)。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    #if(withSwagger && swaggerVersion.getName() == "FOX")
    @ApiOperation("查询所有#(tableComment)")
    #end
    #if(withSwagger && swaggerVersion.getName() == "DOC")
    @Operation(description="查询所有#(tableComment)")
    #end
    public List<#(entityClassName)> list() {
        return #(serviceVarName).list();
    }

    /**
     * 根据#(tableComment)主键获取详细信息。
     *
     * @param id #(tableComment)主键
     * @return #(tableComment)详情
     */
    @GetMapping("getInfo/{id}")
    #if(withSwagger && swaggerVersion.getName() == "FOX")
    @ApiOperation("根据主键获取#(tableComment)")
    #end
    #if(withSwagger && swaggerVersion.getName() == "DOC")
    @Operation(description="根据主键获取#(tableComment)")
    #end
    public #(entityClassName) getInfo(@PathVariable #if(withSwagger && swaggerVersion.getName() == "FOX")@ApiParam("#(tableComment)主键") #if(withSwagger && swaggerVersion.getName() == "DOC")@Parameter(description="#(tableComment)主键")#end#end #(primaryKeyType) id) {
        return #(serviceVarName).getById(id);
    }

    /**
     * 分页查询#(tableComment)。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    #if(withSwagger && swaggerVersion.getName() == "FOX")
    @ApiOperation("分页查询#(tableComment)")
    #end
    #if(withSwagger && swaggerVersion.getName() == "DOC")
    @Operation(description="分页查询#(tableComment)")
    #end
    public Page<#(entityClassName)> page(#if(withSwagger && swaggerVersion.getName() == "FOX")@ApiParam("分页信息") #end #if(withSwagger && swaggerVersion.getName() == "DOC")@Parameter(description="分页信息")#end Page<#(entityClassName)> page) {
        return #(serviceVarName).page(page);
    }

}
