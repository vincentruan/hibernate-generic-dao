package sample.trg.oldworld.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import sample.trg.oldworld.model.Town;
import sample.trg.oldworld.service.TownService;
import sample.trg.oldworld.webhelps.Util;

import com.trg.search.ISearch;

@Controller
public class TownController {
	TownService townService;

	@Autowired
	public void setTownService(TownService townService) {
		this.townService = townService;
	}
	
	@RequestMapping
	public List<Town> list(HttpServletRequest request) {
		ISearch search = Util.getSearchFromParams(request.getParameterMap());
		return townService.search(search);
	}
	
	@RequestMapping
	public String delete(@RequestParam("id") Long id, HttpServletRequest request) {
		townService.delete(id);
		return Util.addSearchParamsToURL("redirect:list.do", request.getParameterMap(), true, true, true);
	}
}
