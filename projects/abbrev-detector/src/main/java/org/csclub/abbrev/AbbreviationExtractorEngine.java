package org.csclub.abbrev;

import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.csclub.abbrev.algorithms.Algorithm;
import org.csclub.abbrev.algorithms.tba.CorpusAbbreviation;
import org.csclub.abbrev.connectors.CorpusReader;
import org.csclub.abbrev.evaluation.AbbreviationEvaluator;
import org.csclub.abbrev.evaluation.ConfusionMatrix;
import org.csclub.abbrev.evaluation.MetaClassifierCorpusBuilder;
import org.csclub.abbrev.impl.Component;
import org.csclub.abbrev.impl.Configuration;
import org.csclub.abbrev.impl.ConfigurationParameter;

/**
 * @author Sergey Serebryakov
 * 
 * A class that implements abbreviation extraction pipeline. 
 * 
 * Configuration parameters are the following:
 *   "connector" :     "connector class name"
 *   "algorithm" :     "algorithm class name"
 *   "pipeline-mode" : "batch|continuous"
 * 
 *   "connector.param1" : "value1"
 *   "connector.param2" : "value2"
 *   ...
 *   "algorithm.param1" : "value1"
 *   "algorithm.param2" : "value2"
 *   ...
 *
 */
public class AbbreviationExtractorEngine  extends Component {
    
    @ConfigurationParameter(name="ConnectorClass", mandatory = true)
    private String connectorClassName;
    @ConfigurationParameter(name="AlgorithmClass", mandatory = true)
    private String algorithmClassName;
    @ConfigurationParameter(name="OutputFileName", defaultValue=ConfigurationParameter.NULL)
    private String outputFileName;
    @ConfigurationParameter(name="OutputFileEncoding", defaultValue=ConfigurationParameter.NULL)
    private String outputFileEncoding;
    @ConfigurationParameter(name="Verbose", defaultValue="true")
    private Boolean verbose;
    
    private CorpusReader corpusReader;
    private Algorithm algorithm;
    
    public CorpusReader getCorpusReader() { return corpusReader; }
    public Algorithm getAlgorithm() { return algorithm; }
    
    @Override
    public void init(Configuration storage) throws Exception {
        super.init(storage);
        
        corpusReader = (CorpusReader)AbbreviationUtils.createClassInstance(connectorClassName);
        corpusReader.init(storage.getOnlyForNamespace("Connector"));
        
        algorithm = (Algorithm)AbbreviationUtils.createClassInstance(algorithmClassName);
        algorithm.init(storage.getOnlyForNamespace("Algorithm"));
    }
    
    public void run() throws Exception {
        long startTime, stopTime;
        // read entire corpus
        if (verbose) { System.out.print(String.format("reading corpus (%s) ...", corpusReader.getClass().getSimpleName())); }
        startTime = System.nanoTime();
        Corpus corpus = corpusReader.read();
        stopTime = System.nanoTime();
        if (verbose) { System.out.println(String.format(" done in %.3f seconds", AbbreviationUtils.toSeconds(startTime, stopTime))); }
        
        // run final algorithm steps
        if (verbose) { System.out.print(String.format("running algorihm (%s) ...", algorithm.getClass().getSimpleName())); }
        startTime = System.nanoTime();
        algorithm.run(corpus);
        stopTime = System.nanoTime();
        if (verbose) { System.out.println(String.format(" done in %.3f seconds", AbbreviationUtils.toSeconds(startTime, stopTime))); }
        
        // serialzie if required
        if (null != outputFileName) {
            if (verbose) { System.out.print("serializing abbreviations ..."); }
            startTime = System.nanoTime();
            List<? extends Abbreviation> abbreviations = algorithm.getAbbreviations();
            // this should somehow must be debugged
            Collections.sort(abbreviations, Collections.reverseOrder());
            Serializer.toTextFile(
                                   outputFileName, 
                                   outputFileEncoding == null ? "UTF-8" : outputFileEncoding, 
                                   abbreviations
                                 );
            stopTime = System.nanoTime();
            if (verbose) { System.out.println(String.format(" done in %.3f seconds", AbbreviationUtils.toSeconds(startTime, stopTime))); }
        }
    }
    
