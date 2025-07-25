package com.mybatisflex.spring.batch.builder;

import com.mybatisflex.spring.batch.MybatisFlexBatchItemWriter;
import com.mybatisflex.core.BaseMapper;

import java.util.Optional;

/**
 * 构造MybatisFlex数据的写入工具
 *
 * @author zhangjian
 *
 * @see MybatisFlexBatchItemWriter
 */
public class MyBatisFlexBatchItemWriterBuilder<T> {

  /**
   * mapper对象
   */
  private BaseMapper<T> mapper;

  private Boolean assertUpdates;

  /**
   * mapper对象
   * @param mapper
   * @return
   */
  public MyBatisFlexBatchItemWriterBuilder<T> mapper(BaseMapper<T> mapper) {
    this.mapper = mapper;
    return this;
  }

  /**
   * 是否更新标志位
   * @param assertUpdates
   * @return
   */
  public MyBatisFlexBatchItemWriterBuilder<T> assertUpdates(boolean assertUpdates) {
    this.assertUpdates = assertUpdates;
    return this;
  }

  /**
   * 构建写入工具
   * @return
   */
  public MybatisFlexBatchItemWriter<T> build() {
    MybatisFlexBatchItemWriter<T> writer = new MybatisFlexBatchItemWriter<>();
    writer.setMapper(this.mapper);
    Optional.ofNullable(this.assertUpdates).ifPresent(writer::setAssertUpdates);
    return writer;
  }

}
