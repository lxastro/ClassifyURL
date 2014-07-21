package xlong.classifyURL.beans;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;

/**
 * Class for merging information of external links and entity types. A simple
 * entity can contain a list of URLs or a list of types The combine method can
 * merge two simple entity.
 * 
 * @author Xiang Long (longx13@mails.tsinghua.edu.cn)
 */

public class SimpleEntity {

	/**
	 * Constant for tagging entities Tag.Blank tag the initial entity. Tag.Type
	 * tag a entity have types list. Tag.URL tag a entity have URLs list.
	 * Tag.Avail tag a entity have both URLs and types list and just have one
	 * type. Tag.Unavail tag a entity have both URLs and types list and have
	 * more than one type. Tag.Combine tag a entity have both URLs and types
	 * list.
	 */
	protected enum Tag {
		Blank, Type, URL, Combine, Avail, Unavail
	}

	/** The name of a entity */
	protected String name;

	/** The list of URLs */
	protected ArrayList<String> urls;

	/** The list of types */
	protected ArrayList<String> types;

	/** The tag of the entity. */
	protected Tag tag;

	/**
	 * Constructor
	 * 
	 * @param name
	 *            the name of the entity.
	 */
	public SimpleEntity(String name) {
		this.name = name;
		urls = null;
		types = null;
		tag = Tag.Blank;
	}

	/**
	 * Returns the name of the entity.
	 * 
	 * @return the name of the entity.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the list of URLs of the entity.
	 * 
	 * @return the list of URLs of the entity.
	 */
	public ArrayList<String> getURLs() {
		return urls;
	}

	/**
	 * Returns the list of types of the entity.
	 * 
	 * @return the list of types of the entity.
	 */
	public ArrayList<String> getTypes() {
		return types;
	}

	/**
	 * Add a URL into ULRs list
	 * 
	 * @param url
	 *            the URL need to be added.
	 * @return success or not.
	 */
	public boolean addURL(String url) {

		if (tag == Tag.Blank) {
			urls = new ArrayList<String>();
			tag = Tag.URL;
		}
		if (tag == Tag.URL) {
			urls.add(url);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Add a type into types list.
	 * 
	 * @param type
	 *            the type need to be added.
	 * @return success or not.
	 */
	public boolean addType(String type) {
		if (tag == Tag.Blank) {
			types = new ArrayList<String>();
			tag = Tag.Type;
		}
		if (tag == Tag.Type) {
			types.add(type);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 
	 * @param b
	 *            the entity combine with.
	 * @return success or not.
	 */
	public boolean combine(SimpleEntity b) {
		if (!equals(b)) {
			return false;
		}
		if (this.tag == Tag.Type && b.tag == Tag.URL) {
			urls = new ArrayList<String>();
			for (String url : b.urls) {
				urls.add(url);
			}
			tag = Tag.Combine;
			return true;
		} else if (this.tag == Tag.URL && b.tag == Tag.Type) {
			types = new ArrayList<String>();
			for (String type : b.types) {
				types.add(type);
			}
			tag = Tag.Combine;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Filter types. Exclude types don't start with
	 * 'http://dbpedia.org/ontology/'. If type A is subclass of type B, then
	 * exclude type B.
	 * 
	 * @param subClassMap
	 *            the subclass of relationship map.
	 * @return success or not.
	 */
	public boolean FilterTypes(Map<String, HashSet<String>> subClassMap) {
		if (tag != Tag.Combine)
			return false;
		HashSet<String> dels = new HashSet<String>();
		for (String type : types) {
			if (subClassMap.containsKey(type)) {
				dels.addAll(subClassMap.get(type));
			}
		}
		ArrayList<String> newTypes = new ArrayList<String>();
		for (String type : types) {
			if (!dels.contains(type)
					&& type.startsWith("http://dbpedia.org/ontology/")) {
				newTypes.add(type);
			}
		}
		types = newTypes;
		if (newTypes.size() != 1) {
			tag = Tag.Unavail;
		} else {
			tag = Tag.Avail;
		}
		return true;
	}

	/**
	 * Gets if this entity is equals to another entity or not.
	 * 
	 * @param b
	 *            entity to compare with.
	 * @return this entity equals to entity b or not.
	 */
	public boolean equals(SimpleEntity b) {
		if (name.equals(b.name))
			return true;
		else
			return false;
	}

	/**
	 * To string method.
	 */
	@Override
	public String toString() {
		String s = name + "\n";
		for (String url : urls) {
			s = s + url + " ";
		}
		s = s + "\n";
		for (String type : types) {
			s = s + type + " ";
		}
		return s;
	}
}
