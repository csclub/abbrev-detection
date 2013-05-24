/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.csclub.abbrev.graph;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
import org.apache.commons.io.IOUtils;
import org.math.plot.Plot2DPanel;

/**
 *
 * @author Sergey Serebryakov
 */
public class Plotter {
    
    public static double [] loadValues(String fileName, String fileEncoding) throws IOException {
        List<String> lines = IOUtils.readLines(new FileInputStream(fileName), fileEncoding);
        double [] y = new double [lines.size()];
        for (int i=0; i<lines.size(); i++) {
            String [] tokenWeight = lines.get(i).split("\t");
            y[i] = Double.parseDouble(tokenWeight[1].trim());
        }
        return y;
    }
    
    public static void main(String [] args) throws IOException {
        
        String fileName = Paths.get(System.getProperty("user.dir"), "../../datasets/europarlament/abbreviations/en/europarl-v7.fr-en.en-chi.txt").toString();
        String fileEncoding = "UTF-8";
        
        double[] y = loadValues(fileName, fileEncoding);
 
        // create your PlotPanel (you can use it as a JPanel)
        Plot2DPanel plot = new Plot2DPanel();
 
        // add a line plot to the PlotPanel
        plot.addLinePlot("europarl-v7.fr-en.en-threshold", y);
 
        // put the PlotPanel in a JFrame, as a JPanel
        JFrame frame = new JFrame("a plot panel");
        frame.setContentPane(plot);
        frame.setSize(600, 480);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
    
}
