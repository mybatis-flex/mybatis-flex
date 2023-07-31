/*
 *  Copyright (c) 2022-2023, Mybatis-Flex (fuhai999@gmail.com).
 *  <p>
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  <p>
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  <p>
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.mybatisflex.core.relation;

import com.mybatisflex.annotation.RelationManyToMany;
import com.mybatisflex.annotation.RelationManyToOne;
import com.mybatisflex.annotation.RelationOneToMany;
import com.mybatisflex.annotation.RelationOneToOne;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.FlexConsts;
import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.core.datasource.DataSourceKey;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.row.Row;
import com.mybatisflex.core.util.ClassUtil;
import com.mybatisflex.core.util.CollectionUtil;
import com.mybatisflex.core.util.StringUtil;
import org.apache.ibatis.util.MapUtil;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.mybatisflex.core.query.QueryMethods.column;

/**
 * @author michael
 */
@SuppressWarnings("rawtypes")
public class RelationManager {

    private RelationManager() {
    }

    private static Map<Class<?>, List<AbstractRelation>> classRelations = new ConcurrentHashMap<>();

    /**
     * 默认查询深度
     */
    private static int defaultQueryDepth = FlexGlobalConfig.getDefaultConfig().getDefaultRelationQueryDepth();

    /**
     * 递归查询深度，默认为 2，在一些特殊场景下可以修改这个值
     */
    private static ThreadLocal<Integer> depthThreadLocal = ThreadLocal.withInitial(() -> defaultQueryDepth);

    /**
     * 附加条件的查询参数
     */
    private static ThreadLocal<Map<String, Object>> extraConditionParams = new ThreadLocal<>();


    /**
     * 查询时，可忽略某些已经添加 Relation 注解的属性
     */
    private static ThreadLocal<Set<String>> ignoreRelations = new ThreadLocal<>();


    /**
     * 每次查询是否自动清除 depth  extraConditionParams ignoreRelations 的配置
     */
    private static ThreadLocal<Boolean> autoClearConfig = ThreadLocal.withInitial(() -> true);

    public static int getDefaultQueryDepth() {
        return defaultQueryDepth;
    }

    public static void setDefaultQueryDepth(int defaultQueryDepth) {
        RelationManager.defaultQueryDepth = defaultQueryDepth;
    }

    public static void setMaxDepth(int maxDepth) {
        depthThreadLocal.set(maxDepth);
    }

    public static int getMaxDepth() {
        return depthThreadLocal.get();
    }

    public static void clearMaxDepth() {
        extraConditionParams.remove();
    }


    public static void setExtraConditionParams(Map<String, Object> params) {
        extraConditionParams.set(params);
    }

    public static void addExtraConditionParam(String key, Object value) {
        Map<String, Object> params = extraConditionParams.get();
        if (params == null) {
            params = new HashMap<>();
            extraConditionParams.set(params);
        }
        params.put(key, value);
    }

    public static Map<String, Object> getExtraConditionParams() {
        return extraConditionParams.get();
    }

    public static void clearExtraConditionParams() {
        extraConditionParams.remove();
    }


    public static void setIgnoreRelations(Set<String> ignoreRelations) {
        RelationManager.ignoreRelations.set(ignoreRelations);
    }

    public static void addIgnoreRelations(String... ignoreRelations) {
        Set<String> relations = RelationManager.ignoreRelations.get();
        if (relations == null) {
            relations = new HashSet<>();
            setIgnoreRelations(relations);
        }
        relations.addAll(Arrays.asList(ignoreRelations));
    }

    public static Set<String> getIgnoreRelations() {
        return ignoreRelations.get();
    }

    public static void clearIgnoreRelations() {
        ignoreRelations.remove();
    }


    public static void setAutoClearConfig(boolean enable) {
        autoClearConfig.set(enable);
    }

    public static boolean getAutoClearConfig() {
        return autoClearConfig.get();
    }

    public static void clearAutoClearConfig() {
        autoClearConfig.remove();
    }


    static Object[] getExtraConditionParams(List<String> keys) {
        if (keys == null || keys.isEmpty()) {
            return FlexConsts.EMPTY_ARRAY;
        }
        Map<String, Object> paramMap = extraConditionParams.get();
        if (paramMap == null || paramMap.isEmpty()) {
            return new Object[keys.size()];
        }

        Object[] params = new Object[keys.size()];
        for (int i = 0; i < keys.size(); i++) {
            params[i] = paramMap.get(keys.get(i));
        }

        return params;
    }


    private static List<AbstractRelation> getRelations(Class<?> clazz) {
        return MapUtil.computeIfAbsent(classRelations, clazz, RelationManager::doGetRelations);
    }

