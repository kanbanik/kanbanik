package com.googlecode.kanbanik;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

@Entity
@DiscriminatorValue("C")
public class Itemcomposite extends Workflowitem {

	private static final long serialVersionUID = -5176636591058414210L;

	@OneToMany(cascade={CascadeType.PERSIST, CascadeType.REMOVE})
	private Collection<Itemleaf> leafs;
	
	public Collection<Itemleaf> getLeafs() {
		return leafs;
	}
	
	public void addLeaf(Itemleaf leaf) {
		if (leafs == null) {
			leafs = new ArrayList<Itemleaf>();
		}
		
		leafs.add(leaf);
	}
}