    /** 
     * This method should be called from main method in production version.
     * From command line, the class can be invoked in the following way:
     * AbbreviationExtractorApp --ConnectorClass="some class" --Connector.Option1=value1 --Connector.Option2="value2" ... --AlgorithmClass="some class" --Algorithm.Option1=option1 ...
     */
    public static void run(String [] args) {
        try {
            Configuration config = AbbreviationUtils.commandLineArgsToConfiguration(args);
            AbbreviationExtractorEngine app = new AbbreviationExtractorEngine ();
            app.init(config);
            app.run();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void main( String[] args ) {
        
        // threshold based
        try {
            List<Abbreviation> goldPositiveAbbreviations = Serializer.fromTextFile
                    (
                        Paths.get(System.getProperty("user.dir"), "../../resources/abbreviations/abbrev-gold-simple-positive.txt").toString(), 
                        "UTF-8", 
                        Abbreviation.class
                    );
            List<CorpusAbbreviation> goldAbbreviations = Serializer.fromTextFile
                    (
                        Paths.get(System.getProperty("user.dir"), "../../resources/abbreviations/abbrev-gold-length.txt").toString(), 
                        "UTF-8",
                        CorpusAbbreviation.class
                    );
            AbbreviationEvaluator evaluator = new AbbreviationEvaluator(goldAbbreviations);
            
            MetaClassifierCorpusBuilder metaCorpusBuilder = new MetaClassifierCorpusBuilder (goldPositiveAbbreviations);
            
            // threshold based
            {
                String corpusFile = Paths.get(System.getProperty("user.dir"), "../../datasets/opencorpora/opencorpora.sent.train.ru").toString();
                Configuration config = new Configuration( new String [] 
                                                {   
                                                    "ConnectorClass", "org.csclub.abbrev.connectors.SentPerLineCorpusReader",
                                                    "Connector.FileName", corpusFile,
                                                    "Connector.FileEncoding", "UTF-8",

                                                    "AlgorithmClass", "org.csclub.abbrev.algorithms.tba.ThresholdBasedAlgorithm",
                                                    "Algorithm.Threshold", "32",
                                                } 
                                             );
                AbbreviationExtractorEngine app = new AbbreviationExtractorEngine ();
                app.init(config);
                app.run();
                
                metaCorpusBuilder.addClassifier(app.algorithm.getClass().getSimpleName(), app.algorithm.getAbbreviations());
                ConfusionMatrix matrix = evaluator.evaluate(app.algorithm.getAbbreviations());
                System.out.println("Threshold based: " + matrix);
            }
            
            //length based algorithm 
            {
                String corpusFile = Paths.get(System.getProperty("user.dir"), "../../datasets/opencorpora/opencorpora.sent.train.ru").toString();
                Configuration config = new Configuration(new String [] 
                                                {   
                                                    "ConnectorClass", "org.csclub.abbrev.connectors.SentPerLineCorpusReader",
                                                    "Connector.FileName", corpusFile,
                                                    "Connector.FileEncoding", "UTF-8",
                                                    "AlgorithmClass", "org.csclub.abbrev.algorithms.tba.LengthBasedAlgorithm",
                                                } 
                                             );
                AbbreviationExtractorEngine app = new AbbreviationExtractorEngine();
                app.init(config);
                app.run();
                
                ConfusionMatrix matrix = evaluator.evaluate(app.algorithm.getAbbreviations());
                System.out.println("Length based algorithm: " + matrix);
            }
            
            //t-test based algorithm
            {
                String corpusFile = Paths.get(System.getProperty("user.dir"), "../../datasets/opencorpora/opencorpora.sent.train.ru").toString();
                Configuration config = new Configuration(new String [] 
                                                {   
                                                    "ConnectorClass", "org.csclub.abbrev.connectors.SentPerLineCorpusReader",
                                                    "Connector.FileName", corpusFile,
                                                    "Connector.FileEncoding", "UTF-8",
                                                    "AlgorithmClass", "org.csclub.abbrev.algorithms.tba.TTestBasedAlgorithm",
                                                } 
                                             );
                AbbreviationExtractorEngine app = new AbbreviationExtractorEngine();
                app.init(config);
                app.run();
                
                metaCorpusBuilder.addClassifier(app.algorithm.getClass().getSimpleName(), app.algorithm.getAbbreviations());
                ConfusionMatrix matrix = evaluator.evaluate(app.algorithm.getAbbreviations());
                System.out.println("T-Test based: " + matrix);
            }
            
            //chi-square based algorithm        
            {
                String corpusFile = Paths.get(System.getProperty("user.dir"), "../../datasets/opencorpora/opencorpora.sent.train.ru").toString();
                Configuration config = new Configuration(new String [] 
                                                {   
                                                    "ConnectorClass", "org.csclub.abbrev.connectors.SentPerLineCorpusReader",
                                                    "Connector.FileName", corpusFile,
                                                    "Connector.FileEncoding", "UTF-8",
                                                    "AlgorithmClass", "org.csclub.abbrev.algorithms.tba.ChiSquareTestBasedAlgorithm",
                                                } 
                                             );
                AbbreviationExtractorEngine app = new AbbreviationExtractorEngine();
                app.init(config);
                app.run();
                
                metaCorpusBuilder.addClassifier(app.algorithm.getClass().getSimpleName(), app.algorithm.getAbbreviations());
                ConfusionMatrix matrix = evaluator.evaluate(app.algorithm.getAbbreviations());
                System.out.println("Chi sqare test based: " + matrix);
            }
            
            metaCorpusBuilder.buildARFF
                    ( 
                        Paths.get(System.getProperty("user.dir"), "../../resources/abbreviations/meta-classifier.arff").toString(),  
                        "UTF-8"
                    );
            
            //likelihood ratios based 
            {
                String corpusFile = Paths.get(System.getProperty("user.dir"), "../../datasets/opencorpora/opencorpora.sent.train.ru").toString();
                Configuration config = new Configuration(new String [] 
                                                {   
                                                    "ConnectorClass", "org.csclub.abbrev.connectors.SentPerLineCorpusReader",
                                                    "Connector.FileName", corpusFile,
                                                    "Connector.FileEncoding", "UTF-8",
                                                    "AlgorithmClass", "org.csclub.abbrev.algorithms.tba.LikelihoodRatiosBasedAlgorithm",
                                                } 
                                             );
                AbbreviationExtractorEngine app = new AbbreviationExtractorEngine();
                app.init(config);
                app.run();
                
                ConfusionMatrix matrix = evaluator.evaluate(app.algorithm.getAbbreviations());
                System.out.println("Likelihood ratios based: " + matrix);
            }
            
            //mutual information based 
            {
                String corpusFile = Paths.get(System.getProperty("user.dir"), "../../datasets/opencorpora/opencorpora.sent.train.ru").toString();
                Configuration config = new Configuration(new String [] 
                                                {   
                                                    "ConnectorClass", "org.csclub.abbrev.connectors.SentPerLineCorpusReader",
                                                    "Connector.FileName", corpusFile,
                                                    "Connector.FileEncoding", "UTF-8",
                                                    "AlgorithmClass", "org.csclub.abbrev.algorithms.tba.MutualInformationBasedAlgorithm",
                                                } 
                                             );
                AbbreviationExtractorEngine app = new AbbreviationExtractorEngine();
                app.init(config);
                app.run();
                
                ConfusionMatrix matrix = evaluator.evaluate(app.algorithm.getAbbreviations());
                System.out.println("Mutual information based: " + matrix);
            }
            
            /*{
                String corpusFile = Paths.get(System.getProperty("user.dir"), "../../resources/abbreviations/abbrev-gold.txt").toString();
                Configuration config = new Configuration( new String [] 
                                                {   
                                                    "ConnectorClass", "org.csclub.abbrev.algorithms.tba.impl.AbbreviationsCorpusReader",
                                                    "Connector.FileName", corpusFile,
                                                    "Connector.FileEncoding", "UTF-8",

                                                    "AlgorithmClass", "org.csclub.abbrev.algorithms.tba.FractionBasedAlgorithm"
                                                } 
                                             );
                AbbreviationExtractorApp app = new AbbreviationExtractorApp ();
                app.init(config);
                app.run();
                
                metaCorpusBuilder.addClassifier(app.algorithm.getClass().getSimpleName(), app.algorithm.getAbbreviations());
                ConfusionMatrix matrix = evaluator.evaluate(app.algorithm.getAbbreviations());
                System.out.println("Fraction based: " + matrix);
            }
           
            
            //next symbol based 
            {
                String corpusFile = Paths.get(System.getProperty("user.dir"), "../../datasets/opencorpora/opencorpora.sent.train.ru").toString();
                Configuration config = new Configuration(new String [] 
                                                {   
                                                    "ConnectorClass", "org.csclub.abbrev.connectors.SentPerLineCorpusReader",
                                                    "Connector.FileName", corpusFile,
                                                    "Connector.FileEncoding", "UTF-8",
                                                    "AlgorithmClass", "org.csclub.abbrev.algorithms.tba.NextSymbolBasedAlgorithm",
                                                } 
                                             );
                AbbreviationExtractorApp app = new AbbreviationExtractorApp();
                app.init(config);
                app.run();
                
                ConfusionMatrix matrix = evaluator.evaluate(app.algorithm.getAbbreviations());
                System.out.println("Next symbol based: " + matrix);
            }
            
            */
            
        } catch(Exception e) {
            e.printStackTrace(System.out);
        }
    }
}
