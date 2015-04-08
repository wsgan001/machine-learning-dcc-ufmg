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
//import weka.core.Attribute;
//import weka.core.Instance;
//import moa.classifiers.AbstractClassifier;
//import moa.classifiers.eess.AssociationRuleInterface;
//import moa.classifiers.eess.LacWeka;
//import moa.core.Measurement;
//import moa.options.ClassOption;
//import moa.options.FlagOption;
//import moa.options.FloatOption;
//
//public class Sigir  extends AbstractClassifier {
//
//	private static final long serialVersionUID = 6789593603127136931L;
//	
//	public ClassOption associationRuleOption = 
//			new ClassOption("associationRule", 'a', "", AssociationRuleInterface.class, 
//					"moa.classifiers.eess.EclatAssociationRule");
//	
//	public FloatOption thresholdOption = new FloatOption("threshold",
//			'r', "", 0.8, 0, 1);
//	
//	public FloatOption timeThresholdOption = new FloatOption("timeThreshold",
//			't', "", 0.9, 0, 1);
//		
//	public FlagOption considerFeaturesPositionOption = new FlagOption("considerFeaturesPosition", 
//			'p', "Consider the position of features.");
//	
//	protected final List<LacInstance> lacInstances;
//	protected final Set<LacInstance> subjudice;
//	protected final List<LacInstance> previousInstances;
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
//	protected int correctlyInserted;
//	protected int subjudiceSize;
//	protected double appreciation;
//	protected double real;
//	protected int count;
//	
//	public Sigir(){
//		this.lacInstances = new Vector<LacInstance>();
//		this.previousInstances = new Vector<LacInstance>();
//		this.subjudice = new HashSet<LacInstance>();
//		this.training = new Training();
//		this.rand = new Random(this.randomSeed);
//		this.lac = new LacSupervised();
//		
//	}
//	
//	@Override
//	public void resetLearningImpl() {
//		
//		this.subjudice.clear();
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
//		this.correctlyInserted = 0;
//		this.subjudiceSize = 0;
//		
//		AssociationRuleInterface associationRuleInterface = (AssociationRuleInterface) 
//				getPreparedClassOption(this.associationRuleOption);
//		
//		associationRule = associationRuleInterface.getAssociationRule();
//	}
//
//	@Override
//	public void trainOnInstanceImpl(Instance arg0) {
//		this.timestamp++;
//		
//		if(this.manage == true){
//			this.nsawInstances++;
//			if(this.subjudiceSize == this.subjudice.size()){
//				//Inserting instance
//				for(LacInstance i : this.previousInstances){
//					this.labeledAdded++;
//					String label = this.getModelContext().classAttribute().value((int)arg0.classValue());
//					this.correctlyInserted += (label.equals(i.label) ? 1 : 0);
//					this.lacInstances.add(i);
//				}
//				this.previousInstances.clear();
//				this.evaluateSubjudice(arg0.classAttribute());
//			}
//			this.subjudiceSize = this.subjudice.size();
//		}else{
//			LacInstance inst = LacWeka.populateInstance(timestamp, arg0, this.considerFeaturesPositionOption.isSet());
//			this.lacInstances.add(inst);
//		}
//	}
//	
//	protected void evaluateSubjudice(Attribute classAttribute){				
//		this.trainLac();
//		
//		final double[] scores = new double[this.training.getNumberOfClasses()];	
//		final List<LacRule> rules = new Vector<LacRule>();
//		
//		Iterator<LacInstance> it = this.subjudice.iterator();
//		LacInstance subjudiced = null;
//
//		double tim = 0;
//		
//		while(it.hasNext()){
//			subjudiced = it.next();
//			tim = (double)subjudiced.tid/this.timestamp;
//			if(Double.compare(tim, this.timeThresholdOption.getValue()) < 0){
//				it.remove();
//			}
//		}
//		
//		double score = 0;
//		int predicted = 0;
//		it = this.subjudice.iterator();
//		double[] votes = new double[scores.length];
//		while(it.hasNext()){
//			subjudiced = it.next();
//			lac.predict(this.training, subjudiced, associationRule, scores, rules);
//			
//			LacWeka.calcProbability(this.training, classAttribute,
//					scores, votes);
//			
//			double normalizationFactor = 0.0;
//			
//			for(int classId = 0; classId < scores.length; classId++){
//				normalizationFactor += scores[classId];
//			}
//
//			for(int classId = 0; classId < scores.length; classId++){
//				scores[classId] = scores[classId]/normalizationFactor;
//			}
//			
//			score = votes[0];
//			predicted = 0;
//			for (int i = 1; i < votes.length; i++) {
//				if (Double.compare(score, votes[i]) < 0) {
//					score = votes[i];
//					predicted = i;
//				}
//			}
//
//			if(Double.compare(score, this.thresholdOption.getValue()) > 0){
////				System.out.println("Removed from Subjudice");
//				it.remove();
//				this.labeledAdded++;
//				subjudiced.score = score;
//				subjudiced.label = this.training.getClassesTable().getName(predicted);
//				subjudiced.setIndexedClass(predicted);
//				
//				this.correctlyInserted += (subjudiced.trueClass == subjudiced.getIndexedClass() ? 1 : 0);
//				
//				this.lacInstances.add(subjudiced);
//			}
//		}
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
//			this.trainLac();
//			
//			final double[] scores = new double[this.training.getNumberOfClasses()];	
//			final List<LacRule> rules = new Vector<LacRule>();
//				
//			final LacInstance instance = LacWeka.populateInstance(timestamp, arg0,
//						considerFeaturesPositionOption.isSet());
//			lac.predict(this.training, instance, associationRule, scores, rules);
//			
//			LacWeka.calcProbability(this.training, arg0.classAttribute(),
//					scores, votes);
//			
//			double normalizationFactor = 0.0;
//			
//			for(int classId = 0; classId < scores.length; classId++){
//				normalizationFactor += scores[classId];
//			}
//
//			for(int classId = 0; classId < scores.length; classId++){
//				scores[classId] = scores[classId]/normalizationFactor;
//			}
//			
//			double score = votes[0];
//			int predicted = 0;
//			for (int i = 1; i < votes.length; i++) {
//				if (Double.compare(score, votes[i]) < 0) {
//					score = votes[i];
//					predicted = i;
//				}
//			}
//			
////			System.out.println(predicted + " - " + score);
////			System.out.printf("Rules: %d - Predicted: %s (%.2f) - Label: %s - Classes: %d\n", rules.size(), 
////					this.training.getClassesTable().getName(predicted), score, instance.label,
////					this.training.getClassesTable().size());
//
//			instance.score = score;
//			try {
//				instance.trueClass = this.training.getClassesTable().getId(instance.label);
//			} catch (Exception e) {
//				
//				instance.trueClass = this.training.getClassesTable().addName(instance.label);
//			}
//			
//			int wekaIndex = this.getModelContext().classAttribute().indexOfValue(instance.label);
//
//			instance.label = this.training.getClassesTable().getName(predicted);
//
//			this.appreciation += votes[1];
//			this.real += wekaIndex;
//			this.count++;
//			
//			instance.setIndexedClass(predicted);
//			
//			if(Double.compare(score, this.thresholdOption.getValue()) < 0){
////				System.out.println("Subjudice: " + Arrays.toString(scores));
//				this.subjudice.add(instance);
//			}else{
//				this.previousInstances.add(instance);
//			}
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
//	private void trainLac(){
//		this.training.clear();
//		this.training.addTransactions(this.lacInstances);
//		
//		this.associationRule.setClassTable(this.training.getClassesTable());
//		this.associationRule.setFeaturesTalbe(this.training.getFeaturesTable());
//		this.associationRule.setTidClassMap(this.training.getTidClassMap());
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
//				new Measurement("correctly inserted", this.correctlyInserted),
//				new Measurement("window", this.trainingSize),
//				new Measurement("Real",
//						((double) this.real/this.count)),
//				new Measurement("Appreciation",
//						((double) this.appreciation/this.count)) };
//		
//		this.real = 0;
//		this.appreciation = 0;
//		this.count = 0;
//		this.nsawInstances = 0;
//		this.trainingAvgSize = 0;
//		
//		return m;
//	}
//}
