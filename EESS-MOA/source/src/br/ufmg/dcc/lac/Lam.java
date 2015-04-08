package br.ufmg.dcc.lac;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.Vector;

import br.ufmg.dcc.lac.lam.utility.PaperUtility;
import br.ufmg.dcc.lac.lam.utility.PatternsFilter;
import br.ufmg.dcc.lac.lam.utility.UtilityFactory;
import br.ufmg.dcc.lac.lam.utility.UtilityFunction;
import br.ufmg.dcc.lac.lam.utility.UtilitySpace;

public class Lam extends AssociationRule{

	private static final long serialVersionUID = -5396326090734080089L;

	private final int numPasses;
	private final int clusterThreshold;
	private final int numHash;
	private RulesResult rulesResult;
	private List<Integer> classesSupport;
	private int nclasses;

	private UtilityFunction utilityFunction;
	private UtilitySpace utilitySpace;
	private PatternsFilter filter;
	
	public Lam(double minConfidence, int numPasses, int clusterThreshold, int numHash, String utilityClass) {
		this.numPasses = numPasses;
		this.clusterThreshold = clusterThreshold;
		this.numHash = numHash;
		try{
			if(UtilityFunction.class.isAssignableFrom(Class.forName(utilityClass))){
				this.filter = PatternsFilter.UTILITY_FUNCTION;
				this.utilityFunction = UtilityFactory.createUtilityFunction(utilityClass);
				this.utilitySpace = null;
			}else if(UtilitySpace.class.isAssignableFrom(Class.forName(utilityClass))){
				this.filter = PatternsFilter.PARETO;
				this.utilitySpace = UtilityFactory.createUtilitySpace(utilityClass);
				this.utilityFunction = new PaperUtility();
			}
		}catch(Exception e){
			e.printStackTrace();			
		}
	}

	@Override
	RulesResult induceRules(Projection projection,
			int nclasses, List<Integer> pclassesSupport) {

		this.rulesResult = new RulesResult(nclasses);
		this.classesSupport = pclassesSupport;
		this.nclasses = nclasses;
		
		List<Itemset> database = projection.itemsets;
		
		if(projection.itemsets.size() == 0){
			return rulesResult;
		}
		
		List<Itemset> transactionDatabase = new Vector<Itemset>();
		int t = -1;
		for(Itemset item : database){
			for(int tIndex = 0; tIndex < item.tids.size(); tIndex++){
				t = item.tids.get(tIndex);
				while(transactionDatabase.size() <= t){
					transactionDatabase.add(null);
				}
				
				Itemset transaction = transactionDatabase.get(t);
				if(transaction == null){
					transaction = new Itemset();
					transaction.addTid(t, tidClassMap.get(t));
					transaction.classId = tidClassMap.get(t);
					transactionDatabase.add(t, transaction);
				}
				
				transaction.featureIds.addAll(item.featureIds);				
			}
		}
		
		Iterator<Itemset> transactionDatabaseIt = transactionDatabase.iterator();
		Itemset trans = null;
		while(transactionDatabaseIt.hasNext()){
			trans = transactionDatabaseIt.next();
			if(trans == null){
				transactionDatabaseIt.remove();
			}
		}
		
		

		for(int i = 0; i < this.numPasses && transactionDatabase.size() > 0; i++){
			this.localizePhase(transactionDatabase, this.numHash);
		}

		return rulesResult;
	}
	
	void localizePhase(List<Itemset> itemsets, int numHash){
		final MinHash minHash = new MinHash(numHash);
		
		for(Itemset i : itemsets){
			i.minHash = minHash.minHash(i.featureIds);
			i.minHashStr = Arrays.toString(i.minHash);
		}
		
		Collections.sort(itemsets, new Comparator<Itemset>() {

			@Override
			public int compare(Itemset o1, Itemset o2) {
				return o1.minHashStr.compareTo(o2.minHashStr);
			}
		});
		
		String currentMinHash = itemsets.get(0).minHash[0];
		final int size = itemsets.size();
		int from = 0;
		int to = 0;

		for(; to < size; to++){
			Itemset t = itemsets.get(to);
			
			if(!t.minHash[0].equals(currentMinHash)){
				getList(itemsets, from, to, numHash, this.clusterThreshold);
				from = to;
				currentMinHash = t.minHash[0];
			}
		}
		getList(itemsets, from, to, numHash, this.clusterThreshold);
	}

	void getList(final List<Itemset> itemsets, final int _from, final int _to, final int numHash, final int minSizeCluster){

		final Stack<int[]> pilha = new Stack<int[]>();

		int[] N = null;
		pilha.push(new int[]{_from, _to, 1});

		int lFrom = -1;
		int lTo = -1;
		int lCol = -1;
		int lSize = -1;
		
		int i= -1;
		Itemset t = null;
		String currentMinHash = null;
		
		while(!pilha.empty()){
			N = pilha.pop();

			lFrom = N[0];
			lTo = N[1];
			lCol = N[2];
			lSize = lTo - lFrom;

			if(lSize > minSizeCluster && lCol < numHash){
				currentMinHash = itemsets.get(lFrom).minHash[lCol];
				
				i = lFrom;		
				for(; i < lTo; i++){
					t = itemsets.get(i);

					if(!t.minHash[lCol].equals(currentMinHash)){
						pilha.push(new int[]{lFrom, i, lCol+1});
						currentMinHash = t.minHash[lCol];
						lFrom = i;						
					}
				}
				pilha.push(new int[]{lFrom, i, lCol+1});
				
			}else{
				mine(itemsets, lFrom, lTo);
			}
		}
	}
	
