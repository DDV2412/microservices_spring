//
// This file was generated by the Eclipse Implementation of JAXB, v2.3.7 
// See https://eclipse-ee4j.github.io/jaxb-ri 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2023.01.29 at 04:26:19 PM ICT 
//


package com.ipmugo.articleservice.schema.openarchives.oai.oai_marc;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{http://www.openarchives.org/OAI/1.1/oai_marc}subfield" maxOccurs="unbounded"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="id" use="required" type="{http://www.openarchives.org/OAI/1.1/oai_marc}idType" /&gt;
 *       &lt;attribute name="i1" use="required" type="{http://www.openarchives.org/OAI/1.1/oai_marc}iType" /&gt;
 *       &lt;attribute name="i2" use="required" type="{http://www.openarchives.org/OAI/1.1/oai_marc}iType" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "subfield"
})
@XmlRootElement(name = "varfield")
public class Varfield {

    @XmlElement(required = true)
    protected List<Subfield> subfield;
    @XmlAttribute(name = "id", required = true)
    protected String id;
    @XmlAttribute(name = "i1", required = true)
    protected String i1;
    @XmlAttribute(name = "i2", required = true)
    protected String i2;

    /**
     * Gets the value of the subfield property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the subfield property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSubfield().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Subfield }
     * 
     * 
     */
    public List<Subfield> getSubfield() {
        if (subfield == null) {
            subfield = new ArrayList<Subfield>();
        }
        return this.subfield;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the i1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getI1() {
        return i1;
    }

    /**
     * Sets the value of the i1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setI1(String value) {
        this.i1 = value;
    }

    /**
     * Gets the value of the i2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getI2() {
        return i2;
    }

    /**
     * Sets the value of the i2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setI2(String value) {
        this.i2 = value;
    }

}
