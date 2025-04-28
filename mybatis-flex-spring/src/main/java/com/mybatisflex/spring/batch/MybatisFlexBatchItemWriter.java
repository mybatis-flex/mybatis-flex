package com.mybatisflex.spring.batch;

import com.mybatisflex.core.BaseMapper;
import org.mybatis.logging.Logger;
import org.mybatis.logging.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.List;
import java.util.Objects;

import static org.springframework.util.Assert.notNull;

/**
 * mybatisflex实现的数据写入工具
 * @author zhangjian
 * @param <T>
 */
public class MybatisFlexBatchItemWriter<T> implements ItemWriter<T>, InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(MybatisFlexBatchItemWriter.class);

    private BaseMapper<T> mapper;

    private boolean assertUpdates = true;

    /**
     * Public setter for the flag that determines whether an assertion is made that number of BatchResult objects returned
     * is one and all items cause at least one row to be updated.
     *
     * @param assertUpdates the flag to set. Defaults to true;
     */
    public void setAssertUpdates(boolean assertUpdates) {
        this.assertUpdates = assertUpdates;
    }

    /**
     * mapper对象
     * @param mapper
     */
    public void setMapper(BaseMapper<T> mapper) {
        if (Objects.isNull(mapper))
            throw new RuntimeException("MybatisFlex Mapper can't be null!");

        this.mapper = mapper;
    }

    /**
     * Check mandatory properties - there must be an SqlSession and a statementId.
     */
    @Override
    public void afterPropertiesSet() {
        notNull(mapper, "A Mapper is required.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(final List<? extends T> items) {

        if (!items.isEmpty()) {
            LOGGER.debug(() -> "Executing batch with " + items.size() + " items.");
            int results = this.mapper.insertBatch((List<T>) items);

            if (assertUpdates) {
                if (results != items.size()) {
                    throw new EmptyResultDataAccessException(
                            "Items.size + " + items.size() + " doesn't match the number of updated rows: " + results, 1);

                }
            }
        }
    }
}
