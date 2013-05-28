/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.csclub.abbrev.graph;

import java.awt.Color;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
import org.apache.commons.io.IOUtils;
import org.math.plot.Plot2DPanel;
import org.math.plot.Plot3DPanel;

/**
 *
 * @author Sergey Serebryakov
 */
public class Plotter {
    
    public static double [] loadDoubleColumn(String fileName, String fileEncoding) throws IOException {
        List<String> lines = IOUtils.readLines(new FileInputStream(fileName), fileEncoding);
        double [] column = new double [lines.size()];
        for (int i=0; i<lines.size(); i++) {
            column[i] = Double.parseDouble(lines.get(i).trim());
        }
        return column;
    }
    
    public static double [][] loadDoubleColumns(String fileName, String fileEncoding, int columnsCount) throws IOException {
        List<String> lines = IOUtils.readLines(new FileInputStream(fileName), fileEncoding);
        double [][] columns = new double [columnsCount][];
        for (int i=0; i<columnsCount; i++) {
            columns[i] = new double [lines.size()];
        }
        for (int i=0; i<lines.size(); i++) {
            String [] values = lines.get(i).split("\t");
            for (int j=0; j<columnsCount; j++ )
            columns[j][i] = Double.parseDouble(values[j].trim());
        }
        return columns;
    }
    
    public static double [] loadAbbreviationsWeightsValues(String fileName, String fileEncoding) throws IOException {
        List<String> lines = IOUtils.readLines(new FileInputStream(fileName), fileEncoding);
        double [] y = new double [lines.size()];
        for (int i=0; i<lines.size(); i++) {
            String [] tokenWeight = lines.get(i).split("\t");
            y[i] = Double.parseDouble(tokenWeight[1].trim());
        }
        return y;
    }
    
    public static void main(String [] args) throws IOException {
        /*
        // load abberviation weights
        String fileName = Paths.get(System.getProperty("user.dir"), "../../datasets/brown-corpus/abbreviations/brown-likelihood.txt").toString();
        String fileEncoding = "UTF-8";
        double[] y = loadAbbreviationsWeightsValues(fileName, fileEncoding);
        Plot2DPanel plot = new Plot2DPanel();
        String title = "abbreviations weights";
        plot.addLinePlot(title, y);
        */
        
        /*
        // load precision or recall values
        String fileName = Paths.get(System.getProperty("user.dir"), "../../datasets/brown-corpus/experiments/FreeLing/config/precision-recall/results/brown-r.data").toString();
        String fileEncoding = "UTF-8";
        double[] y = loadDoubleColumn(fileName, fileEncoding);
        Plot2DPanel plot = new Plot2DPanel();
        String title = "r";
        plot.addLinePlot(title, y);
        //plot.addScatterPlot(title, y);
        */
        /*
        // load precision-recall curve
        String fileName = Paths.get(System.getProperty("user.dir"), "../../datasets/brown-corpus/experiments/FreeLing/config/precision-recall/results/brown-pr.data").toString();
        String fileEncoding = "UTF-8";
        double[][] pr = loadDoubleColumns(fileName, fileEncoding, 2);
        Plot2DPanel plot = new Plot2DPanel();
        String title = "precision-recall";
        // precision \t recall
        plot.addLinePlot(title, Color.BLACK, pr[1], pr[0]);
        plot.addScatterPlot(title, pr[1], pr[0]);
        */
        
        
        Plot3DPanel plot = new Plot3DPanel();
        double[] p = loadDoubleColumn(Paths.get(System.getProperty("user.dir"), "../../datasets/brown-corpus/experiments/FreeLing/config/precision-recall/results/brown-p.data").toString(), "UTF-8");
        double[] r = loadDoubleColumn(Paths.get(System.getProperty("user.dir"), "../../datasets/brown-corpus/experiments/FreeLing/config/precision-recall/results/brown-r.data").toString(), "UTF-8");
        double[] f1 = loadDoubleColumn(Paths.get(System.getProperty("user.dir"), "../../datasets/brown-corpus/experiments/FreeLing/config/precision-recall/results/brown-f1.data").toString(), "UTF-8");
        //plot.addScatterPlot("all", p, r, f1);
        plot.addLinePlot("all", p, r, f1);
        String title = "precision-recall-f1";
        
        JFrame frame = new JFrame(title);
        frame.setContentPane(plot);
        frame.setSize(600, 480);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
    
}
