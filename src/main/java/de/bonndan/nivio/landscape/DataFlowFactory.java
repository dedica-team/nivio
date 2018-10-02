package de.bonndan.nivio.landscape;

import java.util.List;

public class DataFlowFactory {

    public DataFlow createFrom (DataFlowItem dataFlowItem, List<Service> serviceList) {

        DataFlow dataFlow = new DataFlow();
        dataFlow.setDescription(dataFlowItem.getDescription());
        dataFlow.setFormat(dataFlowItem.getFormat());
        dataFlow.setSourceEntity(Utils.pick(dataFlowItem.getSource(), serviceList));
        dataFlow.setTargetEntity(Utils.pick(dataFlowItem.getSource(), serviceList));
        return dataFlow;
    }
}
