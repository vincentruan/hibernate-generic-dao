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
package com.trg.dao.hibernate;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import com.trg.search.ExampleOptions;
import com.trg.search.Filter;
import com.trg.search.ISearch;
import com.trg.search.Search;
import com.trg.search.SearchResult;

/**
 * Implementation of <code>GenericDAO</code> using Hibernate.
 * The SessionFactory property is annotated for automatic resource injection.
 * 
 * @author dwolverton
 * 
 * @param <T>
 *            The type of the domain object for which this instance is to be
 *            used.
 * @param <ID>
 *            The type of the id of the domain object for which this instance is
 *            to be used.
 */
@SuppressWarnings("unchecked")
public class GenericDAOImpl<T, ID extends Serializable> extends
		HibernateBaseDAO implements GenericDAO<T, ID> {

	protected Class<T> persistentClass = (Class<T>) ((ParameterizedType) getClass()
			.getGenericSuperclass()).getActualTypeArguments()[0];

	public int count(ISearch search) {
		if (search == null)
			search = new Search();
		return _count(persistentClass, search);
	}

	public T find(Serializable id) {
		return _get(persistentClass, id);
	}

	public T[] find(Serializable... ids) {
		return _get(persistentClass, ids);
	}

	public List<T> findAll() {
		return _all(persistentClass);
	}

	public void flush() {
		_flush();
	}

	public T getReference(Serializable id) {
		return _load(persistentClass, id);
	}

	public T[] getReferences(Serializable... ids) {
		return _load(persistentClass, ids);
	}

	public boolean isAttached(T entity) {
		return _sessionContains(entity);
	}

	public void refresh(T... entities) {
		_refresh(entities);
	}

	public boolean remove(T entity) {
		return _deleteEntity(entity);
	}

	public void remove(T... entities) {
		_deleteEntities(entities);
	}

	public boolean removeById(Serializable id) {
		return _deleteById(persistentClass, id);
	}

	public void removeByIds(Serializable... ids) {
		_deleteById(persistentClass, ids);
	}

	public boolean save(T entity) {
		return _saveOrUpdateIsNew(entity);
	}

	public boolean[] save(T... entities) {
		return _saveOrUpdateIsNew(entities);
	}

	public List<T> search(ISearch search) {
		if (search == null)
			return findAll();
		return _search(persistentClass, search);
	}

	public SearchResult<T> searchAndCount(ISearch search) {
		if (search == null) {
			SearchResult<T> result = new SearchResult<T>();
			result.setResult(findAll());
			result.setTotalCount(result.getResult().size());
			return result;
		}
		return _searchAndCount(persistentClass, search);
	}

	public List searchGeneric(ISearch search) {
		if (search == null)
			return findAll();
		return _search(persistentClass, search);
	}

	public T searchUnique(ISearch search) {
		return (T) _searchUnique(persistentClass, search);
	}

	public Object searchUniqueGeneric(ISearch search) {
		return _searchUnique(persistentClass, search);
	}

	public Filter getFilterFromExample(T example) {
		return _getFilterFromExample(example);
	}

	public Filter getFilterFromExample(T example, ExampleOptions options) {
		return _getFilterFromExample(example, options);
	}	
}