package com.mybatisflex.spring.batch;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;
import org.apache.ibatis.cursor.Cursor;
import org.springframework.batch.item.support.AbstractItemCountingItemStreamItemReader;
import org.springframework.beans.factory.InitializingBean;

import java.util.*;

import static org.springframework.util.Assert.notNull;
import static org.springframework.util.ClassUtils.getShortName;

/**
 * 游标模式读取
 * @author zhangjian
 */
public class MyBatisFlexCursorItemReader<T> extends AbstractItemCountingItemStreamItemReader<T>
    implements InitializingBean {

  /**
   * 当前的mapper
   */
  private BaseMapper<T> mapper;

  /**
   * 拼接的入参列表
   */
  private QueryWrapper queryWrapper;

  /**
   *
   */
  private Cursor<T> cursor;

  /**
   *
   */
  private Iterator<T> cursorIterator;

  public MyBatisFlexCursorItemReader() {
    setName(getShortName(MyBatisFlexCursorItemReader.class));
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

  @Override
  protected T doRead() throws Exception {
    T next = null;
    if (cursorIterator.hasNext()) {
      next = cursorIterator.next();
    }
    return next;
  }

  @Override
  protected void doOpen() {
    if (Objects.isNull(this.mapper) || Objects.isNull(this.queryWrapper)) {
      throw new IllegalArgumentException("mapper or queryWrapper is required.");
    }
    this.cursor = this.mapper.selectCursorByQuery(queryWrapper);
    cursorIterator = cursor.iterator();
  }

  @Override
  protected void doClose() throws Exception {
    if (cursor != null) {
      cursor.close();
    }
    cursorIterator = null;
  }

  /**
   * Check mandatory properties.
   *
   * @see InitializingBean#afterPropertiesSet()
   */
  @Override
  public void afterPropertiesSet() {
    notNull(mapper, "A BaseMapper is required.");
    notNull(queryWrapper, "A queryWrapper is required.");
  }

}
