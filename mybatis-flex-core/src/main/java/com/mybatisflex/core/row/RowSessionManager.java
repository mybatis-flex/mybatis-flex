package com.mybatisflex.core.row;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

public interface RowSessionManager {
    RowSessionManager DEFAULT = new RowSessionManager() {
        @Override
        public SqlSession getSqlSession(SqlSessionFactory sqlSessionFactory, ExecutorType executorType) {
            return sqlSessionFactory.openSession(executorType);
        }

        @Override
        public void releaseSqlSession(SqlSession sqlSession, SqlSessionFactory sqlSessionFactory) {
            sqlSession.commit();
            sqlSession.close();
        }
    };

    /**
     * 获取 sqlSession
     *
     * @param sqlSessionFactory
     */
    default SqlSession getSqlSession(SqlSessionFactory sqlSessionFactory){
        return getSqlSession(sqlSessionFactory,sqlSessionFactory.getConfiguration().getDefaultExecutorType());
    }


    /**
     *  获取 sqlSession
     * @param sqlSessionFactory
     * @param executorType
     */
    SqlSession getSqlSession(SqlSessionFactory sqlSessionFactory, ExecutorType executorType);

    /**
     * 释放 sqlSession
     *
     * @param sqlSession
     */
    void releaseSqlSession(SqlSession sqlSession, SqlSessionFactory sqlSessionFactory);

}
