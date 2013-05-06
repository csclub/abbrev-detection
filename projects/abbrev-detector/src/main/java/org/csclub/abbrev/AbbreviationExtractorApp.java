package org.csclub.abbrev;

import java.nio.file.Paths;
import java.util.List;
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
public class AbbreviationExtractorApp  extends Component {
    
    @ConfigurationParameter(name="ConnectorClass", mandatory = true)
    private String connectorClassName;
    @ConfigurationParameter(name="AlgorithmClass", mandatory = true)
    private String algorithmClassName;
    
    private CorpusReader corpusReader;
    private Algorithm algorithm;
    
    @Override
    public void init(Configuration storage) throws Exception {
        super.init(storage);
        
        corpusReader = (CorpusReader)AbbreviationUtils.createClassInstance(connectorClassName);
        corpusReader.init(storage.getOnlyForNamespace("Connector"));
        
        algorithm = (Algorithm)AbbreviationUtils.createClassInstance(algorithmClassName);
        algorithm.init(storage.getOnlyForNamespace("Algorithm"));
    }
    
    public void run() throws Exception {
        // read entire corpus
        Corpus corpus = corpusReader.read();
        
        // run final algorithm steps
        algorithm.run(corpus);
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
                        Paths.get(System.getProperty("user.dir"), "../../resources/abbreviations/abbrev-gold.txt").toString(), 
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
                                                    "Algorithm.Threshold", "2",
                                                } 
                                             );
                AbbreviationExtractorApp app = new AbbreviationExtractorApp ();
                app.init(config);
                app.run();
                
                metaCorpusBuilder.addClassifier(app.algorithm.getClass().getSimpleName(), app.algorithm.getAbbreviations());
                ConfusionMatrix matrix = evaluator.evaluate(app.algorithm.getAbbreviations());
                System.out.println("Threshold based: " + matrix);
            }
            
            {
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
                AbbreviationExtractorApp app = new AbbreviationExtractorApp();
                app.init(config);
                app.run();
                
                metaCorpusBuilder.addClassifier(app.algorithm.getClass().getSimpleName(), app.algorithm.getAbbreviations());
                ConfusionMatrix matrix = evaluator.evaluate(app.algorithm.getAbbreviations());
                System.out.println("T-Test based: " + matrix);
            }
            
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
                AbbreviationExtractorApp app = new AbbreviationExtractorApp();
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
                AbbreviationExtractorApp app = new AbbreviationExtractorApp();
                app.init(config);
                app.run();
                
                ConfusionMatrix matrix = evaluator.evaluate(app.algorithm.getAbbreviations());
                System.out.println("Mutual information based: " + matrix);
            }
            
        } catch(Exception e) {
            e.printStackTrace(System.out);
        }
    }
}
