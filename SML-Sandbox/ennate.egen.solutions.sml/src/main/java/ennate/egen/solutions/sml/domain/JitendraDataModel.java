package ennate.egen.solutions.sml.domain;


import lombok.Getter;
import lombok.Setter;
import java.util.List;

/**
 * Created by jmalakalapalli on 4/10/17.
 */
@Getter
@Setter
public class JitendraDataModel {
    private int numberOfFields;
    private Data mean;
    private Data stdDev;
    private String classId;

    public String toString() {
        return " Number of Fields = " + numberOfFields + " Mean Vector => " + mean.toString() + " StdDev Vector => "
                + stdDev.toString() + " ClassId = " + classId;

    }

    JitendraDataModel(List<Data> data, int numberOfFields) {

        if (data.isEmpty()) {
            return;
        }

        this.classId = data.get(0).getClassId();

        this.numberOfFields = numberOfFields;

        /* compute mean and stDev*/
        mean = new Data(numberOfFields);
        stdDev = new Data(numberOfFields);

        /* compute aggregate */

        Double[] aggregateFields = new Double[numberOfFields];
        for (int i = 0; i < numberOfFields; i++) {
            aggregateFields[i] = 0.0d;
        }

        for (int i = 0; i < data.size(); i++) {
            Data d = data.get(i);
            Double[] fields = d.getFields();
            for (int j = 0; j < fields.length; j++) {
                aggregateFields[j] += fields[j];
            }
        }

        /* compute mean */
        for (int i = 0; i < numberOfFields; i++) {
            aggregateFields[i] /= (double) data.size();
        }

        mean.setFields(aggregateFields);

        /* for standard deviation. compute sum of squared of differences of each data point from its mean */

        Double[] standardDeviationFields = new Double[numberOfFields];
        for (int i = 0; i < numberOfFields; i++) {
            standardDeviationFields[i] = 0.0d;
        }

        for (int i = 0; i < data.size(); i++) {
            Data d = data.get(i);
            Double[] fields = d.getFields();
            for (int j = 0; j < fields.length; j++) {
                standardDeviationFields[j] += (fields[j] - mean.getFields()[j]) * (fields[j] - mean.getFields()[j]);
            }
        }


        /* compute standard deviation */
        for (int i = 0; i < numberOfFields; i++) {
            standardDeviationFields[i] /= (double) data.size() - 1;
            standardDeviationFields[i] = Math.sqrt(standardDeviationFields[i]);
        }

        stdDev.setFields(standardDeviationFields);

    }
}
