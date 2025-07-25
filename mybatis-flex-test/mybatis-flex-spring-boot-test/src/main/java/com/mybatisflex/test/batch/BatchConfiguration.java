package com.mybatisflex.test.batch;

import com.mybatisflex.spring.FlexTransactionManager;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.MapJobRepositoryFactoryBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * 配置batch
 */
@Configuration
@EnableBatchProcessing
public class BatchConfiguration extends DefaultBatchConfigurer {

    /**
     * springbatch默认配置;使用内存库
     *
     * @return
     */

    @Override
    protected JobRepository createJobRepository() throws Exception {
        MapJobRepositoryFactoryBean factory = new MapJobRepositoryFactoryBean();
        return factory.getObject();
    }

    @Override
    protected JobLauncher createJobLauncher() throws Exception {
        SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
        jobLauncher.setTaskExecutor(new SimpleAsyncTaskExecutor()); //转换为异步任务
        jobLauncher.setJobRepository(this.getJobRepository());
        jobLauncher.afterPropertiesSet();
        return jobLauncher;
    }

    /**
     * @return
     */
    @Override
    public PlatformTransactionManager getTransactionManager() {
        FlexTransactionManager mybatisTransactionManager = new FlexTransactionManager();
        return mybatisTransactionManager;
    }
}
