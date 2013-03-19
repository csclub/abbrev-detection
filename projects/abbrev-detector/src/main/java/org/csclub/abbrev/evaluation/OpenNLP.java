/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.csclub.abbrev.evaluation;

import com.google.common.collect.Sets;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import opennlp.tools.dictionary.Dictionary;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.sentdetect.SentenceSample;
import opennlp.tools.sentdetect.SentenceSampleStream;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.Span;
import opennlp.tools.util.TrainingParameters;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author Sergey Serebryakov
 */
public class OpenNLP {
    
    
    public static class SentenceEvaluator extends Evaluator {
        
        public ConfusionMatrix evaluate(SentenceSample goldStandardSamples, SentenceSample actualSamples) {
            Set<String> goldStandardSentences = Sets.newHashSet();
            for (Span sentence : goldStandardSamples.getSentences()) {
                goldStandardSentences.add(String.format("%d-%d", sentence.getStart(), sentence.getEnd()));
            }
            
            Set<String> actualSentences = Sets.newHashSet();
            for (Span sentence : actualSamples.getSentences()) {
                actualSentences.add(String.format("%d-%d", sentence.getStart(), sentence.getEnd()));
            }
            
            return evaluate(goldStandardSentences, actualSentences);
        }
                
    }
    
    /** 
     * Learns the OpenNLP sentence splitter model.
     */
    public static SentenceModel learnSentenceSplitter( String language, String learningDataset, String learningDataEncoding, 
                                                       String abbrevDictFile, String abbrevDictFileEncoding, String outputModelFile, TrainingParameters predefinedParams) throws FileNotFoundException, IOException {
        
        TrainingParameters params;
        if (null != predefinedParams) {
            params = predefinedParams;
        } else {
            params = new TrainingParameters();
            params.put(TrainingParameters.CUTOFF_PARAM, "5");
            params.put(TrainingParameters.ITERATIONS_PARAM, "1000");
        }
			
        Dictionary abbrevDict = null;
        if (null != abbrevDictFile) {
            Reader reader = new InputStreamReader(new FileInputStream(abbrevDictFile), abbrevDictFileEncoding);
            abbrevDict = Dictionary.parseOneEntryPerLine(reader);
        }
			
        ObjectStream<String> lineStream = new PlainTextByLineStream(new FileInputStream(learningDataset), Charset.forName(learningDataEncoding));
        ObjectStream<SentenceSample> sampleStream = new SentenceSampleStream(lineStream);
        SentenceModel model = SentenceDetectorME.train( language, sampleStream, true, abbrevDict, params );
	
        if (null != outputModelFile) {
            OutputStream modelOut = new BufferedOutputStream(new FileOutputStream(outputModelFile));
            model.serialize(modelOut);
        }
        
        return model;
    }
    
    /** 
     * Splits the entire file into sentences. It is assumed, that we split on the per-line basis. 
     * Output file will contain a single sentence per line.
     */
    public static List<String> splitTextIntoSentences(String modelFile, String inputFile, String inputFileEncoding) throws FileNotFoundException, IOException {
        SentenceModel model = new SentenceModel( new FileInputStream(modelFile) );
        SentenceDetectorME sentenceDetector = new SentenceDetectorME(model);
        
        List<String> paragraphs = IOUtils.readLines(new FileInputStream(inputFile), inputFileEncoding);
        List<String> sentences = new ArrayList<> ();
        
        for (String paragraph : paragraphs) {
            String paragraphSentences [] = sentenceDetector.sentDetect(paragraph);
            sentences.addAll(Arrays.asList(paragraphSentences));
        }
        
        return sentences;
    }
    
    /** 
     * Evaluates sentence splitter model
     */
    public static ConfusionMatrix evaluateSentenceSplitter(String goldStandardDataset, String goldStandardDatasetEncoding, 
                                                           String actualDataset, String actualDatasetEncoding) throws FileNotFoundException, IOException {

        ObjectStream<SentenceSample> goldStandardStream =  new SentenceSampleStream ( new PlainTextByLineStream(new FileInputStream(goldStandardDataset).getChannel(), Charset.forName(goldStandardDatasetEncoding))  );
        SentenceSample goldStandardSamples = goldStandardStream.read();
        
        ObjectStream<SentenceSample> actualStream =  new SentenceSampleStream ( new PlainTextByLineStream(new FileInputStream(actualDataset).getChannel(), Charset.forName(actualDatasetEncoding))  );
        SentenceSample actualSamples = actualStream.read();

        return new SentenceEvaluator().evaluate(goldStandardSamples, actualSamples);
        
       
    }
    
    public static void convertTestSentencesIntoTestCorpus(String testSentencesFile, String testSentencesFileEncoding, String testCorpusFile, String testCorpusFileEncoding, int maxParagraphSize) throws FileNotFoundException, IOException {
        
        List<String> sentences = IOUtils.readLines(new FileInputStream(testSentencesFile), testSentencesFileEncoding);
        List<String> paragraphs = new ArrayList<> ();
        StringBuilder sb = new StringBuilder();
        int paragraphSize = 0;
        for (String sentence : sentences) {
            if (paragraphSize < maxParagraphSize) {
                if (paragraphSize != 0) {
                    sb.append(" ");
                }
                sb.append(sentence);
                paragraphSize ++;
                continue;
            }
            paragraphs.add(sb.toString());
            sb = new StringBuilder();
            
            sb.append(sentence);
            paragraphSize = 1;
        }
        
        if (paragraphSize != 0 ) {
            paragraphs.add(sb.toString());
        }
        
        IOUtils.writeLines(paragraphs, "\n", new FileOutputStream(testCorpusFile), testCorpusFileEncoding);
    }
    
}
