package com.mtuity.sensordetections;

import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

/**
 * Created by kalyani on 21/8/17.
 */

public class ChartModel {
    private XYMultipleSeriesDataset dataset;
    private XYMultipleSeriesRenderer multiRenderer;

    public XYMultipleSeriesRenderer getMultiRenderer() {
        return multiRenderer;
    }

    public void setMultiRenderer(XYMultipleSeriesRenderer multiRenderer) {
        this.multiRenderer = multiRenderer;
    }

    public XYMultipleSeriesDataset getDataset() {
        return dataset;
    }

    public void setDataset(XYMultipleSeriesDataset dataset) {
        this.dataset = dataset;
    }

    @Override
    public String toString() {
        return "DataSet " + dataset.toString() + "  Multirender: " + multiRenderer;
    }
}
