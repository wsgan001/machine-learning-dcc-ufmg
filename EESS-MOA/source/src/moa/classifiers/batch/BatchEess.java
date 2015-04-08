//package moa.classifiers.batch;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Random;
//import java.util.Vector;
//
//import br.ufmg.dcc.lac.AssociationRule;
//import br.ufmg.dcc.lac.KaldorHicksEfficiency;
//import br.ufmg.dcc.lac.LacInstance;
//import br.ufmg.dcc.lac.LacRule;
//import br.ufmg.dcc.lac.LacSupervised;
//import br.ufmg.dcc.lac.Skyline2Instance;
//import br.ufmg.dcc.lac.Training;
//import br.ufmg.dcc.lac.Util;
//
//import weka.classifiers.Classifier;
//import weka.core.Instance;
//import weka.core.Instances;
//import moa.classifiers.AbstractClassifier;
//import moa.classifiers.eess.AssociationRuleInterface;
//import moa.core.Measurement;
//import moa.options.ClassOption;
//import moa.options.FlagOption;
//import moa.options.FloatOption;
//import moa.options.IntOption;
//import moa.options.WEKAClassOption;
//
//public class BatchEess extends AbstractClassifier {
//
//	private static final long serialVersionUID = 4740958383832856257L;
//	
//	public WEKAClassOption baseLearnerOption = new WEKAClassOption("baseLearner", 'l',
//            "Classifier to train.", weka.classifiers.Classifier.class, "weka.classifiers.bayes.NaiveBayes");
//
//	public ClassOption associationRuleOption = 
//			new ClassOption("associationRule", 'a', "", AssociationRuleInterface.class, "moa.classifiers.eess.LamAssociationRule");
//	
//	public FlagOption considerFeaturesPositionOption = new FlagOption("considerFeaturesPosition", 'p', "Consider the position of features.");
//	
//	public FlagOption kaldorHicksOption = new FlagOption("kaldorHicks", 'k', "Ativate sample selection by Kaldor-Hicks Efficiency.");
//	
//	public IntOption batchSizeOption = new IntOption("batchSize", 'b', "Size of processing batch", 1, 0, Integer.MAX_VALUE);
//	
//	public FloatOption maxSimilarityOption = new FloatOption("maxSimilarity", 'y', "", 0.0, 0, 1);
//	
//	protected Classifier classifier;
//	
//	protected Instances train;
//	
//	protected List<LacInstance> lacInstances;
//	
//	int labeledAdded = 0;
//	
//	int oldSize;
//	
//	int timestamp;
//	Random rand;
//	
//	boolean manage;
//	
//	int skylineSize;
//	int kaldorHicksSize;
//	
//	List<LacInstance> instancesInBatch;
//	Map<String, LacRule> rulesInBatch;
//	
//	@Override
//	public void resetLearningImpl() {
//		this.instancesInBatch = new ArrayList<LacInstance>();
//		this.rulesInBatch = new HashMap<String, LacRule>();
//		
//		this.labeledAdded = 0;
//		try {
//            String[] options = weka.core.Utils.splitOptions(baseLearnerOption.getValueAsCLIString());
//            this.createWekaClassifier(options);
//        } catch (Exception e) {
//            System.err.println("Creating a new classifier: " + e.getMessage());
//        }
//
//		this.timestamp = 0;
//		this.rand = new Random(this.randomSeed);
//		this.oldSize = 0;
//		this.lacInstances = new ArrayList<LacInstance>();
//		this.manage = false;
//	}
//	
//	@Override
//	public void trainOnInstanceImpl(Instance arg0) {
//		LacInstance inst = this.populateInstance(timestamp, arg0, this.considerFeaturesPositionOption.isSet());
//		this.timestamp++;
//		if(this.manage == true && this.timestamp > 1){
//			this.oldSize = this.train.size();
//			
//			List<LacRule> rules = this.getRulesForInstance(inst);
//			int nRules = rules.size();
//			
//			for(LacRule r : rules){
//				this.rulesInBatch.put(r.toString(), r);
//			}
//			
//			inst.setNRules(nRules);
//			inst.instance = arg0;
//			this.instancesInBatch.add(inst);
//			
//			if(this.instancesInBatch.size() == this.batchSizeOption.getValue()){
//				this.maintain();
//				this.insertInstances();
//				
//				this.rulesInBatch.clear();
//				this.instancesInBatch.clear();
//			}
//			
//		}else{
//			if(this.train == null) {
//				this.train = new Instances(arg0.dataset());
//			}
//			inst.instance = arg0;
//			this.addInstance(inst);
//		}
//	}
//	
//	private List<LacRule> getRulesForInstance(LacInstance inst){
//		Training lacTraining = new Training();
//		lacTraining.addTransactions(this.lacInstances);
//		
//		double[] scores = new double[lacTraining.getNumberOfClasses()];
//		List<LacRule> rules = new Vector<LacRule>();
//		
//		LacSupervised lac = new LacSupervised();
//
//		AssociationRuleInterface associationRuleInterface = (AssociationRuleInterface) 
//				getPreparedClassOption(this.associationRuleOption);
//		
//		AssociationRule associationRule = associationRuleInterface.getAssociationRule();
//		
//		associationRule.setClassTable(lacTraining.getClassesTable());
//		associationRule.setFeaturesTalbe(lacTraining.getFeaturesTable());
//		associationRule.setTidClassMap(lacTraining.getTidClassMap());
//		
//		lac.predict(lacTraining, inst, associationRule, scores, rules);
//		
//		return rules;
//	}
//	
//	private void computeSkyline(final List<LacRule> rules, List<LacInstance> returnedSkyline, List<LacInstance> returnedDominated){
//		double[] coverageRank = Util.coverageRank(this.lacInstances, rules);
//
//		List<LacInstance> insts = new Vector<LacInstance>(this.lacInstances);
//		
//		Skyline2Instance skyline = new Skyline2Instance();
//		for(int i = 0; i < insts.size(); i++){
//			LacInstance instance = insts.get(i); 
//			
//			instance.metrics = new double[3];
//			instance.metrics[0] = coverageRank[i];
//			instance.metrics[1] = (double)instance.tid/this.timestamp;
//			instance.metrics[2] = this.rand.nextDouble();
//			
//			skyline.addPoint(instance);
//		}
//		
//		returnedSkyline.addAll(skyline.window);
//		returnedDominated.addAll(skyline.removed);
//	}
//	
//	@Override
//	public double[] getVotesForInstance(Instance arg0) {
//		
//		double[] votes = new double[arg0.numClasses()];
//		 
//		if(this.manage == true){
//			if(this.oldSize < this.train.size()){
//				this.buildClassifier();
//			}
//			
//			try {
//                votes = this.classifier.distributionForInstance(arg0);
//            } catch (Exception e) {}
//		}else{
//			double equalVotes = 1.0 / arg0.numClasses();
//			Arrays.fill(votes, equalVotes);
//			
//			this.manage = true;
//		}
//		
//		return votes;
//	}
//	
//	private void maintain(){
//
//		List<LacRule> rules = new ArrayList<LacRule>(rulesInBatch.values());
//		
//		List<LacInstance> skyline = new ArrayList<LacInstance>();
//		List<LacInstance> dominated = new ArrayList<LacInstance>();
//		this.computeSkyline(rules, skyline, dominated);	
//		
//		this.skylineSize = skyline.size(); 
//
//		if(kaldorHicksOption.isSet()){
//			KaldorHicksEfficiency.frontier(skyline, dominated);
//			this.kaldorHicksSize = skyline.size();
//		}
//		
//		Instances t = new Instances(train);
//		t.clear();
//		
//		for(LacInstance b : skyline){
//			t.add(b.instance);
//			b.instance = t.lastInstance();
//		}
//		
//		train = t;
//		
//		this.lacInstances.clear();
//		this.lacInstances.addAll(skyline);
//	}
//	
//	private void insertInstances(){
//		
//		for(LacInstance i : this.instancesInBatch){
//			int nRules = i.getNRules();
//			double totalRules = (double) this.rulesInBatch.size();
//			double sim =  1-(nRules/totalRules);
//
//			if(Double.compare(sim, this.maxSimilarityOption.getValue()) > -1){
//				this.addInstance(i);
//				this.labeledAdded++;
//			}
//		}
//	}
//
//	@Override
//	public boolean isRandomizable() {
//		return true;
//	}
//
//	@Override
//	public void getModelDescription(StringBuilder arg0, int arg1) {
//		
//	}
//
//	@Override
//	protected Measurement[] getModelMeasurementsImpl() {		
//		return new Measurement[]{
//				new Measurement("model training instances", this.labeledAdded),
//				new Measurement("window", this.train.size()),
//				new Measurement("Skyline", this.skylineSize),
//				new Measurement("Kaldor-Hicks", this.kaldorHicksSize)};
//	}
//
//	private void buildClassifier() {
//        try {
//            Classifier auxclassifier = weka.classifiers.AbstractClassifier.makeCopy(classifier);
//            auxclassifier.buildClassifier(train);
//            classifier = auxclassifier;
//        } catch (Exception e) {
//            System.err.println("Building WEKA Classifier: " + e.getMessage());
//        }
//    }
//	
//	private void createWekaClassifier(String[] options) throws Exception {
//        String classifierName = options[0];
//        String[] newoptions = options.clone();
//        newoptions[0] = "";
//        this.classifier = weka.classifiers.AbstractClassifier.forName(classifierName, newoptions);
//    }
//	
//	private LacInstance populateInstance(int timestamp, Instance wekaInstance, boolean considerFeaturesPositions) {
//		String label = "";
//		List<String> featuresList = new Vector<String>();
//
//		int numAtts = wekaInstance.numAttributes();
//		int nvalidAtts = 0;
//
//		for (int i = 0; i < numAtts; i++) {
//			if (i != wekaInstance.classIndex() && !wekaInstance.isMissing(i)) {
//				String feature = wekaInstance.toString(i);
//
//				if (considerFeaturesPositions) {
//					feature = String.format("w[%d]=%s", i, feature);
//				}else{
//					feature = String.format("w=%s", feature);
//				}
//				featuresList.add(feature);
//
//				nvalidAtts++;
//
//			} else {
//				label = wekaInstance.classAttribute().value((int)wekaInstance.classValue());
//			}
//		}
//
//		String[] features = featuresList.toArray(new String[nvalidAtts]);
//
//		LacInstance instance = new LacInstance(timestamp, features, label, wekaInstance);
//
//		return instance;
//	}
//	
//	private void addInstance(LacInstance inst){
//		Instance arg0 = inst.instance;
//		this.train.add(arg0);
//		inst.instance = this.train.lastInstance();
//		this.lacInstances.add(inst);
//	}
//}
