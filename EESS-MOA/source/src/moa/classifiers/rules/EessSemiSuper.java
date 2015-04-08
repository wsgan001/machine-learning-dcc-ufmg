//package moa.classifiers.rules;
//
//import java.util.Arrays;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Random;
//import java.util.Set;
//import java.util.Vector;
//
//import br.ufmg.dcc.lac.AssociationRule;
//import br.ufmg.dcc.lac.KaldorHicksEfficiency;
//import br.ufmg.dcc.lac.LacInstance;
//import br.ufmg.dcc.lac.LacRule;
//import br.ufmg.dcc.lac.LacSupervised;
//import br.ufmg.dcc.lac.Skyline2Instance;
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
//import moa.options.IntOption;
//
//public class EessSemiSuper extends AbstractClassifier {
//
////	private static final long serialVersionUID = 6789593603127136931L;
////
////	public ClassOption associationRuleOption = new ClassOption(
////			"associationRule", 'a', "", AssociationRuleInterface.class,
////			"moa.classifiers.eess.EclatAssociationRule");
////
////	public FlagOption spaceSimilarityOption = new FlagOption(
////			"spaceSimilarity", 's', "");
////	
////	public FlagOption timeSimilarityOption = new FlagOption(
////			"timeSimilarity", 't', "");
////	
////	public IntOption numIndependentDimensionsOption = new IntOption("independenteDimensions", 'd',
////			"", 1, 0, Integer.MAX_VALUE);
////	
////	public FloatOption thresholdOption = new FloatOption("threshold", 'i', "",
////			0.87, 0, 1);
////
////	public FlagOption kaldorHicksOption = new FlagOption("kaldorHicks", 'k',
////			"Activate sample selection by Kaldor-Hicks Efficiency.");
////	
////	public FlagOption perClassOption = new FlagOption(
////			"perClass", 'c',
////			"Consider the position of features.");
////	
////	public FlagOption considerFeaturesPositionOption = new FlagOption(
////			"considerFeaturesPosition", 'p',
////			"Consider the position of features.");
////
////	protected final List<LacInstance> lacInstances;
////	protected final List<LacInstance> previousRules;
////	protected final Training training;
////	protected AssociationRule associationRule;
////	protected final Random rand;
////	protected final LacSupervised lac;
////
////	protected int labeledAdded;
////	protected int timestamp;
////
////	protected boolean manage;
////
////	protected int trainingSize;
////
////	protected int skylineAvgSize;
////	protected int kaldorHicksAvgSize;
////	protected int trainingAvgSize;
////
////	protected int nsawInstances;
////	protected int correctlyInserted;
////	protected double appreciation;
////	protected double real;
////	protected int count;
////	
////	protected boolean spaceSim;
////	protected boolean timeSim;
////	protected int numDimensions;
////	
////	protected boolean kaldorHicks;
////	protected boolean perClass;
////	protected int numIndependentDimensions;
////	
////	public EessSemiSuper() {
////		this.lacInstances = new Vector<LacInstance>();
////		this.previousRules = new Vector<LacInstance>();
////
////		this.training = new Training();
////		this.rand = new Random(this.randomSeed);
////		this.lac = new LacSupervised();
////	}
////
////	@Override
////	public void resetLearningImpl() {
////
////		this.previousRules.clear();
////		this.lacInstances.clear();
////
////		this.labeledAdded = 0;
////		this.rand.setSeed(this.randomSeed);
////
////		this.timestamp = 0;
////
////		this.manage = false;
////
////		this.trainingSize = 0;
////
////		this.skylineAvgSize = 0;
////		this.kaldorHicksAvgSize = 0;
////		this.trainingAvgSize = 0;
////
////		this.nsawInstances = 0;
////		this.correctlyInserted = 0;
////		AssociationRuleInterface associationRuleInterface = 
////				(AssociationRuleInterface) getPreparedClassOption(this.associationRuleOption);
////
////		this.associationRule = associationRuleInterface.getAssociationRule();
////		
////		this.kaldorHicks = this.kaldorHicksOption.isSet();
////		
////		this.perClass = this.perClassOption.isSet();
////		
////		this.numIndependentDimensions = this.numIndependentDimensionsOption.getValue();
////		
////		this.numIndependentDimensions = this.numIndependentDimensionsOption.getValue();
////		this.spaceSim = this.spaceSimilarityOption.isSet();
////		this.timeSim = this.timeSimilarityOption.isSet();
////		
////		this.numDimensions = 0;
////		if(this.spaceSim){
////			this.numDimensions++;
////		}
////		
////		if(this.timeSim){
////			this.numDimensions++;
////		}
////		this.numDimensions += this.numIndependentDimensions;
////		
////	}
////
////	@Override
////	public void trainOnInstanceImpl(Instance arg0) {
////		LacInstance inst = LacWeka.populateInstance(timestamp, arg0,
////				this.considerFeaturesPositionOption.isSet());
////		this.timestamp++;
////
////		if (this.manage == true) {
////			this.nsawInstances++;
////			this.eess();
////
////			// Inserting instance
////			for (LacInstance i : this.previousRules) {
////				if (Double.compare(i.score, thresholdOption.getValue()) > 0) {
////					this.labeledAdded++;
////					this.lacInstances.add(i);
////					String label = this.getModelContext().classAttribute().value((int)arg0.classValue());
////					this.correctlyInserted += (label.equals(i.label) ? 1 : 0);
////				}
////			}
////
////			// this.labeledAdded++;
////			// this.lacInstances.add(inst);
////			this.previousRules.clear();
////		} else {
////			inst.score = 1.0;
////			this.lacInstances.add(inst);
////		}
////	}
////
////	protected void eess() {
////		List<LacInstance> skyline = new Vector<LacInstance>();
////		List<LacInstance> dominated = new Vector<LacInstance>();
////
////		this.computeSkyline(skyline, dominated);
////
////		this.trainingSize = this.lacInstances.size();
////		this.trainingAvgSize += this.lacInstances.size();
////
////		this.lacInstances.clear();
////		this.lacInstances.addAll(skyline);
////	}
////
////	protected void computeSkyline(List<LacInstance> returnedSkyline,
////			List<LacInstance> returnedDominated) {
////
////		double[] similarity = null;
////		
////		if(this.spaceSim){
////			similarity = this.similarity();
////		}
////		final Skyline2Instance[] skyline = new Skyline2Instance[this
////				.getModelContext().numClasses()];
////
////		for (int s = 0; s < skyline.length; s++) {
////			skyline[s] = new Skyline2Instance();
////		}
////		
////		int indexSkyline = 0;
////		int dim = 0;
////		for (int i = 0; i < this.lacInstances.size(); i++) {
////			LacInstance instance = this.lacInstances.get(i);
////			
////			if(this.perClass){
////				indexSkyline = instance.getIndexedClass();
////			}
////			
////			dim = 0;
////			instance.metrics = new double[this.numDimensions];
////			if(this.spaceSim){
////				instance.metrics[dim++] = similarity[i]; // short memory
////			}
////			
////			if(this.timeSim){
////				instance.metrics[dim++] = (double) instance.tid / this.timestamp; // short memory
////			}
////			
////			for(int d = 0; d < this.numIndependentDimensions; d++){
////				instance.metrics[dim++] = (double)((i+1) * this.rand.nextGaussian() + 1); // long memory
////			}	
////
////			skyline[indexSkyline].addPoint(instance);
////		}
////
////		for (int s = 0; s < skyline.length; s++) {
////			this.skylineAvgSize += skyline[s].window.size();
////
////			if (this.kaldorHicks) {
////				KaldorHicksEfficiency.frontier(skyline[s].window,
////						skyline[s].removed);
////				this.kaldorHicksAvgSize += skyline[s].window.size();
////			}
////
////			returnedSkyline.addAll(skyline[s].window);
////		}
////	}
////
////	@Override
////	public double[] getVotesForInstance(Instance arg0) {
////		final double[] votes = new double[arg0.numClasses()];
////
////		if (this.manage == true) {
////			this.manage = true;
////			this.training.clear();
////			this.training.addTransactions(this.lacInstances);
////
////			final LacInstance instance = LacWeka.populateInstance(timestamp,
////					arg0, considerFeaturesPositionOption.isSet());
////
////			this.associationRule.setClassTable(this.training.getClassesTable());
////			this.associationRule.setFeaturesTalbe(this.training
////					.getFeaturesTable());
////			this.associationRule.setTidClassMap(this.training.getTidClassMap());
////
////			final double[] scores = new double[this.training
////					.getNumberOfClasses()];
////
////			final List<LacRule> rules = new Vector<LacRule>();
////
////			lac.predict(this.training, instance, associationRule, scores, rules);
////			
////			LacWeka.calcProbability(this.training, arg0.classAttribute(),
////					scores, votes);
////
////			double normalizationFactor = 0.0;
////			
////			for(int classId = 0; classId < scores.length; classId++){
////				normalizationFactor += scores[classId];
////			}
////
////			for(int classId = 0; Double.compare(normalizationFactor,0.0) == 1 && classId < scores.length; classId++){
////				scores[classId] = scores[classId]/normalizationFactor;
////			}
////			
////			double score = scores[0];
////			int predicted = 0;
////			for (int i = 1; i < scores.length; i++) {
////				if (Double.compare(score, scores[i]) < 0) {
////					score = scores[i];
////					predicted = i;
////				}
////			}
////			
////			instance.score = score;
////			
////			int wekaIndex = this.getModelContext().classAttribute().indexOfValue(instance.label);
////
////			instance.label = this.training.getClassesTable().getName(predicted);
////			
////			this.appreciation += votes[1];
////			this.real += wekaIndex;
////			this.count++;
////
//////			System.out.printf("Rules: %d - Predicted: %s (%.2f) - Label: %s - TrainingSize: %d\n",
//////			rules.size(), this.training.getClassesTable().getName(predicted), 
//////			score, instance.label, this.lacInstances.size());
////
////		
////			this.previousRules.add(instance);
////
////		} else {
////			final double equalVotes = 1.0 / arg0.numClasses();
////			Arrays.fill(votes, equalVotes);
////
////			this.manage = true;
////			this.training.clear();
////			this.training.addTransactions(this.lacInstances);
////		}
////
////		return votes;
////	}
////
////	@Override
////	public boolean isRandomizable() {
////		return true;
////	}
////
////	@Override
////	public void getModelDescription(StringBuilder arg0, int arg1) {
////	}
////
////	@Override
////	protected Measurement[] getModelMeasurementsImpl() {
////
////		Measurement[] m = new Measurement[] {
////				new Measurement("model training instances", this.labeledAdded),
////				new Measurement("correclty inserted", this.correctlyInserted),
////				new Measurement("window", this.trainingSize),
////				new Measurement("AvgWindow", (double) this.trainingAvgSize
////						/ this.nsawInstances),
////				new Measurement("AvgSkyline", (double) this.skylineAvgSize
////						/ this.nsawInstances),
////				new Measurement("AvgKaldor-Hicks",
////						(double) this.kaldorHicksAvgSize / this.nsawInstances),
////				new Measurement("Real",
////						((double) this.real/this.count)),
////				new Measurement("Appreciation",
////						((double) this.appreciation/this.count)) };
////
////		this.real = 0;
////		this.appreciation = 0;
////		this.count = 0;
////		this.nsawInstances = 0;
////		this.trainingAvgSize = 0;
////		this.skylineAvgSize = 0;
////		this.kaldorHicksAvgSize = 0;
////
////		return m;
////	}
////
////	private double[] similarity() {
////		double[] coverage = new double[this.lacInstances.size()];
////
////		Arrays.fill(coverage, 0);
////
////		final Set<String> union = new HashSet<String>();
////		final Set<String> intersection = new HashSet<String>();
////		final Set<String> iFeatures = new HashSet<String>();
////		final Set<String> features = new HashSet<String>();
////
////		LacInstance instance = null;
////		for (int i = 0; i < this.lacInstances.size(); i++) {
////			instance = this.lacInstances.get(i);
////			features.clear();
////			for (String f : instance.getFeatures()) {
////				features.add(f);
////			}
////
////			for (LacInstance r : this.previousRules) {
////				iFeatures.clear();
////				for (String f : r.getFeatures()) {
////					union.add(f);
////					iFeatures.add(f);
////				}
////
////				union.clear();
////				union.addAll(features);
////
////				intersection.clear();
////				intersection.addAll(features);
////				intersection.retainAll(iFeatures);
////
////				// Jaccard Similarity
////				coverage[i] += (double) intersection.size() / union.size();
////			}
////
////			coverage[i] /= (double) this.previousRules.size();
////		}
////
////		return coverage;
////	}
//}