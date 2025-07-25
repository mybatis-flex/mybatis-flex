package com.mybatisflex.test.batch;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.batch.MybatisFlexBatchItemWriter;
import com.mybatisflex.spring.batch.MybatisFlexPagingItemReader;
import com.mybatisflex.spring.batch.builder.MyBatisFlexBatchItemWriterBuilder;
import com.mybatisflex.spring.batch.builder.MyBatisFlexPagingItemReaderBuilder;
import com.mybatisflex.test.mapper.MyAccountMapper;
import com.mybatisflex.test.model.Account;
import com.mybatisflex.test.model.table.AccountTableDef;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 * 生成batch demo
 */
@Configuration
public class BatchJobConfiguration implements StepExecutionListener, JobExecutionListener {

    /**
     * 帐户信息
     */
    @Autowired
    @Lazy
    private MyAccountMapper accountMapper;


    /**
     * 帐户导入信息
     *
     * @param jobBuilders
     * @return
     */
    @Bean(name = "testImportJob")
    public Job testImportJob(JobBuilderFactory jobBuilders,
                             @Qualifier("accountStep") Step accountStep) {
        return jobBuilders.get("testImportJob")
            .start(accountStep).listener(this)
            .build();
    }

    /**
     * 帐户执行阶段
     *
     * @param stepBuilders
     * @return
     */
    @Bean
    public Step accountStep(StepBuilderFactory stepBuilders,
                            @Qualifier("accountReader") MybatisFlexPagingItemReader accountReader,
                            @Qualifier("accountProcessor") ItemProcessor accountProcessor,
                            @Qualifier("accountWriter") MybatisFlexBatchItemWriter<Account> accountWriter) {
        TaskletStep step = stepBuilders.get("创建帐户")
            .<Account, Account>chunk(10)
            .reader(accountReader)
            .processor(accountProcessor)
            .writer(accountWriter)
            .build();

        step.registerStepExecutionListener(this);
        return step;
    }

    /**
     * 帐户读取
     * @return
     */
    @Bean
    @StepScope
    public MybatisFlexPagingItemReader accountReader() {
        QueryWrapper query = QueryWrapper.create();
        query.select(AccountTableDef.ACCOUNT.ALL_COLUMNS)
            .from(AccountTableDef.ACCOUNT);

        MyBatisFlexPagingItemReaderBuilder builder = new MyBatisFlexPagingItemReaderBuilder();
        MybatisFlexPagingItemReader reader = builder.mapper(accountMapper)
            .pageSize(10)
            .queryWrapper(query)
            .build();

        return reader;
    }

    /**
     * 数据转换
     * @return
     */
    @Bean
    @StepScope
    public ItemProcessor<Account,Account> accountProcessor() {
        ItemProcessor<Account,Account> processor = new ItemProcessor<Account, Account>() {
            @Override
            public Account process(Account account) throws Exception {
                Account entity = new Account();
                BeanUtils.copyProperties(account, entity);
                entity.setUserName(account.getUserName() + "_1");
                entity.setId(null);
                return entity;
            }
        };

        return processor;
    }

    /**
     * 帐户写入
     *
     * @return
     */
    @Bean
    @StepScope
    public MybatisFlexBatchItemWriter<Account> accountWriter() {
        MybatisFlexBatchItemWriter writer = new MyBatisFlexBatchItemWriterBuilder()
            .mapper(accountMapper)
            .build();

        return writer;
    }

//============以下是事件监听==============

    /**
     * Initialize the state of the listener with the {@link StepExecution} from
     * the current scope.
     *
     * @param stepExecution instance of {@link StepExecution}.
     */
    @Override
    public void beforeStep(StepExecution stepExecution) {
    }

    /**
     * Give a listener a chance to modify the exit status from a step. The value
     * returned will be combined with the normal exit status using
     * {@link ExitStatus#and(ExitStatus)}.
     * <p>
     * Called after execution of step's processing logic (both successful or
     * failed). Throwing exception in this method has no effect, it will only be
     * logged.
     *
     * @param stepExecution {@link StepExecution} instance.
     * @return an {@link ExitStatus} to combine with the normal value. Return
     * {@code null} to leave the old value unchanged.
     */
    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        System.out.println(stepExecution.getStepName() + " 共计导入：" + stepExecution.getWriteCount() + "行数据");
        return stepExecution.getExitStatus();
    }

    /**
     * Callback before a job executes.
     *
     * @param jobExecution the current {@link JobExecution}
     */
    @Override
    public void beforeJob(JobExecution jobExecution) {
    }

    /**
     * Callback after completion of a job. Called after both both successful and
     * failed executions. To perform logic on a particular status, use
     * "if (jobExecution.getStatus() == BatchStatus.X)".
     *
     * @param jobExecution the current {@link JobExecution}
     */
    @Override
    public void afterJob(JobExecution jobExecution) {
    }
}
