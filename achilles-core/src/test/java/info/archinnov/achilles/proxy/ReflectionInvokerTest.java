/**
 *
 * Copyright (C) 2012-2013 DuyHai DOAN
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package info.archinnov.achilles.proxy;

import static info.archinnov.achilles.entity.metadata.PropertyType.ID;
import static org.fest.assertions.api.Assertions.assertThat;
import info.archinnov.achilles.entity.metadata.PropertyMeta;
import info.archinnov.achilles.entity.metadata.PropertyType;
import info.archinnov.achilles.exception.AchillesException;
import info.archinnov.achilles.test.builders.PropertyMetaTestBuilder;
import info.archinnov.achilles.test.mapping.entity.CompleteBean;
import info.archinnov.achilles.test.parser.entity.CompoundKey;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.cassandra.utils.Pair;
import org.apache.commons.lang.math.RandomUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

public class ReflectionInvokerTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	private ReflectionInvoker invoker = new ReflectionInvoker();

	@Test
	public void should_get_value_from_field() throws Exception {
		Bean bean = new Bean();
		bean.setComplicatedAttributeName("test");
		Method getter = Bean.class
				.getDeclaredMethod("getComplicatedAttributeName");

		String value = (String) invoker.getValueFromField(bean, getter);
		assertThat(value).isEqualTo("test");
	}

	@Test
	public void should_get_value_from_null_field() throws Exception {
		Method getter = Bean.class
				.getDeclaredMethod("getComplicatedAttributeName");
		assertThat(invoker.getValueFromField(null, getter)).isNull();
	}

	@Test
	public void should_exception_when_getting_value_from_field()
			throws Exception {
		Bean bean = new Bean();
		bean.setComplicatedAttributeName("test");
		Method getter = Bean.class
				.getDeclaredMethod("getComplicatedAttributeName");

		exception.expect(AchillesException.class);
		exception.expectMessage("Cannot invoke '" + getter.getName()
				+ "' of type '" + Bean.class.getCanonicalName()
				+ "' on instance 'bean'");

		invoker.getValueFromField("bean", getter);
	}

	@Test
	public void should_set_value_to_field() throws Exception {
		Bean bean = new Bean();
		Method setter = Bean.class.getDeclaredMethod(
				"setComplicatedAttributeName", String.class);

		invoker.setValueToField(bean, setter, "fecezzef");

		assertThat(bean.getComplicatedAttributeName()).isEqualTo("fecezzef");
	}

	@Test
	public void should_not_set_value_when_null_field() throws Exception {
		Method setter = Bean.class.getDeclaredMethod(
				"setComplicatedAttributeName", String.class);
		invoker.setValueToField(null, setter, "fecezzef");
	}

	@Test
	public void should_exception_when_setting_value_to_field() throws Exception {
		Bean bean = new Bean();
		bean.setComplicatedAttributeName("test");
		Method setter = Bean.class.getDeclaredMethod(
				"setComplicatedAttributeName", String.class);

		exception.expect(AchillesException.class);
		exception.expectMessage("Cannot invoke '" + setter.getName()
				+ "' of type '" + Bean.class.getCanonicalName()
				+ "' on instance 'bean'");

		invoker.setValueToField("bean", setter, "test");
	}

	@Test
	public void should_get_value_from_list_field() throws Exception {
		CompleteBean bean = new CompleteBean();
		bean.setFriends(Arrays.asList("foo", "bar"));
		Method getter = CompleteBean.class.getDeclaredMethod("getFriends");

		@SuppressWarnings("unchecked")
		List<String> value = (List<String>) invoker.getListValueFromField(bean,
				getter);
		assertThat(value).containsExactly("foo", "bar");
	}

	@Test
	public void should_get_value_from_set_field() throws Exception {
		CompleteBean bean = new CompleteBean();
		bean.setFollowers(Sets.newHashSet("foo", "bar"));
		Method getter = CompleteBean.class.getDeclaredMethod("getFollowers");

		@SuppressWarnings("unchecked")
		Set<String> value = (Set<String>) invoker.getSetValueFromField(bean,
				getter);
		assertThat(value).containsOnly("foo", "bar");
	}

	@Test
	public void should_get_value_from_map_field() throws Exception {
		CompleteBean bean = new CompleteBean();
		bean.setPreferences(ImmutableMap.of(1, "FR"));
		Method getter = CompleteBean.class.getDeclaredMethod("getPreferences");

		@SuppressWarnings("unchecked")
		Map<Integer, String> value = (Map<Integer, String>) invoker
				.getMapValueFromField(bean, getter);
		assertThat(value).containsKey(1).containsValue("FR");
	}

	@Test
	public void should_get_primary_key() throws Exception {
		Long id = RandomUtils.nextLong();
		CompleteBean bean = new CompleteBean(id);

		PropertyMeta idMeta = PropertyMetaTestBuilder
				.completeBean(Void.class, Long.class).type(ID).field("id")
				.accessors().build();

		Object key = invoker.getPrimaryKey(bean, idMeta);
		assertThat(key).isEqualTo(id);
	}

	@Test
	public void should_exception_when_getting_primary_key() throws Exception {

		PropertyMeta idMeta = PropertyMetaTestBuilder
				.completeBean(Void.class, String.class).type(ID).field("id")
				.accessors().build();

		exception.expect(AchillesException.class);
		exception
				.expectMessage("Cannot get primary key value by invoking getter 'getId' of type '"
						+ CompleteBean.class.getCanonicalName()
						+ "' from entity 'bean'");

		invoker.getPrimaryKey("bean", idMeta);
	}

	@Test
	public void should_return_null_key_when_null_entity() throws Exception {
		PropertyMeta idMeta = PropertyMetaTestBuilder
				.completeBean(Void.class, Long.class).field("id").accessors()
				.build();
		assertThat(invoker.getPrimaryKey(null, idMeta)).isNull();
	}

	@Test
	public void should_get_partition_key() throws Exception {
		long partitionKey = RandomUtils.nextLong();
		Method userIdGetter = CompoundKey.class.getDeclaredMethod("getUserId");
		PropertyMeta idMeta = PropertyMetaTestBuilder
				.valueClass(CompoundKey.class).compGetters(userIdGetter)
				.type(PropertyType.EMBEDDED_ID).build();

		CompoundKey compoundKey = new CompoundKey(partitionKey, "name");

		assertThat(invoker.getPartitionKey(compoundKey, idMeta)).isEqualTo(
				partitionKey);
	}

	@Test
	public void should_exception_when_getting_partition_key() throws Exception {

		Method userIdGetter = CompoundKey.class.getDeclaredMethod("getUserId");
		PropertyMeta idMeta = PropertyMetaTestBuilder
				.valueClass(CompoundKey.class).compGetters(userIdGetter)
				.type(PropertyType.EMBEDDED_ID).build();

		exception.expect(AchillesException.class);
		exception
				.expectMessage("Cannot get partition key value by invoking getter 'getUserId' of type '"
						+ CompoundKey.class.getCanonicalName()
						+ "' from compoundKey 'compound'");

		invoker.getPartitionKey("compound", idMeta);
	}

	@Test
	public void should_return_null_for_partition_key_if_not_embedded_id()
			throws Exception {
		long partitionKey = RandomUtils.nextLong();
		Method userIdGetter = CompoundKey.class.getDeclaredMethod("getUserId");
		PropertyMeta idMeta = PropertyMetaTestBuilder
				.valueClass(CompoundKey.class).compGetters(userIdGetter)
				.type(PropertyType.ID).build();

		CompoundKey compoundKey = new CompoundKey(partitionKey, "name");
		assertThat(invoker.getPartitionKey(compoundKey, idMeta)).isNull();
	}

	@Test
	public void should_instanciate_entity_from_class() throws Exception {
		CompoundKey actual = invoker.instanciate(CompoundKey.class);
		assertThat(actual).isNotNull();
		assertThat(actual.getUserId()).isNull();
		assertThat(actual.getName()).isNull();
	}

	@Test
	public void should_instanciate_embedded_id_with_partition_key_using_default_constructor()
			throws Exception {
		Long partitionKey = RandomUtils.nextLong();

		Method userIdSetter = CompoundKey.class.getDeclaredMethod("setUserId",
				Long.class);
		PropertyMeta idMeta = PropertyMetaTestBuilder
				.valueClass(CompoundKey.class).compSetters(userIdSetter)
				.build();

		Object actual = invoker.instanciateEmbeddedIdWithPartitionKey(idMeta,
				partitionKey);

		assertThat(actual).isNotNull();
		CompoundKey compoundKey = (CompoundKey) actual;
		assertThat(compoundKey.getUserId()).isEqualTo(partitionKey);
		assertThat(compoundKey.getName()).isNull();
	}

	@Test
	public void should_throw_exception_when_cannot_instanciate_entity_from_class()
			throws Exception {

		exception.expect(AchillesException.class);
		exception.expectMessage("Cannot instanciate entity from class '"
				+ Pair.class.getCanonicalName() + "'");

		invoker.instanciate(Pair.class);
	}

	class Bean {

		private String complicatedAttributeName;

		public String getComplicatedAttributeName() {
			return complicatedAttributeName;
		}

		public void setComplicatedAttributeName(String complicatedAttributeName) {
			this.complicatedAttributeName = complicatedAttributeName;
		}
	}

	class ComplexBean {
		private List<String> friends;

		public List<String> getFriends() {
			return friends;
		}

		public void setFriends(List<String> friends) {
			this.friends = friends;
		}
	}
}