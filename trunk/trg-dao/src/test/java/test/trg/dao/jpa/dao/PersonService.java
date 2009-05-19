package test.trg.dao.jpa.dao;

import java.util.List;

import test.trg.model.Person;

import com.trg.search.ExampleOptions;
import com.trg.search.Filter;
import com.trg.search.ISearch;
import com.trg.search.SearchResult;

public class PersonService {

	PersonDAO dao;

	public void setPersonDAO(PersonDAO dao) {
		this.dao = dao;
	}

	public int count(ISearch search) {
		return dao.count(search);
	}

	public Person[] find(Long... ids) {
		return dao.find(ids);
	}

	public Person find(Long id) {
		return dao.find(id);
	}

	public List<Person> findAll() {
		return dao.findAll();
	}

	public void flush() {
		dao.flush();
	}

	public Filter getFilterFromExample(Person example, ExampleOptions options) {
		return dao.getFilterFromExample(example, options);
	}

	public Filter getFilterFromExample(Person example) {
		return dao.getFilterFromExample(example);
	}

	public Person getReference(Long id) {
		return dao.getReference(id);
	}

	public Person[] getReferences(Long... ids) {
		return dao.getReferences(ids);
	}

	public boolean isAttached(Person entity) {
		return dao.isAttached(entity);
	}

	public Person[] merge(Person... entities) {
		return dao.merge(entities);
	}

	public Person merge(Person entity) {
		return dao.merge(entity);
	}

	public void persist(Person... entities) {

		dao.persist(entities);
	}

	public void refresh(Person... entities) {

		dao.refresh(entities);
	}

	public void remove(Person... entities) {

		dao.remove(entities);
	}

	public boolean remove(Person entity) {

		return dao.remove(entity);
	}

	public boolean removeById(Long id) {

		return dao.removeById(id);
	}

	public void removeByIds(Long... ids) {
		dao.removeByIds(ids);
	}

	public Person[] save(Person... entities) {
		return dao.save(entities);
	}

	public Person save(Person entity) {
		return dao.save(entity);
	}

	public List<Person> search(ISearch search) {
		return dao.search(search);
	}

	public SearchResult<Person> searchAndCount(ISearch search) {
		return dao.searchAndCount(search);
	}

	@SuppressWarnings("unchecked")
	public List searchGeneric(ISearch search) {
		return dao.searchGeneric(search);
	}

	public Person searchUnique(ISearch search) {
		return dao.searchUnique(search);
	}

	public Object searchUniqueGeneric(ISearch search) {
		return dao.searchUniqueGeneric(search);
	}

}
