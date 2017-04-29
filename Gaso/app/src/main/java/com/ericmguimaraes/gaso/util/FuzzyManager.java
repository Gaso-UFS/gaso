package com.ericmguimaraes.gaso.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.rule.LinguisticTerm;
import net.sourceforge.jFuzzyLogic.rule.Variable;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static com.google.android.gms.wearable.DataMap.TAG;

/**
 * Created by adrianodias on 3/19/17.
 */
public class FuzzyManager {

    private static FuzzyManager ourInstance = new FuzzyManager();

    private final String filename = "gaso.fcl";
    private FIS fis;
    private String[] colors = {"#6dc066", "#b4eeb4", "#ffdd50", "#ffa500", "#d9534f", "#a6a6a6"};

    public static FuzzyManager getInstance() {
        return ourInstance;
    }

    private FuzzyManager() {

    }

    public String getCor(String str) {
        switch (str) {
            case "muito_baixo":
                return colors[0];
            case "baixo":
                return colors[1];
            case "medio":
                return colors[2];
            case "alto":
                return colors[3];
            case "muito_alto":
                return colors[4];
            default:
                return colors[5];
        }
    }

    public String getConsumo(String str) {
        switch (str) {
            case "muito_baixo":
                return "Consumo Muito Baixo";
            case "baixo":
                return "Consumo Baixo";
            case "medio":
                return "Consumo Médio";
            case "alto":
                return "Consumo Alto";
            case "muito_alto":
                return "Consumo Muito Alto";
            default:
                return "Nível de consumo indisponível";
        }
    }

    public String processFuzzy(Context context, Double velocidade, Double rpm, Double acelerador) {

        try {
            AssetManager assetManager = context.getAssets();
            InputStream is = assetManager.open(filename);
            fis = FIS.load(is, true);
            if (fis == null) {
                Log.wtf(TAG, "Arquivo FDL não encontrado");
            }
            fis.setVariable("velocidade", velocidade);
            fis.setVariable("rpm", rpm);
            fis.setVariable("acelerador", acelerador);
            fis.evaluate();
            Variable consumo = fis.getVariable("consumo");

            LinguisticTerm foundTerm = null;
            Double foundMembership = 0.0;
            List<LinguisticTerm> terms = consumo.linguisticTermsSorted();
            for (LinguisticTerm term : terms) {
                Double membership = consumo.getMembership(term.getTermName());
                if (membership > foundMembership) {
                    foundTerm = term;
                    foundMembership = membership;
                }
            }
            return foundTerm.getTermName();
        } catch (IOException e) {
            Log.e(TAG, "Não foi possível carregar o arquivo fcl");
            return "";
        }

    }

}
