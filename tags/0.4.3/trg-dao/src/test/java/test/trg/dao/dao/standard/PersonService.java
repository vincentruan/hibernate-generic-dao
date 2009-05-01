package test.trg.dao.dao.standard;

import java.util.List;

import javax.annotation.Resource;

import test.trg.shared.model.Person;

import com.trg.search.ExampleOptions;
import com.trg.search.Filter;
import com.trg.search.Search;
import com.trg.search.SearchResult;

public class PersonService {

	private PersonDAO dao;
	
	@Resource(name="personDAO")
	public void setDAO(PersonDAO dao) {
		this.dao = dao;
	}
	
	
	public int count(Search search) {
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

	public Person getReference(Long id) {
		return dao.getReference(id);
	}

	public Person[] getReferences(Long... ids) {
		return dao.getReferences(ids);
	}

	public boolean isAttached(Person entity) {
		return dao.isAttached(entity);
	}

	public void refresh(Person entity) {
		dao.refresh(entity);
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

	public void save(Person... entities) {
		dao.save(entities);
	}

	public boolean save(Person entity) {
		return dao.save(entity);
	}

	public List<Person> search(Search search) {
		return dao.search(search);
	}

	public SearchResult<Person> searchAndCount(Search search) {
		return dao.searchAndCount(search);
	}

	@SuppressWarnings("unchecked")
	public List searchGeneric(Search search) {
		return dao.searchGeneric(search);
	}

	public Person searchUnique(Search search) {
		return dao.searchUnique(search);
	}

	public Object searchUniqueGeneric(Search search) {
		return dao.searchUniqueGeneric(search);
	}
	
	public Filter getFilterFromExample(Person example) {
		return dao.getFilterFromExample(example);
	}
	
	public Filter getFilterFromExample(Person example, ExampleOptions options) {
		return dao.getFilterFromExample(example, options);
	}
}
