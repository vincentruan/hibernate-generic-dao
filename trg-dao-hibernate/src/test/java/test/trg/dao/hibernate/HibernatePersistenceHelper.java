/* Copyright 2009 The Revere Group
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package test.trg.dao.hibernate;

import java.io.Serializable;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import test.trg.PersistenceHelper;

public class HibernatePersistenceHelper implements PersistenceHelper {

	@SuppressWarnings("unchecked")
	public <T> T find(Class<T> type, Serializable id) {
		return (T) sessionFactory.getCurrentSession().get(type, id);
	}
	
	public void flush() {
		sessionFactory.getCurrentSession().flush();
	}

	public void persist(Object entity) {
		sessionFactory.getCurrentSession().persist(entity);
	}
	
	public void clear() {
		sessionFactory.getCurrentSession().clear();
	}
	
	private SessionFactory sessionFactory;

	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

}
