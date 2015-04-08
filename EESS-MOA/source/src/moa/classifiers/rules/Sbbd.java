//package moa.classifiers.rules;
//
//import java.util.Arrays;
//import java.util.HashSet;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Random;
//import java.util.Set;
//import java.util.Vector;
//
//import br.ufmg.dcc.lac.AssociationRule;
//import br.ufmg.dcc.lac.LacInstance;
//import br.ufmg.dcc.lac.LacRule;
//import br.ufmg.dcc.lac.LacSupervised;
//import br.ufmg.dcc.lac.Training;
//
//import weka.core.Instance;
//import moa.classifiers.AbstractClassifier;
//import moa.classifiers.eess.AssociationRuleInterface;
//import moa.classifiers.eess.LacWeka;
//import moa.core.Measurement;
//import moa.options.ClassOption;
//import moa.options.FlagOption;
//import moa.options.FloatOption;
//
//public class Sbbd  extends AbstractClassifier {
//
//	private static final long serialVersionUID = 6789593603127136931L;
//	
//	public ClassOption associationRuleOption = 
//			new ClassOption("associationRule", 'a', "", AssociationRuleInterface.class, 
//					"moa.classifiers.eess.EclatAssociationRule");
//	
//	public FloatOption thresholdOption = new FloatOption("threshold",
//			'r', "", 0.1, 0, 1);
//		
//	public FlagOption considerFeaturesPositionOption = new FlagOption("considerFeaturesPosition", 
//			'p', "Consider the position of features.");
//	
//	protected final List<LacInstance> lacInstances;
//	protected final Set<LacInstance> previousRules;
//	protected final Training training;
//	protected AssociationRule associationRule;
//	protected final Random rand;
//	protected final LacSupervised lac;
//	
//	protected int labeledAdded;
//	protected int timestamp;
//	
//	protected boolean manage;
//
//	protected int trainingSize;	
//	protected int trainingAvgSize;
//	
//	protected int nsawInstances;
//	
//	public Sbbd(){
//		this.lacInstances = new Vector<LacInstance>();
//		this.previousRules = new HashSet<LacInstance>();
//		this.training = new Training();
//		this.rand = new Random(this.randomSeed);
//		this.lac = new LacSupervised();
//	}
//	
//	@Override
//	public void resetLearningImpl() {
//		
//		this.previousRules.clear();
//		this.lacInstances.clear();
//		this.labeledAdded = 0;
//		this.rand.setSeed(this.randomSeed);
//	
//		this.timestamp = 1;
//		
//		this.manage = false;
//		
//		this.trainingSize = 0;
//		
//		this.trainingAvgSize = 0;
//		
//		this.nsawInstances = 0;
//		
//		AssociationRuleInterface associationRuleInterface = (AssociationRuleInterface) 
//				getPreparedClassOption(this.associationRuleOption);
//		
//		associationRule = associationRuleInterface.getAssociationRule();
//	}
//
//	@Override
//	public void trainOnInstanceImpl(Instance arg0) {
//		LacInstance inst = LacWeka.populateInstance(timestamp, arg0, this.considerFeaturesPositionOption.isSet());
//		this.timestamp++;
//		
//		if(this.manage == true){
//			this.nsawInstances++;
//			this.rankFunction(inst);
//			this.previousRules.clear();
//			
//			//Inserting instance
//			this.labeledAdded++;
//			this.lacInstances.add(inst);
//			
//		}else{
//			this.lacInstances.add(inst);
//		}
//	}
//	
//	protected void rankFunction(LacInstance t){				
//		double sim = 0;
//		double tim = 0;
//		
//		final Set<String> tFeatures = new HashSet<String>();
//		for(String f : t.getFeatures()){
//			tFeatures.add(f);
//		}
//		
//		final Set<String> union = new HashSet<String>();
//		final Set<String> intersection = new HashSet<String>();
//		final Set<String> iFeatures = new HashSet<String>();
//		
//		LacInstance i = null;
//		final Iterator<LacInstance> it = this.lacInstances.iterator();
//		while(it.hasNext()){
//			i = it.next();
//			
//			//similarity
//			union.clear();
//			union.addAll(tFeatures);
//			
//			intersection.clear();
//			intersection.addAll(tFeatures);
//			
//			for(String f : i.getFeatures()){
//				iFeatures.add(f);
//			}
//
//			union.addAll(iFeatures);
//			intersection.retainAll(iFeatures);
//			sim = (double)intersection.size()/union.size();
//			
//			//time
//			tim = 1 - (double)i.tid/t.tid;
//			
//			//rank Function
//			i.rank = (sim + tim)/2;
//
////			System.out.printf("Rank: %f (Keep: %s) - Sim:%f - Timestamp-J: %d - Timestamp-I: %d - M(j,i): %f\n", 
////					i.rank, (Double.compare(i.rank, thresholdOption.getValue()) > 0 ? "No" : "Yes" ),
////					sim, i.tid, t.tid, tim);
//			
//			//rank evaluation
//			if(Double.compare(i.rank, thresholdOption.getValue()) > 0){
//				it.remove();
//			}
//		}
////		System.out.println();
//		
//		this.trainingSize = this.lacInstances.size();
//		this.trainingAvgSize += this.lacInstances.size();
//	}
//	
//	@Override
//	public double[] getVotesForInstance(Instance arg0) {
//		 final double[] votes = new double[arg0.numClasses()];
//		 
//		 if(this.manage == true){
//			this.training.clear();
//			this.training.addTransactions(this.lacInstances);
//			
//			final LacInstance instance = LacWeka.populateInstance(timestamp, arg0,
//					considerFeaturesPositionOption.isSet());
//			
//			this.associationRule.setClassTable(this.training.getClassesTable());
//			this.associationRule.setFeaturesTalbe(this.training.getFeaturesTable());
//			this.associationRule.setTidClassMap(this.training.getTidClassMap());
//			
//			final double[] scores = new double[this.training.getNumberOfClasses()];
//			
//			final List<LacRule> rules = new Vector<LacRule>();
//			
//			lac.predict(this.training, instance, associationRule, scores, rules);
//			
//			LacWeka.calcProbability(this.training, arg0.classAttribute(), scores, votes);
//
//			this.previousRules.add(instance);
//
//		}else{
//			final double equalVotes = 1.0 / arg0.numClasses();
//			Arrays.fill(votes, equalVotes);
//			
//			this.manage = true;
//		}
//		
//		return votes;
//	}
//
//	@Override
//	public boolean isRandomizable() {
//		return false;
//	}
//
//	@Override
//	public void getModelDescription(StringBuilder arg0, int arg1) {}
//	
//	@Override
//	protected Measurement[] getModelMeasurementsImpl() {
//		
//		Measurement[] m = new Measurement[]{
//				new Measurement("model training instances", this.labeledAdded),
//				new Measurement("window", this.trainingSize),
//				new Measurement("AvgWindow", (double)this.trainingAvgSize/this.nsawInstances)};
//		
//		this.nsawInstances = 0;
//		this.trainingAvgSize = 0;
//		
//		return m;
//	}
//}