    private static List<AbstractRelation> doGetRelations(Class<?> entityClass) {
        List<Field> allFields = ClassUtil.getAllFields(entityClass);
        List<AbstractRelation> relations = new ArrayList<>();
        for (Field field : allFields) {
            RelationManyToMany manyToManyAnnotation = field.getAnnotation(RelationManyToMany.class);
            if (manyToManyAnnotation != null) {
                relations.add(new ManyToMany<>(manyToManyAnnotation, entityClass, field));
            }

            RelationManyToOne manyToOneAnnotation = field.getAnnotation(RelationManyToOne.class);
            if (manyToOneAnnotation != null) {
                relations.add(new ManyToOne<>(manyToOneAnnotation, entityClass, field));
            }

            RelationOneToMany oneToManyAnnotation = field.getAnnotation(RelationOneToMany.class);
            if (oneToManyAnnotation != null) {
                relations.add(new OneToMany<>(oneToManyAnnotation, entityClass, field));
            }

            RelationOneToOne oneToOneAnnotation = field.getAnnotation(RelationOneToOne.class);
            if (oneToOneAnnotation != null) {
                relations.add(new OneToOne<>(oneToOneAnnotation, entityClass, field));
            }
        }
        return relations;
    }


    public static <Entity> void queryRelations(BaseMapper<?> mapper, List<Entity> entities) {
        doQueryRelations(mapper, entities, 0, depthThreadLocal.get(), ignoreRelations.get());
        clearConfigIfNecessary();
    }

    /**
     * 清除查询配置
     */
    private static void clearConfigIfNecessary() {
        Boolean autoClearEnable = autoClearConfig.get();
        if (autoClearEnable != null && autoClearEnable) {
            depthThreadLocal.remove();
            extraConditionParams.remove();
            ignoreRelations.remove();
        }
    }


    @SuppressWarnings({"rawtypes", "unchecked"})
    static <Entity> void doQueryRelations(BaseMapper<?> mapper, List<Entity> entities, int currentDepth, int maxDepth, Set<String> ignoreRelations) {
        if (CollectionUtil.isEmpty(entities)) {
            return;
        }

        if (currentDepth >= maxDepth) {
            return;
        }

        Class<Entity> entityClass = (Class<Entity>) ClassUtil.getUsefulClass(entities.get(0).getClass());
        List<AbstractRelation> relations = getRelations(entityClass);
        if (relations.isEmpty()) {
            return;
        }

        String currentDsKey = DataSourceKey.get();
        try {
            relations.forEach(relation -> {

                //ignore
                if (ignoreRelations != null && (ignoreRelations.contains(relation.getSimpleName())
                    || ignoreRelations.contains(relation.getName()))) {
                    return;
                }

                Set<Object> targetValues;
                List<Row> mappingRows = null;

                //通过中间表关联查询
                if (relation.isRelationByMiddleTable()) {
                    targetValues = new HashSet<>();
                    Set selfFieldValues = relation.getSelfFieldValues(entities);
                    QueryWrapper queryWrapper = QueryWrapper.create().select()
                        .from(relation.getJoinTable());
                    if (selfFieldValues.size() > 1) {
                        queryWrapper.where(column(relation.getJoinSelfColumn()).in(selfFieldValues));
                    } else {
                        queryWrapper.where(column(relation.getJoinSelfColumn()).eq(selfFieldValues.iterator().next()));
                    }

                    mappingRows = mapper.selectListByQueryAs(queryWrapper, Row.class);
                    if (CollectionUtil.isEmpty(mappingRows)) {
                        return;
                    }

                    for (Row mappingData : mappingRows) {
                        Object targetValue = mappingData.getIgnoreCase(relation.getJoinTargetColumn());
                        if (targetValue != null) {
                            targetValues.add(targetValue);
                        }
                    }
                }
                //通过外键字段关联查询
                else {
                    targetValues = relation.getSelfFieldValues(entities);
                }

                if (CollectionUtil.isEmpty(targetValues)) {
                    return;
                }

                //注解配置的数据源
                String configDsKey = relation.getDataSource();
                if (StringUtil.isBlank(configDsKey) && currentDsKey != null) {
                    configDsKey = currentDsKey;
                }

                try {
                    if (StringUtil.isNotBlank(configDsKey)) {
                        DataSourceKey.use(configDsKey);
                    }

                    QueryWrapper queryWrapper = relation.buildQueryWrapper(targetValues);
                    List<?> targetObjectList = mapper.selectListByQueryAs(queryWrapper, relation.getMappingType());
                    if (CollectionUtil.isNotEmpty(targetObjectList)) {

                        //递归查询
                        doQueryRelations(mapper, targetObjectList, currentDepth + 1, maxDepth, ignoreRelations);

                        //进行内存 join
                        relation.join(entities, targetObjectList, mappingRows);
                    }
                } finally {
                    if (StringUtil.isNotBlank(configDsKey)) {
                        DataSourceKey.clear();
                    }
                }
            });
        } finally {
            if (currentDsKey != null) {
                DataSourceKey.use(currentDsKey);
            }
        }
    }

}
