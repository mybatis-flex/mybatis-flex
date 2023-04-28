package com.mybatisflex.annotation;

/**
 * 类型支持 update 监听器
 *
 * @author snow
 * @since 2023/4/28
 */
public abstract class AbstractUpdateListener<T> implements UpdateListener {

    /**
     * 该监听器支持的entity类型
     *
     * @return type
     */
    public abstract Class<T> supportType();
    public abstract void doUpdate(T entity);

    @Override
    @SuppressWarnings("unchecked")
    public void onUpdate(Object entity) {
        Class<T> supportType = supportType();
        if (supportType.isInstance(entity)) {
            T object = (T) entity;
            doUpdate(object);
        }
    }
}
