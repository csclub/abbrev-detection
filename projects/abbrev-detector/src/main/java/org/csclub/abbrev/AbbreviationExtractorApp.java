package org.csclub.abbrev;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.csclub.abbrev.algorithms.Algorithm;
import org.csclub.abbrev.algorithms.tba.impl.Delimiter;
import org.csclub.abbrev.connectors.CorpusReader;
import org.csclub.abbrev.evaluation.AbbreviationEvaluator;
import org.csclub.abbrev.evaluation.ConfusionMatrix;
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
    
    public ConfusionMatrix delimiterAnalysis(final List<Abbreviation> goldAbbreviations) {
        Delimiter.setShareOfAbbreviations(goldAbbreviations);
        
        List<Abbreviation> trueAbbreviations = new ArrayList();
        
        for (int i = 0; i < goldAbbreviations.size(); ++i) {
            Abbreviation cur = goldAbbreviations.get(i);
            if (i <= Delimiter.shareOfAbbreviations * goldAbbreviations.size()) {
                System.out.println(i + 1 + "\tOK\t" + cur.toString(-1));
                
            }
            else {
                System.out.println(i + 1 + "\tNOT\t" + cur.toString(-1));
            }
            
            if (goldAbbreviations.get(i).getAbbrevState() == Abbreviation.AbbrevState.True) {
                trueAbbreviations.add(goldAbbreviations.get(i));
            }
          
        }
        System.out.println();
        
        AbbreviationEvaluator evaluator = new AbbreviationEvaluator(trueAbbreviations);
        return evaluator.evaluate(Delimiter.probablyAbbreviations(goldAbbreviations));
    }
    
    public ConfusionMatrix constLimitAnalysis(final List<Abbreviation> goldAbbreviations, int lim) {
        List<Abbreviation> trueAbbreviations = new ArrayList();
        List<Abbreviation> positiveAbbreviations = new ArrayList();
        
        
        for (int i = 0; i < goldAbbreviations.size(); ++i) {
            Abbreviation cur = goldAbbreviations.get(i);
            if (cur.getAbbrevCount() >= lim) {
                System.out.println(i + 1 + "\tOK\t" + cur.toString(-1));
                positiveAbbreviations.add(cur);
            }
            else {
                System.out.println(i + 1 + "\tNOT\t" + cur.toString(-1));
            }
            
            if (goldAbbreviations.get(i).getAbbrevState() == Abbreviation.AbbrevState.True) {
                trueAbbreviations.add(goldAbbreviations.get(i));
            }
        }
        System.out.println();
        
        AbbreviationEvaluator evaluator = new AbbreviationEvaluator(trueAbbreviations);
        return evaluator.evaluate(positiveAbbreviations);
    }
    
    public void collate(String fileName, String encoding) throws FileNotFoundException, UnsupportedEncodingException, IOException {
       /* 
        List<Abbreviation> goldAbbreviations = new Serializer().fromTextFile(fileName, encoding);
        
        abbrevCounter.onNewAbbreviations(goldAbbreviations);
        abbrevCounter.corpusProcessComplete();
        
        delimiterAnalysis(goldAbbreviations).print("Delimiter algorithm:");
        constLimitAnalysis(goldAbbreviations, 2).print("Const limit algorithm:");
        constLimitAnalysis(goldAbbreviations, 0).print("Zero limit algorithm:");
        */
    }
    
    public static void main( String[] args ) {
        
        // threshold based
        try {
            
            List<Abbreviation> goldAbbreviations = Serializer.fromTextFile
                    (
                        Paths.get(System.getProperty("user.dir"), "../../resources/abbreviations/abbrev-gold.txt").toString(), 
                        "UTF-8"
                    );
            AbbreviationEvaluator evaluator = new AbbreviationEvaluator(goldAbbreviations);
            
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
                
                ConfusionMatrix matrix = evaluator.evaluate(app.algorithm.getAbbreviations());
                System.out.println("Threshold based: " + matrix);
            }
            
            {
                String corpusFile = Paths.get(System.getProperty("user.dir"), "../../resources/abbreviations/abbrev-gold.txt").toString();
                Configuration config = new Configuration( new String [] 
                                                {   
                                                    "ConnectorClass", "org.csclub.abbrev.connectors.AbbreviationsCorpusReader",
                                                    "Connector.FileName", corpusFile,
                                                    "Connector.FileEncoding", "UTF-8",

                                                    "AlgorithmClass", "org.csclub.abbrev.algorithms.tba.FractionBasedAlgorithm"
                                                } 
                                             );
                AbbreviationExtractorApp app = new AbbreviationExtractorApp ();
                app.init(config);
                app.run();
                
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
                
                ConfusionMatrix matrix = evaluator.evaluate(app.algorithm.getAbbreviations());
                System.out.println("T-Test based: " + matrix);
            }
            
        } catch(Exception e) {
            e.printStackTrace(System.out);
        }
    }
}
