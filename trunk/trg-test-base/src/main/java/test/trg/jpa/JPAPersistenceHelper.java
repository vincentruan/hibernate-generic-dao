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
package test.trg.jpa;

import java.io.Serializable;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import test.trg.PersistenceHelper;

public class JPAPersistenceHelper implements PersistenceHelper {

	public <T> T find(Class<T> type, Serializable id) {
		return (T) entityManager.find(type, id);
	}

	public void persist(Object entity) {
		entityManager.persist(entity);
	}
	
	public void flush() {
		entityManager.flush();
	}
	
	public void clear() {
		entityManager.clear();
	}
	
	private EntityManager entityManager;

	@PersistenceContext
	public void setEnityManager(EntityManager enityManager) {
		this.entityManager = enityManager;
	}

	public <T> T getProxy(Class<T> type, Serializable id) {
		return entityManager.getReference(type, id);
	}

}
