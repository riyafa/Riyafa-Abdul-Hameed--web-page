package org.riyafa;

import java.util.HashMap;
import java.util.Map;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.xpath.AXIOMXPath;
import org.apache.axiom.soap.SOAPBody;
import org.apache.synapse.MessageContext;
import org.apache.synapse.SynapseLog;
import org.apache.synapse.mediators.AbstractMediator;
import org.jaxen.JaxenException;


public class SurchargeStockQuoteMediator extends AbstractMediator {

    // symbol -> surcharge percentage
    private Map<String, Double> surcharges = new HashMap<String, Double>();
    // default surcharge percentage to use if symbol not found in surcharges map
    private Double defaultPercentage = 0d;

    public boolean mediate(MessageContext synCtx) {

        // establish log levels
        SynapseLog logger = getLog(synCtx);
        boolean traceOn = logger.isTraceOrDebugEnabled();
        boolean traceOrDebugOn = logger.isTraceOrDebugEnabled();

        // write log messages
        if (traceOrDebugOn) {
            logger.traceOrDebug("Start : SurchargeStockQuote mediator");

            if (traceOn && trace.isTraceEnabled()) {
                trace.trace("Message : " + synCtx.getEnvelope());
            }
        }

        // get symbol, last elements of SOAP envelope
        SOAPBody body = synCtx.getEnvelope().getBody();
        OMElement firstElement = body.getFirstElement();

        OMElement symbolElement = null;
        try {
            AXIOMXPath xPathSymbol = new AXIOMXPath("//ns:symbol");
            xPathSymbol.addNamespace("ns", "http://services.samples/xsd");
            symbolElement = (OMElement) xPathSymbol.selectSingleNode(firstElement);

        } catch (JaxenException e) {
            handleException("element symbol error", e, synCtx);
        }

        if (symbolElement == null) {
            handleException("element symbol not found", synCtx);
        }

        OMElement lastElement = null;
        try {
            AXIOMXPath xPathLast = new AXIOMXPath("//ns:last");
            xPathLast.addNamespace("ns", "http://services.samples/xsd");
            lastElement = (OMElement) xPathLast.selectSingleNode(firstElement);

        } catch (JaxenException e) {
            handleException("element last error", e, synCtx);
        }

        if (lastElement == null) {
            handleException("element last not found", synCtx);
        }

        // lookup surcharge percentage from surcharges map
        // if not found apply default surcharge percentage
        Double surchargePercentage = getDefaultPercentage();
        String symbol = symbolElement.getText();
        Double givenSurchargePercentage = surcharges.get(symbol);
        if (givenSurchargePercentage != null) {
            surchargePercentage = givenSurchargePercentage;
        }

        String text = lastElement.getText();
        Double price = Double.valueOf(text);

        Double newPrice = price
                + (price * surchargePercentage / 100);

        // write back new stock price
        lastElement.setText(String.valueOf(newPrice));

        // print log message
        log.info("symbol:" + symbol + " original price:" + price + " surcharge:"
                         + surchargePercentage + "% new price:" + newPrice);

        // write log messages
        if (traceOrDebugOn) {
            logger.traceOrDebug("End : SurchargeStockQuote mediator");
        }

        // proceed with next mediator
        return true;
    }

    public Double getDefaultPercentage() {
        return defaultPercentage;
    }

    public void setDefaultPercentage(double defaultPercentage) {
        this.defaultPercentage = defaultPercentage;
    }

    public Map<String, Double> getSurcharges() {
        return surcharges;
    }

    public void setSurcharges(Map<String, Double> surcharges) {
        this.surcharges = surcharges;
    }

    public void addSurcharge(String symbol, Double percentage) {
        this.surcharges.put(symbol, percentage);
    }
}

