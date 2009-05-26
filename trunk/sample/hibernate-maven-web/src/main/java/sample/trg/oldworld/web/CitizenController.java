package sample.trg.oldworld.web;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import sample.trg.oldworld.model.Citizen;
import sample.trg.oldworld.service.CitizenService;
import sample.trg.oldworld.webhelps.Util;

import com.trg.search.Search;
import com.trg.search.SearchResult;

@Controller
public class CitizenController {
	CitizenService citizenService;

	@Autowired
	public void setCitizenService(CitizenService citizenService) {
		this.citizenService = citizenService;
	}
	
	@RequestMapping
	public void list(HttpServletRequest request, Model model) {
		Search search = new Search();
		search.setMaxResults(10);
		Util.getSearchFromParams(search, request.getParameterMap());
		
		SearchResult<Citizen> results = citizenService.searchAndCount(search);
		model.addAttribute(results.getResult());
		model.addAttribute("pageCount", (results.getTotalCount() + 9) / 10);
		model.addAttribute("page", search.getPage() < 0 ? 1 : search.getPage() + 1);
	}
	
	@RequestMapping
	public String delete(@RequestParam("id") Long id, HttpServletRequest request) {
		citizenService.delete(id);
		return Util.addSearchParamsToURL("redirect:list.do", request.getParameterMap(), true, true, true);
	}
}
