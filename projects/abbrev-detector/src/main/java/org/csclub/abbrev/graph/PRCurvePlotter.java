package org.csclub.abbrev.graph;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.math.plot.Plot2DPanel;

/**
 *
 * @author Fedor Amosov
 */
public class PRCurvePlotter {
    static double[] x;
    static double[] y;
    
    public static void loadValues(String fileName, String fileEncoding) throws IOException {
        List<String> lines = IOUtils.readLines(new FileInputStream(fileName), fileEncoding);
        
        Map<Double, Double> rocCurve = new TreeMap();
        rocCurve.put(0.0, 1.0);
        rocCurve.put(1.0, 0.0);
        
        for (int i = 0; i < lines.size(); ++i) {
            String[] matrix = lines.get(i).split("\t");
            if (matrix.length != 3) {
                throw new IOException();
            }

            int tp = Integer.parseInt(matrix[0]);
            int fp = Integer.parseInt(matrix[1]);
            int fn = Integer.parseInt(matrix[2]);
            
            double p = 1.0 * tp / (tp + fp);
            double r = 1.0 * tp / (tp + fn);
            
            rocCurve.put(p, r);
        }
        
        x = ArrayUtils.toPrimitive(rocCurve.keySet().toArray(new Double[0]));
        y = ArrayUtils.toPrimitive(rocCurve.values().toArray(new Double[0]));
    }
    
    public static void main(String [] args) throws IOException {
        
        String fileName = Paths.get(System.getProperty("user.dir"), "../../datasets/europarlament/sen-split-results/en/europarl-v7.fr-en.en-likelihood.txt").toString();
        String fileEncoding = "UTF-8";
        
        loadValues(fileName, fileEncoding);
 
        // create your PlotPanel (you can use it as a JPanel)
        Plot2DPanel plot = new Plot2DPanel();
 
        // add a line plot to the PlotPanel
        plot.addLinePlot("europarl-v7.fr-en.en-likelihood", x, y);
        
        // put the PlotPanel in a JFrame, as a JPanel
        JFrame frame = new JFrame("roc curve");
        frame.setContentPane(plot);
        frame.setSize(600, 480);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
