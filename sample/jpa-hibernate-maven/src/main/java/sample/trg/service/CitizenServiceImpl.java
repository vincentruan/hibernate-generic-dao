package sample.trg.service;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import sample.trg.dao.CitizenDAO;
import sample.trg.model.Citizen;

import com.trg.search.Search;


@Transactional
public class CitizenServiceImpl implements CitizenService {

	CitizenDAO dao;
	
	@Autowired
	public void setDao(CitizenDAO dao) {
		this.dao = dao;
	}
	
	public void persist(Citizen citizen) {
		dao.persist(citizen);
	}
	
	public List<Citizen> findAll() {
		return dao.findAll();
	}
	
	public Citizen findByName(String name) {
		if (name == null)
			return null;
		return dao.searchUnique(new Search().addFilterEqual("name", name));
	}
	
	public void flush() {
		dao.flush();
	}
}
