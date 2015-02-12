package org.riyafa;

import org.apache.axiom.om.OMElement;
import org.apache.synapse.Mediator;
import org.apache.synapse.config.xml.AbstractMediatorSerializer;

import java.util.Map;

public class SurchargeStockQuoteMediatorSerializer extends
                                                   AbstractMediatorSerializer {

    public OMElement serializeSpecificMediator(Mediator m) {

        if (!(m instanceof SurchargeStockQuoteMediator)) {
            handleException("Unsupported mediator passed in for serialization : "
                                    + m.getType());
        }

        SurchargeStockQuoteMediator mediator = (SurchargeStockQuoteMediator) m;
        OMElement surchargeStockQuoteElement = fac
                .createOMElement(SurchargeStockQuoteMediatorFactory.SURCHARGE_STOCK_QUOTE_Q);


        saveTracingState(surchargeStockQuoteElement, mediator);

        // add default percentage element
        OMElement defaultElement = fac.createOMElement(
                SurchargeStockQuoteMediatorFactory.DEFAULT_PERCENTAGE_Q, surchargeStockQuoteElement);
        defaultElement.setText(String.valueOf(mediator.getDefaultPercentage()));

        OMElement surchargesElement = fac
                .createOMElement(SurchargeStockQuoteMediatorFactory.SURCHARGES_Q, surchargeStockQuoteElement);

        // add symbol -> percentage list
        Map surcharges = mediator.getSurcharges();
        for (String symbol : mediator.getSurcharges().keySet()) {
            Double percentage = (Double) surcharges.get(symbol);

            OMElement surchargeElement = fac
                    .createOMElement(SurchargeStockQuoteMediatorFactory.SURCHARGE_Q, surchargesElement);

            surchargeElement.addAttribute(fac.createOMAttribute("symbol", nullNS, symbol));

            surchargeElement.setText(String.valueOf(percentage));
        }

        return surchargeStockQuoteElement;
    }

    public String getMediatorClassName() {
        return SurchargeStockQuoteMediator.class.getName();
    }
}
