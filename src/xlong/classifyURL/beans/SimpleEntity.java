package xlong.classifyURL.beans;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;

public class SimpleEntity {
	public String name;
	public ArrayList<String> urls;
	public ArrayList<String> types;
	public boolean availed;
	
	public SimpleEntity(String name){
		this.name = name;
		urls = new ArrayList<String>();
		types = new ArrayList<String>();
		availed = true;
	}
	
	public void addUrl(String url){
		urls.add(url);
	}
	
	public void addType(String type){
		types.add(type);
	}
	
	public void combine(SimpleEntity b){
		if (!equal(b)) return;
		for (String url:b.urls){
			urls.add(url);
		}
		for (String type:b.types){
			types.add(type);
		}
	}
	
	public void combineAndFilter(SimpleEntity b, Map<String,HashSet<String>> subClassMap){
		combine(b);
		HashSet<String> dels = new HashSet<String>();
		for (String type:types){
			if (subClassMap.containsKey(type)){
				dels.addAll(subClassMap.get(type));
			}
		}
		ArrayList<String> newTypes = new ArrayList<String>();
		for (String type:types){
			if (!dels.contains(type) && type.startsWith("http://dbpedia.org/ontology/")){
				newTypes.add(type);
			}
		}
		if (newTypes.size() != 1){
			availed = false;
		}
		types = newTypes;
	}

	public boolean equal(SimpleEntity b){
		if (name.equals(b.name)) return true;
		else return false;
	}
	
	public String toString(){
		if (availed){
			String s = name + "\n";
			for (String url:urls){
				s = s + url + " ";
			}
			s = s + "\n";
			for (String type:types){
				s = s + type + " ";
			}
			return s;
		}
		else{
			return "";
		}
	}
}
