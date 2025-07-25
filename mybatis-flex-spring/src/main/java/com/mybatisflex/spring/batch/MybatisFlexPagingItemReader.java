package com.mybatisflex.spring.batch;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.mybatis.spring.batch.MyBatisPagingItemReader;
import org.springframework.batch.item.database.AbstractPagingItemReader;

import java.util.concurrent.CopyOnWriteArrayList;

import static org.springframework.util.Assert.notNull;
import static org.springframework.util.ClassUtils.getShortName;

/**
 * mybatis-flex的分页读取器
 * @author zhangjian
 * @param <T> 实体类型
 */
public class MybatisFlexPagingItemReader<T> extends AbstractPagingItemReader<T>  {

    /**
     * 当前的mapper
     */
    private BaseMapper<T> mapper;

    /**
     * 拼接的入参列表
     */
    private QueryWrapper queryWrapper;

    public MybatisFlexPagingItemReader() {
        setName(getShortName(MyBatisPagingItemReader.class));
    }

    /**
     * 当前的mapper对象
     * @param mapper
     */
    public void setMapper(BaseMapper<T> mapper) {
        this.mapper = mapper;
    }

    /**
     * 当前的参数对象
     * @param queryWrapper
     */
    public void setQueryWrapper(QueryWrapper queryWrapper) {
        this.queryWrapper = queryWrapper;
    }

    /**
     * Check mandatory properties.
     *
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        notNull(mapper, "mapper is required.");
        notNull(queryWrapper, "querywrapper is required.");
    }

    @Override
    protected void doReadPage() {
        if (results == null) {
            results = new CopyOnWriteArrayList<>();
        } else {
            results.clear();
        }
        Page<T> paginate = mapper.paginate(getPage() + 1, getPageSize(), queryWrapper);
        results.addAll(paginate.getRecords());
    }

    @Override
    protected void doJumpToPage(int itemIndex) {
        if (results == null) {
            results = new CopyOnWriteArrayList<>();
        } else {
            results.clear();
        }
        Page<T> paginate = mapper.paginate(itemIndex + 1, getPageSize(), queryWrapper);
        results.addAll(paginate.getRecords());
    }
}
