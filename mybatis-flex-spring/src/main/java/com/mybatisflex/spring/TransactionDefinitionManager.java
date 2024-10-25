package com.mybatisflex.spring;

import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionTimedOutException;

import java.util.Date;

/**
 * 事务定义管理器 用于更完整的实现Spring事务
 * 仅支持传统事务不支持R2DBC事务
 *
 * @author Aliothmoon
 * @since 2024/10/25
 */
public final class TransactionDefinitionManager {
    private static final ThreadLocal<TransactionDefinition> TRANSACTION_DEFINITION = new ThreadLocal<>();
    private static final ThreadLocal<Date> TRANSACTION_DEADLINE = new ThreadLocal<>();

    public static TransactionDefinition getTransactionDefinition() {
        return TRANSACTION_DEFINITION.get();
    }

    public static void setTransactionDefinition(TransactionDefinition definition) {
        if (definition == null) {
            return;
        }
        int timeout = definition.getTimeout();


        Definition def = new Definition();
        def.setTimeout(timeout);
        def.setIsolationLevel(definition.getIsolationLevel());
        def.setPropagationBehavior(definition.getPropagationBehavior());
        def.setIsolationLevel(definition.getIsolationLevel());
        def.setName(definition.getName());
        TRANSACTION_DEFINITION.set(def);

        if (timeout != TransactionDefinition.TIMEOUT_DEFAULT) {
            Date deadline = new Date(System.currentTimeMillis() + timeout * 1000L);
            TRANSACTION_DEADLINE.set(deadline);
        }
    }


    /**
     * 清除事务上下文
     *
     */
    public static void clear() {
        TRANSACTION_DEFINITION.remove();
        TRANSACTION_DEADLINE.remove();
    }

    /**
     * 获取当前事务可用TTL
     *
     * @return int
     */
    public static int getTimeToLiveInSeconds() {
        Date deadline = TRANSACTION_DEADLINE.get();
        if (deadline == null) {
            return 0;
        }
        double diff = ((double) getTimeToLiveInMillis(deadline)) / 1000;
        int secs = (int) Math.ceil(diff);
        checkTransactionTimeout(secs <= 0, deadline);
        return secs;
    }

    private static void checkTransactionTimeout(boolean deadlineReached, Date deadline) throws TransactionTimedOutException {
        if (deadlineReached) {
            throw new TransactionTimedOutException("Transaction timed out: deadline was " + deadline);
        }
    }

    private static long getTimeToLiveInMillis(Date deadline) throws TransactionTimedOutException {
        if (deadline == null) {
            throw new IllegalStateException("No timeout specified for this resource holder");
        }
        long timeToLive = deadline.getTime() - System.currentTimeMillis();
        checkTransactionTimeout(timeToLive <= 0, deadline);
        return timeToLive;
    }

    private static class Definition implements TransactionDefinition {
        private Integer propagationBehavior;
        private Integer isolationLevel;
        private Integer timeout;
        private String name;
        private Boolean readOnly;

        public void setReadOnly(Boolean readOnly) {
            this.readOnly = readOnly;
        }

        public void setTimeout(Integer timeout) {
            this.timeout = timeout;
        }

        public void setIsolationLevel(Integer isolationLevel) {
            this.isolationLevel = isolationLevel;
        }

        public void setPropagationBehavior(Integer propagationBehavior) {
            this.propagationBehavior = propagationBehavior;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public int getPropagationBehavior() {
            return propagationBehavior == null ? TransactionDefinition.super.getPropagationBehavior() : propagationBehavior;
        }

        @Override
        public int getIsolationLevel() {
            return isolationLevel == null ? TransactionDefinition.super.getIsolationLevel() : isolationLevel;
        }

        @Override
        public int getTimeout() {
            return timeout == null ? TransactionDefinition.super.getTimeout() : timeout;
        }

        @Override
        public boolean isReadOnly() {
            return readOnly == null ? TransactionDefinition.super.isReadOnly() : readOnly;
        }

        @Override
        public String getName() {
            return name == null ? TransactionDefinition.super.getName() : name;
        }
    }


}
