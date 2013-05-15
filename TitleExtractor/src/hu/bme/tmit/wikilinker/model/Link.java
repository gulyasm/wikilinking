package hu.bme.tmit.wikilinker.model;

import java.util.ArrayList;
import java.util.List;

public class Link {
	private String anchor;
	private List<String> titles;
	
	public Link(String anchor){
		this.anchor = anchor;
		this.titles = new ArrayList<>();
	}
	
	public String getAnchor(){
		return anchor;
	}
	
	public List<String> getTitles(){
		return titles;
	}
	
	public void addTitle(String newTitle){
		titles.add(newTitle);
	}
}
