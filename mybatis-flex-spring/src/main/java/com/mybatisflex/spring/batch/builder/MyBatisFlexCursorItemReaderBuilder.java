package com.mybatisflex.spring.batch.builder;

import com.mybatisflex.spring.batch.MyBatisFlexCursorItemReader;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;
import org.mybatis.spring.batch.MyBatisCursorItemReader;

import java.util.Optional;

/**
 * MyBatisCursorItemReader 构造工具
 * @author zhangjian
 * @param <T>
 */
public class MyBatisFlexCursorItemReaderBuilder<T> {

  /**
   * mapper对象
   */
  private BaseMapper<T> mapper;

  /**
   * 查询条件对象
   */
  private QueryWrapper queryWrapper;

  private Boolean saveState;

  /**
   * 最大读取行数
   */
  private Integer maxItemCount;

  /**
   * 设置mapper对象
   * @param mapper
   * @return
   */
  public MyBatisFlexCursorItemReaderBuilder<T> mapper(BaseMapper<T> mapper) {
    this.mapper = mapper;
    return this;
  }

  /**
   * 设置查询条件
   * @param queryWrapper
   * @return
   */
  public MyBatisFlexCursorItemReaderBuilder<T> queryWrapper(QueryWrapper queryWrapper) {
    this.queryWrapper = queryWrapper;
    return this;
  }

  /**
   * 保存状态标志位
   * @param saveState
   * @return
   */
  public MyBatisFlexCursorItemReaderBuilder<T> saveState(boolean saveState) {
    this.saveState = saveState;
    return this;
  }

  /**
   * 数据读取最大行数
   * @param maxItemCount
   * @return
   */
  public MyBatisFlexCursorItemReaderBuilder<T> maxItemCount(int maxItemCount) {
    this.maxItemCount = maxItemCount;
    return this;
  }

  /**
   * Returns a fully built {@link MyBatisFlexCursorItemReader}.
   *
   * @return the reader
   */
  public MyBatisFlexCursorItemReader<T> build() {
    MyBatisFlexCursorItemReader<T> reader = new MyBatisFlexCursorItemReader<>();
    reader.setMapper(this.mapper);
    reader.setQueryWrapper(this.queryWrapper);
    Optional.ofNullable(this.saveState).ifPresent(reader::setSaveState);
    Optional.ofNullable(this.maxItemCount).ifPresent(reader::setMaxItemCount);
    return reader;
  }

}
