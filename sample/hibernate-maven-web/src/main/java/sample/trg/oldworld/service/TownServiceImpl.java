package sample.trg.oldworld.service;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sample.trg.oldworld.dao.TownDAO;
import sample.trg.oldworld.model.Town;

import com.trg.search.ISearch;
import com.trg.search.Search;

@Service
@Transactional
public class TownServiceImpl implements TownService {

	TownDAO dao;
	
	@Autowired
	public void setDao(TownDAO dao) {
		this.dao = dao;
	}
	
	public void save(Town town) {
		dao.save(town);
	}
	
	public List<Town> findAll() {
		return dao.findAll();
	}
	
	public Town findByName(String name) {
		return dao.searchUnique(new Search().addFilterEqual("name", name).addFetch("citizens"));
	}

	public List<Map<String, Object>> findAllWithForDropdown() {
		Search s = new Search();
		s.addField("id");
		s.addField("name");
		s.setResultMode(Search.RESULT_MAP);
		s.addSortAsc("name");
		return dao.searchGeneric(s);
	}

	public void delete(Long id) {
		dao.removeById(id);
	}

	public List<Town> search(ISearch search) {
		return dao.search(search);
	}

	public Town findById(Long id) {
		return dao.find(id);
	}
}
