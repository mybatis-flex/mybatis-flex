#set(isCacheExample = serviceImplConfig.cacheExample)
#set(primaryKey = table.getPrimaryKey().getProperty())
#set(entityClassName = table.buildEntityClassName())
package #(packageConfig.serviceImplPackage);

import #(serviceImplConfig.buildSuperClassImport());
import #(packageConfig.entityPackage).#(table.buildEntityClassName());
import #(packageConfig.mapperPackage).#(table.buildMapperClassName());
#if(table.getGlobalConfig().isServiceGenerateEnable())
import #(packageConfig.servicePackage).#(table.buildServiceClassName());
#end
import org.springframework.stereotype.Service;
#if(isCacheExample)
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
#end

/**
 * #(table.getComment()) 服务层实现。
 *
#if(javadocConfig.getAuthor())
 * @author #(javadocConfig.getAuthor())
#end
#if(javadocConfig.getSince())
 * @since #(javadocConfig.getSince())
#end
 */
@Service
#if(isCacheExample)
@CacheConfig(cacheNames = "#(firstCharToLowerCase(entityClassName))")
#end
public class #(table.buildServiceImplClassName()) extends #(serviceImplConfig.buildSuperClassName())<#(table.buildMapperClassName()), #(table.buildEntityClassName())> #if(table.getGlobalConfig().isServiceGenerateEnable()) implements #(table.buildServiceClassName())#end {

#if(isCacheExample)

#if(table.getGlobalConfig().isServiceGenerateEnable())
    @Override
#end
    @CacheEvict(allEntries = true)
    public boolean remove(QueryWrapper query) {
        return super.remove(query);
    }

#if(table.getGlobalConfig().isServiceGenerateEnable())
    @Override
#end
    @CacheEvict(key = "#id")
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }

#if(table.getGlobalConfig().isServiceGenerateEnable())
    @Override
#end
    @CacheEvict(allEntries = true)
    public boolean removeByIds(Collection<? extends Serializable> ids) {
        return super.removeByIds(ids);
    }

#if(table.getGlobalConfig().isServiceGenerateEnable())
    @Override
#end
    @CacheEvict(allEntries = true)
    public boolean update(#(entityClassName) entity, QueryWrapper query) {
        return super.update(entity, query);
    }

#if(table.getGlobalConfig().isServiceGenerateEnable())
    @Override
#end
    @CacheEvict(key = "#entity.#(primaryKey)")
    public boolean updateById(#(entityClassName) entity, boolean ignoreNulls) {
        return super.updateById(entity, ignoreNulls);
    }

#if(table.getGlobalConfig().isServiceGenerateEnable())
    @Override
#end
    @CacheEvict(allEntries = true)
    public boolean updateBatch(Collection<#(entityClassName)> entities, int batchSize) {
        return super.updateBatch(entities, batchSize);
    }

#if(table.getGlobalConfig().isServiceGenerateEnable())
    @Override
#end
    @Cacheable(key = "#id")
    public #(entityClassName) getById(Serializable id) {
        return super.getById(id);
    }

#if(table.getGlobalConfig().isServiceGenerateEnable())
    @Override
#end
    @Cacheable(key = "#root.methodName + ':' + #query.toSQL()")
    public #(entityClassName) getOne(QueryWrapper query) {
        return super.getOne(query);
    }

#if(table.getGlobalConfig().isServiceGenerateEnable())
    @Override
#end
    @Cacheable(key = "#root.methodName + ':' + #query.toSQL()")
    public <R> R getOneAs(QueryWrapper query, Class<R> asType) {
        return super.getOneAs(query, asType);
    }

#if(table.getGlobalConfig().isServiceGenerateEnable())
    @Override
#end
    @Cacheable(key = "#root.methodName + ':' + #query.toSQL()")
    public Object getObj(QueryWrapper query) {
        return super.getObj(query);
    }

#if(table.getGlobalConfig().isServiceGenerateEnable())
    @Override
#end
    @Cacheable(key = "#root.methodName + ':' + #query.toSQL()")
    public <R> R getObjAs(QueryWrapper query, Class<R> asType) {
        return super.getObjAs(query, asType);
    }

#if(table.getGlobalConfig().isServiceGenerateEnable())
    @Override
#end
    @Cacheable(key = "#root.methodName + ':' + #query.toSQL()")
    public List<Object> objList(QueryWrapper query) {
        return super.objList(query);
    }

#if(table.getGlobalConfig().isServiceGenerateEnable())
    @Override
#end
    @Cacheable(key = "#root.methodName + ':' + #query.toSQL()")
    public <R> List<R> objListAs(QueryWrapper query, Class<R> asType) {
        return super.objListAs(query, asType);
    }

#if(table.getGlobalConfig().isServiceGenerateEnable())
    @Override
#end
    @Cacheable(key = "#root.methodName + ':' + #query.toSQL()")
    public List<#(entityClassName)> list(QueryWrapper query) {
        return super.list(query);
    }

#if(table.getGlobalConfig().isServiceGenerateEnable())
    @Override
#end
    @Cacheable(key = "#root.methodName + ':' + #query.toSQL()")
    public <R> List<R> listAs(QueryWrapper query, Class<R> asType) {
        return super.listAs(query, asType);
    }

    /**
     * @deprecated 无法通过注解进行缓存操作。
     */
#if(table.getGlobalConfig().isServiceGenerateEnable())
    @Override
#end
    @Deprecated
    public List<#(entityClassName)> listByIds(Collection<? extends Serializable> ids) {
        return super.listByIds(ids);
    }

#if(table.getGlobalConfig().isServiceGenerateEnable())
    @Override
#end
    @Cacheable(key = "#root.methodName + ':' + #query.toSQL()")
    public long count(QueryWrapper query) {
        return super.count(query);
    }

#if(table.getGlobalConfig().isServiceGenerateEnable())
    @Override
#end
    @Cacheable(key = "#root.methodName + ':' + #page.getPageSize() + ':' + #page.getPageNumber() + ':' + #query.toSQL()")
    public <R> Page<R> pageAs(Page<R> page, QueryWrapper query, Class<R> asType) {
        return super.pageAs(page, query, asType);
    }

#end
}
