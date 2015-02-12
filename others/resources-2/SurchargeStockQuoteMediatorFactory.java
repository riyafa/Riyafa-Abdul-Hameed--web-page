package org.riyafa;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.synapse.Mediator;
import org.apache.synapse.SynapseException;
import org.apache.synapse.config.xml.AbstractMediatorFactory;
import org.apache.synapse.config.xml.XMLConfigConstants;

import java.util.Iterator;
import java.util.Properties;
import javax.xml.namespace.QName;

public class SurchargeStockQuoteMediatorFactory extends AbstractMediatorFactory {

    static final QName SURCHARGE_STOCK_QUOTE_Q = new QName(
            XMLConfigConstants.SYNAPSE_NAMESPACE, "surchargeStockQuote");

    static final QName DEFAULT_PERCENTAGE_Q = new QName(
            XMLConfigConstants.SYNAPSE_NAMESPACE, "defaultPercentage");

    static final QName SURCHARGES_Q = new QName(
            XMLConfigConstants.SYNAPSE_NAMESPACE, "surcharges");

    static final QName SURCHARGE_Q = new QName(
            XMLConfigConstants.SYNAPSE_NAMESPACE, "surcharge");

    static final QName SYMBOL_Q = new QName("symbol");

    public Mediator createSpecificMediator(OMElement elem, Properties properties) {

        // create new mediator
        SurchargeStockQuoteMediator newMediator = new SurchargeStockQuoteMediator();

        // setup initial settings
        processTraceState(newMediator, elem);

        // read default surcharge percentage
        OMElement defaultPercentageElement = elem.getFirstChildWithName(DEFAULT_PERCENTAGE_Q);
        if (defaultPercentageElement != null) {
            Double defaultPercentage = Double.valueOf(defaultPercentageElement.getText());
            newMediator.setDefaultPercentage(defaultPercentage);

        } else {
            throw new SynapseException("default percentage element missing");
        }

        // read given surcharges and add them to the map of the mediator
        // symbol -> percentage
        OMElement surchargesElement = elem.getFirstChildWithName(SURCHARGES_Q);
        if(surchargesElement != null) {
            Iterator surchargesIter = surchargesElement.getChildrenWithName(SURCHARGE_Q);
            while (surchargesIter.hasNext()) {
                OMElement surchargeElem = (OMElement) surchargesIter.next();
                OMAttribute symbolAtr = surchargeElem.getAttribute(SYMBOL_Q);
                if(symbolAtr != null) {
                    String symbol = symbolAtr.getAttributeValue();

                    String percentageValue = surchargeElem.getText();
                    Double percentage = Double.valueOf(percentageValue);

                    if(symbol != null) {
                        newMediator.addSurcharge(symbol, percentage);
                    }
                } else {
                    throw new SynapseException("symbol attribute missing");
                }
            }
        }

        return newMediator;
    }

    public QName getTagQName() {
        return SURCHARGE_STOCK_QUOTE_Q;
    }
}