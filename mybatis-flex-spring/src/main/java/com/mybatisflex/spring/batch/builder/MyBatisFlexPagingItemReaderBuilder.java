package com.mybatisflex.spring.batch.builder;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.batch.MybatisFlexPagingItemReader;

import java.util.Optional;

/**
 * MybatisFlexPagingItemReader  构造工具
 * @author zhangjian
 * @param <T>
 */
public class MyBatisFlexPagingItemReaderBuilder<T> {

  /**
   * mapper对象
   */
  private BaseMapper<T> mapper;

  /**
   * 查询条件对象
   */
  private QueryWrapper queryWrapper;

  /**
   * 分页大小
   */
  private Integer pageSize;

  /**
   * 保存状态标志位
   */
  private Boolean saveState;

  /**
   *  数据最大读取数量
   */
  private Integer maxItemCount;

  /**
   * 设置mapper
   * @param mapper
   * @return
   */
  public MyBatisFlexPagingItemReaderBuilder<T> mapper(BaseMapper<T> mapper) {
    this.mapper = mapper;
    return this;
  }

  /**
   * 设置查询条件
   * @param queryWrapper
   * @return
   */
  public MyBatisFlexPagingItemReaderBuilder<T> queryWrapper(QueryWrapper queryWrapper) {
    this.queryWrapper = queryWrapper;
    return this;
  }

  /**
   * 分页大小
   * @param pageSize
   * @return
   */
  public MyBatisFlexPagingItemReaderBuilder<T> pageSize(int pageSize) {
    this.pageSize = pageSize;
    return this;
  }

  /**
   * 是否更新状态标志位
   * @param saveState
   * @return
   */
  public MyBatisFlexPagingItemReaderBuilder<T> saveState(boolean saveState) {
    this.saveState = saveState;
    return this;
  }

  /**
   * Configure the max number of items to be read.
   * default Integer.Max_Value
   * @param maxItemCount
   * @return
   */
  public MyBatisFlexPagingItemReaderBuilder<T> maxItemCount(int maxItemCount) {
    this.maxItemCount = maxItemCount;
    return this;
  }

  /**
   * Returns a fully built {@link MybatisFlexPagingItemReader}.
   *
   * @return the reader
   */
  public MybatisFlexPagingItemReader<T> build() {
    MybatisFlexPagingItemReader<T> reader = new MybatisFlexPagingItemReader<>();
    reader.setMapper(this.mapper);
    reader.setQueryWrapper(this.queryWrapper);
    Optional.ofNullable(this.pageSize).ifPresent(reader::setPageSize);
    Optional.ofNullable(this.saveState).ifPresent(reader::setSaveState);
    Optional.ofNullable(this.maxItemCount).ifPresent(reader::setMaxItemCount);
    return reader;
  }

}
