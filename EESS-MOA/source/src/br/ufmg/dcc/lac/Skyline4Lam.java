package br.ufmg.dcc.lac;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class Skyline4Lam implements Serializable{

	private static final long serialVersionUID = -4669210735389148783L;

	private enum RELATION {DOMINATED, DOMINATES, INCOMPARABLE};
    
    protected final List<Itemset> window;
    protected final List<Itemset> dominated;
    
    protected Skyline4Lam(){
        window = new ArrayList<Itemset>();
        dominated = new ArrayList<Itemset>();
    }
    
    protected void addPoint(Itemset point){
        for(int i = window.size() - 1; i > -1 ; --i){
        	Itemset rule = this.window.get(i);
            RELATION r = this.compare(point, rule);
            
            if(r == RELATION.DOMINATED){
            	dominated.add(point);
                return;
            }else if(r == RELATION.DOMINATES){
            	Itemset inst = this.window.remove(i);
            	dominated.add(inst);
            }
        }
        
        this.window.add(point);
    }
    
    private Skyline4Lam.RELATION compare(Itemset point1, Itemset point2){
        int i = 0;
        
        double[] metricsPoint1 = point1.getMetrics();
        double[] metricsPoint2 = point2.getMetrics();
        
        while(i < metricsPoint1.length && Double.compare(metricsPoint1[i], metricsPoint2[i]) == 0){
            ++i;
        }

        if(i == metricsPoint1.length){
            return RELATION.INCOMPARABLE;
        }
        
        if(Double.compare(metricsPoint1[i], metricsPoint2[i]) < 0){
            for(++i; i < metricsPoint1.length; ++i){
                if(Double.compare(metricsPoint1[i], metricsPoint2[i]) > 0){
                        return RELATION.INCOMPARABLE;
                }
            }
            return RELATION.DOMINATED;
        }
                
        for(++i; i < metricsPoint1.length; ++i){
            if(Double.compare(metricsPoint1[i], metricsPoint2[i]) < 0){
                return RELATION.INCOMPARABLE;
            }
        }
        
        return RELATION.DOMINATES;
    }
}