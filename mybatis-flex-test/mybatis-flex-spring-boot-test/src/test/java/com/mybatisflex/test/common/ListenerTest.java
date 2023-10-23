package com.mybatisflex.test.common;

import com.mybatisflex.annotation.InsertListener;
import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.core.exception.FlexAssert;
import com.mybatisflex.core.exception.FlexExceptions;
import com.mybatisflex.core.exception.locale.LocalizedFormats;
import com.mybatisflex.core.table.TableInfo;
import com.mybatisflex.core.table.TableInfoFactory;
import com.mybatisflex.core.util.CollectionUtil;
import com.mybatisflex.test.listener.missionListenerFix.*;
import com.mybatisflex.test.model.AccountMissingListenerTestModel;
import org.apache.ibatis.util.MapUtil;
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

        Map<Class<?>, List<InsertListener>> tempInsertListenerMap = new ConcurrentHashMap<>();//替代原本的缓存Map

        List<InsertListener> insertListeners = MapUtil.computeIfAbsent(tempInsertListenerMap, AccountMissingListenerTestModel.class, aClass -> {
            List<InsertListener> globalListeners = FlexGlobalConfig.getDefaultConfig()
                .getSupportedInsertListener(AccountMissingListenerTestModel.class, CollectionUtil.isNotEmpty(tableInfo.getOnInsertListeners()));
            List<InsertListener> allListeners = CollectionUtil.merge(tableInfo.getOnInsertListeners(), globalListeners);
            Collections.sort(allListeners);
            return allListeners;
        });

        List<? extends Class<? extends InsertListener>> resolvedInsertListeners = insertListeners.stream().map(insertListener -> insertListener.getClass()).collect(Collectors.toList());
        for (Class<?> clazz : CollectionUtil.newArrayList(LogicDeleteInsertListener.class, AccountAgeInsertListener.class, AccountTableAnnoInsertListener.class)) {
            if (!resolvedInsertListeners.contains(clazz)) {
                throw FlexExceptions.wrap("缺失的InsertListener【%s】", clazz.getSimpleName());
            }
        }

        //执行测试 ===> 插入结果比对
//        BaseMapper baseMapper = Mappers.ofEntityClass(accountMissingListenerTestModel.getClass());
//        baseMapper.insert(accountMissingListenerTestModel);

    }
}
