package com.mybatisflex.test.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 测试SpringBatch功能的controller
 */
@RestController
@CrossOrigin
@RequestMapping("job")
@Tag(name = "SpringBatch测试", description = "SpringBatch测试")
public class BatchController {

    /**
     * job执行器
     */
    @Autowired
    @Lazy
    private JobLauncher jobLauncher;

    /**
     * springbatch job
     */
    @Autowired
    @Lazy
    @Qualifier("testImportJob")
    private Job testImportJob;

    /**
     * springbatch job 测试
     *
     * @return
     */
    @GetMapping("testImportJob")
    public Map<String, Object> testImportJob() {
        try {
            JobParametersBuilder parameters = new JobParametersBuilder();
            //为了防止任务无法重复创建的bug
            parameters.addDate("createDate", new Date());
            JobParameters jobParameters = parameters.toJobParameters();

            JobExecution run1 = jobLauncher.run(testImportJob, jobParameters);
            run1.setStartTime(new Date());
            while (run1.isRunning()) {
                Thread.sleep(500);
            }
            Map<String, Object> map = new HashMap<>();
            map.put("code", 200);
            map.put("success", true);
            map.put("message", "job completed");
            return map;
        } catch (Exception ex) {
            Map<String, Object> map = new HashMap<>();
            map.put("code", 500);
            map.put("success", false);
            map.put("message", ex.getMessage());
            return map;
        }
    }

}
