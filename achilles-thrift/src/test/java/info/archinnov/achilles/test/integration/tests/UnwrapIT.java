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
package info.archinnov.achilles.test.integration.tests;

import static org.fest.assertions.api.Assertions.assertThat;
import info.archinnov.achilles.entity.manager.ThriftEntityManager;
import info.archinnov.achilles.junit.AchillesInternalThriftResource;
import info.archinnov.achilles.junit.AchillesTestResource.Steps;
import info.archinnov.achilles.test.builders.TweetTestBuilder;
import info.archinnov.achilles.test.integration.entity.CompleteBean;
import info.archinnov.achilles.test.integration.entity.CompleteBeanTestBuilder;
import info.archinnov.achilles.test.integration.entity.Tweet;
import net.sf.cglib.proxy.Factory;

import org.junit.Rule;
import org.junit.Test;

public class UnwrapIT {

	@Rule
	public AchillesInternalThriftResource resource = new AchillesInternalThriftResource(
			Steps.AFTER_TEST, "CompleteBean");

	private ThriftEntityManager em = resource.getEm();

	@Test
	public void should_unproxy_object() throws Exception {
		CompleteBean bean = CompleteBeanTestBuilder.builder().randomId()
				.name("Jonathan").buid();
		Tweet tweet = TweetTestBuilder.tweet().randomId().content("tweet")
				.buid();
		bean.setWelcomeTweet(tweet);

		bean = em.merge(bean);

		bean = em.unwrap(bean);

		assertThat(bean).isNotInstanceOf(Factory.class);
	}

	@Test
	public void should_unproxy_directly_attached_join_object() throws Exception {
		CompleteBean bean = CompleteBeanTestBuilder.builder().randomId()
				.name("Jonathan").buid();
		Tweet tweet = TweetTestBuilder.tweet().randomId().content("tweet")
				.buid();
		bean.setWelcomeTweet(tweet);

		em.persist(bean);

		bean = em.find(CompleteBean.class, bean.getId());
		em.initialize(bean);
		bean = em.unwrap(bean);

		assertThat(bean).isNotInstanceOf(Factory.class);
		assertThat(bean.getWelcomeTweet()).isNotInstanceOf(Factory.class);
	}

}