package br.ufmg.dcc.lac.lam.utility;

public enum PatternsFilter {

	UTILITY_FUNCTION ("Utility function combining features of itemsets is used to rank the " +
			"list of potential patterns mined."),
	PARETO ("Uses conflicting metrics of itemsets to build an n-dimensional space and filter " +
			"potential patterns mined lying in Pareto Frontier."),
	KALDOR_HICKS ("Uses conflicting metrics of itemsets to build an n-dimensional space and filter " +
			"potential patterns mined lying in Kaldor-Hicks region.");
	
	public final String description;
	
	PatternsFilter(String description){
		this.description = description;
	}
}
