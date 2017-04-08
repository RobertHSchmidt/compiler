/*
 * Copyright 2014, Hridesh Rajan, Robert Dyer,
 *                 and Iowa State University of Science and Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package boa.aggregators.ml;

import boa.BoaTup;
import boa.aggregators.AggregatorSpec;
import boa.aggregators.FinishedException;
import weka.classifiers.functions.SMO;

import java.io.IOException;

/**
 * A Boa aggregator for training the model using SMO.
 *
 * @author ankuraga
 */
@AggregatorSpec(name = "smo", formalParameters = {"string"})
public class SMOAggregator extends MLAggregator {
    private SMO model;

    public SMOAggregator() {
    }

    public SMOAggregator(final String s) {
        super(s);
    }

    @Override
    public void aggregate(String data, String metadata) throws NumberFormatException, IOException, InterruptedException {
        aggregate(data, metadata, "SMO");
    }

    public void aggregate(final BoaTup data, final String metadata) throws IOException, InterruptedException, FinishedException, IllegalAccessException {
        aggregate(data, metadata, "SMO");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void finish() throws IOException, InterruptedException {
        System.out.println("finished called");
        try {
            this.model = new SMO();
            this.model.setOptions(options);
            this.model.buildClassifier(this.trainingSet);
            System.out.println("building done");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("finished");
//        saveModel(this.model);
//		this.saveTrainingSet(this.trainingSet);
        collect(this.model.toString());
    }
}