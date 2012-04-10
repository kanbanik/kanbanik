package com.googlecode.kanbanik;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;


@Entity
@DiscriminatorValue("L")
public class Itemleaf extends Workflowitem {

	private static final long serialVersionUID = 6982395529874368150L;

	@ManyToOne
	@JoinTable(name="Workflowitem_Workflowitem",
		joinColumns=@JoinColumn(name="leafs_id", referencedColumnName="id"),
		inverseJoinColumns=@JoinColumn(name="Workflowitem_id", referencedColumnName="id")
	)
	private Itemcomposite itemcomposite;
	
	public Itemcomposite getItemcomposite() {
		return itemcomposite;
	}

	public boolean isSubitem() {
		return getItemcomposite() != null;
	}

}
