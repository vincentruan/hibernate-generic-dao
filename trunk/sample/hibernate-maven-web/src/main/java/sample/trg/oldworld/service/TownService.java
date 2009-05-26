package sample.trg.oldworld.service;

import java.util.List;
import java.util.Map;

import com.trg.search.ISearch;

import sample.trg.oldworld.model.Citizen;
import sample.trg.oldworld.model.Town;


public interface TownService {

	public void save(Town citizen);

	public List<Town> findAll();

	public Town findByName(String name);

	public List<Map<String,Object>> findAllWithForDropdown();
	
	public List<Town> search(ISearch search);
	
	public void delete(Long id);

	public Town findById(Long id);
}