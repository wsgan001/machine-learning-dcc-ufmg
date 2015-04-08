package br.ufmg.dcc.lac.lam.utility;

import java.io.Serializable;

import br.ufmg.dcc.lac.Itemset;

public abstract class UtilitySpace implements Serializable{

	private static final long serialVersionUID = -1102520449414043915L;

	public abstract double[] getDimensionValues(Itemset item);
}
