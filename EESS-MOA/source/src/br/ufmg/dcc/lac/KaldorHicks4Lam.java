package br.ufmg.dcc.lac;

import java.io.Serializable;
import java.util.List;


public class KaldorHicks4Lam implements Serializable{

	private static final long serialVersionUID = -6926395969695355338L;

	final static void frontier(List<Itemset> skyline, List<Itemset> dominated){
        if(skyline.size() > 0 ){   
	        double k = combine(skyline.get(0).getMetrics());
	        
	        for(Itemset i : skyline){
	            double v = combine(i.getMetrics());
	            
	            if(Double.compare(v, k) < 0){
	                    k = v;
	            }
	        }
	        
	        for(int i = dominated.size() - 1; i > -1 ; i--){
	        	Itemset li = dominated.get(i);
	            double v = combine(li.getMetrics());
	            
	            if(Double.compare(v, k) > -1){
	                    skyline.add(li);
	                    dominated.remove(i);
	            }
	        }
        }
    }
    
    private final static double combine(double[] metrics){
    	double k = 0;
    	for(double m : metrics){
    		k += m;
    	}
    	
    	return k;
    }
}
