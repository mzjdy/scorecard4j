package io.scorecard4j.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.scorecard4j.binning.bin.Bin;
import io.scorecard4j.training.LRModeler;
import io.scorecard4j.transformation.woe.WoeIvCalculator.FeatureWoe;
import io.scorecard4j.util.NumberFormatUtil;

/**
 * Scorecard builder on top of machine learning model
 * 
 * @author rayeaster
 *
 */
public class Scorer {

    private double alpha;
    private double beta;
    private List<Map<String, Double>> scores;
    private List<List<String>> binNames;
    private Double startScore;

    /**
     * constructor
     * 
     * @param baseScore
     *            base score to start with
     * @param baseOdds
     *            good to bad ratio for base score
     * @param pdo
     *            score addition if odds get doubled
     * @param modeler
     *            {@link LRModeler} for this scorecard
     */
    public Scorer(double baseScore, double baseOdds, double pdo, LRModeler modeler) {
        assert (baseScore > 0);
        assert (baseOdds > 0);
        assert (pdo > 0);

        beta = pdo / Math.log(2);
        alpha = baseScore + beta * Math.log(baseOdds);

        double[] weights = modeler.getWeights();
        Map<Integer, FeatureWoe> woes = modeler.getWoes();

        startScore = alpha - beta * weights[weights.length - 1];

        scores = new ArrayList<Map<String, Double>>(weights.length - 1);
        binNames = new ArrayList<List<String>>(weights.length - 1);

        for (int i = 0; i < weights.length - 1; i++) {
            FeatureWoe woe = woes.get(i);
            Map<String, Double> binScores = new HashMap<String, Double>();
            List<String> binName = new ArrayList<String>();
            scores.add(binScores);
            binNames.add(binName);

            List<? extends Bin> bins = woe.bins();
            for (int j = 0; j < bins.size(); j++) {
                Bin bin = bins.get(j);
                double woeValue = woe.woe(bin);
                binScores.put(bin.toString(), (0 - beta * woeValue * weights[i]));
                binName.add(bin.toString());
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("StartScore=").append(NumberFormatUtil.formatTo2DigitsAfterDecimal(startScore)).append("\n");
        for (int i = 0; i < scores.size(); i++) {
            Map<String, Double> score = scores.get(i);
            sb.append(">>>Feature").append(i).append(":\n");
            for (String binName : binNames.get(i)) {
                sb.append(">>>>>>").append(binName).append("=")
                        .append(NumberFormatUtil.formatTo2DigitsAfterDecimal(score.get(binName))).append("\n");
            }
        }
        return sb.toString();
    }

}