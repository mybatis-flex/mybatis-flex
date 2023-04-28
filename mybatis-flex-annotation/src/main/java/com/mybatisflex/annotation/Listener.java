package com.mybatisflex.annotation;

/**
 * 监听器
 *
 * @author snow
 * @since 2023/4/28
 */
public interface Listener extends Comparable<Listener> {

    /**
     * 多个监听器时的执行顺序
     * <p>值越小越早触发执行</p>
     *
     * @return order
     */
    default int order() {
        return Integer.MAX_VALUE;
    }

    @Override
    default int compareTo(Listener other) {
        return order() - other.order();
    }
}
