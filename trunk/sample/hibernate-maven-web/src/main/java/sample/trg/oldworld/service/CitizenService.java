package sample.trg.oldworld.service;

import java.util.List;

import com.trg.search.ISearch;
import com.trg.search.SearchResult;

import sample.trg.oldworld.model.Citizen;

public interface CitizenService {

	public void save(Citizen citizen);
	
	public void delete(Long id);

	public List<Citizen> findAll();

	public List<Citizen> search(ISearch search);
	
	public SearchResult<Citizen> searchAndCount(ISearch search);
	
	public Citizen findById(Long id);

	public Citizen findByName(String name);

	public void flush();
}