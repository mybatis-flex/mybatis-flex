package com.mybatisflex.test.common;

import com.mybatisflex.annotation.InsertListener;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.core.mybatis.Mappers;
import com.mybatisflex.core.table.TableInfo;
import com.mybatisflex.core.table.TableInfoFactory;
import com.mybatisflex.core.util.CollectionUtil;
import com.mybatisflex.test.listener.missingListenerFix.*;
import com.mybatisflex.test.model.AccountMissingListenerTestModel;
import com.mybatisflex.core.util.MapUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 监听器测试
 *
 * @author Ice 2023/10/23
 * @version 1.0
 */
@SpringBootTest
class ListenerTest {

    @Test
    void missingListenerTest() {

        AccountMissingListenerTestModel accountMissingListenerTestModel = new AccountMissingListenerTestModel();

        //加入配置
        FlexGlobalConfig config = FlexGlobalConfig.getDefaultConfig();
        config.registerInsertListener(new LogicDeleteInsertListener(), LogicDeleteInsertListenerFlag.class);
        config.registerInsertListener(new AccountAgeInsertListener(), AccountAgeInsertListenerFlag.class);

        //获取TableInfo
        TableInfo tableInfo = TableInfoFactory.ofEntityClass(AccountMissingListenerTestModel.class);

        //执行测试 ===> Listener列表比对
        Map<Class<?>, List<InsertListener>> tempOnInsertListenerMap = new ConcurrentHashMap<>();//替代原本的缓存Map

        List<InsertListener> insertListeners = MapUtil.computeIfAbsent(tempOnInsertListenerMap, AccountMissingListenerTestModel.class, aClass -> {
            List<InsertListener> globalListeners = FlexGlobalConfig.getDefaultConfig()
                .getSupportedInsertListener(AccountMissingListenerTestModel.class);
            List<InsertListener> allListeners = CollectionUtil.merge(tableInfo.getOnInsertListeners(), globalListeners);
            Collections.sort(allListeners);
            return allListeners;
        });

        List<Class<? extends InsertListener>> resolvedInsertListeners = insertListeners.stream().map(InsertListener::getClass).collect(Collectors.toList());
        List<Class<? extends InsertListener>> expectedInsertListeners = CollectionUtil.newArrayList(LogicDeleteInsertListener.class, AccountAgeInsertListener.class, AccountTableAnnoInsertListener.class);

        Assertions.assertTrue(
            () -> {
                for (Class<?> clazz : expectedInsertListeners) {
                    if (!resolvedInsertListeners.contains(clazz)) {
                        return false;
                    }
                }
                return true;
            },
            String.format("InsertListener与预期结果不一致\n预期Listener列表:%s\n实际Listener列表:%s", expectedInsertListeners, resolvedInsertListeners)
        );

        //执行测试 ===> 插入结果比对
        BaseMapper baseMapper = Mappers.ofEntityClass(accountMissingListenerTestModel.getClass());
        baseMapper.insert(accountMissingListenerTestModel);

        //实际执行结果
        AccountMissingListenerTestModel dbData = (AccountMissingListenerTestModel) baseMapper.selectOneById(accountMissingListenerTestModel.getId());

        //预期数据
        AccountMissingListenerTestModel expectedData = new AccountMissingListenerTestModel();
        expectedData.setId(dbData.getId());
        expectedData.setUserName("测试缺失的监听器-userName");
        expectedData.setAge(18);
        expectedData.setDelete(false);

        Assertions.assertEquals(expectedData, dbData);
    }
}
