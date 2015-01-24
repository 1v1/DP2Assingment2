//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.01.24 at 05:55:28 PM CET 
//


package it.polito.dp2.FDS.sol2.jaxb;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for FlightInfoType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FlightInfoType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="flight" type="{http://www.example.org/FDSInfo}FlightType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="aircraftSet" type="{http://www.example.org/FDSInfo}AircraftSetType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FlightInfoType", propOrder = {
    "flight",
    "aircraftSet"
})
public class FlightInfoType {

    protected List<FlightType> flight;
    protected AircraftSetType aircraftSet;

    /**
     * Gets the value of the flight property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the flight property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFlight().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FlightType }
     * 
     * 
     */
    public List<FlightType> getFlight() {
        if (flight == null) {
            flight = new ArrayList<FlightType>();
        }
        return this.flight;
    }

    /**
     * Gets the value of the aircraftSet property.
     * 
     * @return
     *     possible object is
     *     {@link AircraftSetType }
     *     
     */
    public AircraftSetType getAircraftSet() {
        return aircraftSet;
    }

    /**
     * Sets the value of the aircraftSet property.
     * 
     * @param value
     *     allowed object is
     *     {@link AircraftSetType }
     *     
     */
    public void setAircraftSet(AircraftSetType value) {
        this.aircraftSet = value;
    }

}