	void mine(final List<Itemset> itemsets, final int from, final int to) {
		final Map<Integer, Integer> itemsHistogram = this.itemsHistogram(itemsets, from, to);
		
		final Trie trie = this.pruringTransactionsItems(itemsets, from, to, itemsHistogram);
		this.utilityFunction.setDatabase(itemsHistogram);
		
		final List<Itemset> potentialItemset = this.generatePotentialItemsetList(trie);

		if(potentialItemset.size() > 0){
			this.patternConsume(itemsets, potentialItemset);
		}
	}
	
	Map<Integer, Integer> itemsHistogram(List<Itemset> itemsets, final int from, final int to) {
		final Map<Integer, Integer> itemsHistogram = new TreeMap<Integer, Integer>();

		for(int i = from; i < to; i++) {
			final Itemset t = itemsets.get(i);
		
			final List<Integer> items = t.featureIds;
			
			for(Integer it : items){
				
				if(itemsHistogram.containsKey(it)){
				
					int count = itemsHistogram.get(it);
					itemsHistogram.put(it, ++count);
				
				}else{
					itemsHistogram.put(it, 1);
				}
			}
		}
		
		return itemsHistogram;
	}
	
	Trie pruringTransactionsItems(List<Itemset> itemsets, final int from, final int to, final Map<Integer, Integer> itemsHistogram){
		final Trie trie = new Trie();
		Itemset t = null;
		
		final Map<Integer, Integer> itemsFreq = new TreeMap<Integer, Integer>(new Comparator<Integer>(){

			@Override
			public int compare(Integer o1, Integer o2) {
				int comparing = o1 - o2;
				
				return comparing == 0 ? 1 : -comparing;
			}
			
		});
		
		int classId = -1;
		int classSupport = -1;
		for(int i = from; i < to; i++){
			itemsFreq.clear();
			
			t = itemsets.get(i);
			
			for(Integer it : t.featureIds){
				if(itemsHistogram.get(it) > 1){
					itemsFreq.put(itemsHistogram.get(it), it);
				}
			}
			
			Collection<Integer> itemsOrdered = itemsFreq.values();
			if(itemsOrdered.size() > 0){
				classId = -1;
				classSupport = -1;
				for(int c = 0; c < this.nclasses; c++){
					if(t.getSupport(c) > classSupport){
						classId = c;
						classSupport = t.getSupport(c); 
					}
				}
				
				trie.insertItem(itemsOrdered, i, classId);
			}
		}

		return trie;
	}
	
	List<Itemset> generatePotentialItemsetList(final Trie trie){
		List<Itemset> potentialItemset = trie.generatePotentialItemsetList();
		
		if(this.filter == PatternsFilter.PARETO){
			final Skyline4Lam skyline = new Skyline4Lam();
			
			for(Itemset i : potentialItemset){
				i.metrics = utilitySpace.getDimensionValues(i);
				skyline.addPoint(i);
			}		
						
			potentialItemset = skyline.window;
		}else{
			Collections.sort(potentialItemset, new Comparator<Itemset>() {
	
				@Override
				public int compare(Itemset o1, Itemset o2) {
					return utilityFunction.ordering() * 
							(Double.compare(utilityFunction.utility(o1),utilityFunction.utility(o2)));
				}
			});
		}
		
		return potentialItemset;
	}
	
	void patternConsume(final List<Itemset> database, final List<Itemset> potentialItemset){
		final Set<Integer> compressed = new HashSet<Integer>();
		final Set<Integer> tItems = new HashSet<Integer>();
		int F = 0;
		double confidence = 0;
		Itemset t = null;
		
		double relativeCompression = 0;
		for(Itemset i : potentialItemset){
			compressed.clear();
			tItems.clear();
			F = 0;
			relativeCompression = 0;
			final List<Integer> tids = i.tids;
			final int itemSize = i.getSize();
			for(Integer tid : tids){
				t = database.get(tid);
				
				tItems.addAll(t.featureIds);
				tItems.retainAll(i.featureIds);				
				
				relativeCompression += (double)itemSize/t.size;
				
				if(tItems.size() == i.featureIds.size()){
					t.featureIds.removeAll(tItems);

					F = 1;
				}
				
				tItems.clear();
			}
			
			if(F == 1){
				i.pattern = true;
				i.relativeCompression = relativeCompression;
				i.size = i.getSize();
				database.add(i);
				for(int c = 0; c < this.nclasses; c++){
					Metrics metrics = new Metrics(classesSupport);
					metrics.setAntecedentSupport(i.getSupport());
					metrics.setRuleSupport(i.getSupport(c), c);
					confidence = metrics.confidence();

					if(Double.compare(confidence,0.0) > -1){

						LacRule r = new LacRule(i.featureIds, c, metrics);
			
						rulesResult.addRule(r);

						rulesResult.classesConfidence[c] += 1;
						rulesResult.classesNRules[c] += 1;
						totalRules++;
					}
				}
				
				F = 0;
			}
			
			i = null;
		}
		
		potentialItemset.clear();
	}
}
