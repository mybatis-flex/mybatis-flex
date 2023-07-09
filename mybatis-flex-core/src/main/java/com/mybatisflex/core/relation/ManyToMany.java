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
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.row.Row;
import com.mybatisflex.core.util.ClassUtil;
import com.mybatisflex.core.util.CollectionUtil;
import com.mybatisflex.core.util.MapperUtil;
import com.mybatisflex.core.util.StringUtil;

import java.lang.reflect.Field;
import java.util.*;

import static com.mybatisflex.core.query.QueryMethods.column;

class ManyToMany<SelfEntity> extends AbstractRelation<SelfEntity> {

	private String joinTable;
	private String joinSelfColumn;
	private String joinTargetColumn;

	private String orderBy;

	public ManyToMany(RelationManyToMany annotation, Class<SelfEntity> entityClass, Field relationField) {
		super(getDefaultPrimaryProperty(annotation.selfField(), entityClass, "@RelationManyToMany.selfField can not be empty in field: \"" + entityClass.getName() + "." + relationField.getName() + "\""),
			getDefaultPrimaryProperty(annotation.targetField(), getTargetEntityClass(entityClass, relationField), "@RelationManyToMany.targetField can not be empty in field: \"" + entityClass.getName() + "." + relationField.getName() + "\""),
			annotation.dataSource(), entityClass, relationField);

		this.joinTable = annotation.joinTable();
		this.joinSelfColumn = annotation.joinSelfColumn();
		this.joinTargetColumn = annotation.joinTargetColumn();

		this.orderBy = annotation.orderBy();
	}

	@Override
	public Class<?> getMappingType() {
		return Row.class;
	}


	@Override
	public QueryWrapper toQueryWrapper(List<SelfEntity> selfEntities) {
		Set<Object> selfFieldValues = getSelfFieldValues(selfEntities);
		if (selfFieldValues.isEmpty()) {
			return null;
		}

		QueryWrapper queryWrapper = QueryWrapper.create().select()
			.from(joinTable);
		if (selfFieldValues.size() > 1) {
			queryWrapper.where(column(joinSelfColumn).in(selfFieldValues));
		} else {
			queryWrapper.where(column(joinSelfColumn).eq(selfFieldValues.iterator().next()));
		}

		return queryWrapper;
	}


	@Override
	public void join(List<SelfEntity> selfEntities, List<?> mappingObjectList, BaseMapper<?> mapper) {
		List<Row> mappingRows = (List<Row>) mappingObjectList;
		Set<Object> targetValues = new LinkedHashSet<>();
		for (Row row : mappingRows) {
			Object targetValue = row.getIgnoreCase(joinTargetColumn);
			if (targetValue != null) {
				targetValues.add(targetValue);
			}
		}

		if (targetValues.isEmpty()) {
			return;
		}

		QueryWrapper queryWrapper = QueryWrapper.create().select()
			.from(targetTableInfo.getTableNameWithSchema());
		if (targetValues.size() > 1) {
			queryWrapper.where(column(targetTableInfo.getColumnByProperty(targetField.getName())).in(targetValues));
		} else {
			queryWrapper.where(column(targetTableInfo.getColumnByProperty(targetField.getName())).eq(targetValues.iterator().next()));
		}

		if (StringUtil.isNotBlank(orderBy)) {
			queryWrapper.orderBy(orderBy);
		}


		List<?> targetObjectList = mapper.selectListByQueryAs(queryWrapper, relationFieldWrapper.getMappingType());

		if (CollectionUtil.isNotEmpty(targetObjectList)) {
			selfEntities.forEach(selfEntity -> {
				Object selfValue = selfFieldWrapper.get(selfEntity);
				if (selfValue != null) {
					selfValue = selfValue.toString();
					Set<String> targetMappingValues = new HashSet<>();
					for (Row mappingRow : mappingRows) {
						if (selfValue.equals(String.valueOf(mappingRow.getIgnoreCase(joinSelfColumn)))) {
							Object joinValue = mappingRow.getIgnoreCase(joinTargetColumn);
							if (joinValue != null) {
								targetMappingValues.add(joinValue.toString());
							}
						}
					}

					if (!targetMappingValues.isEmpty()) {
						Class<?> wrapType = MapperUtil.getWrapType(relationFieldWrapper.getFieldType());
						Collection<Object> collection = (Collection) ClassUtil.newInstance(wrapType);
						for (Object targetObject : targetObjectList) {
							Object targetValue = targetFieldWrapper.get(targetObject);
							if (targetValue != null && targetMappingValues.contains(targetValue.toString())) {
								collection.add(targetObject);
							}
						}
						relationFieldWrapper.set(collection, selfEntity);
					}
				}
			});
		}
	}
}
