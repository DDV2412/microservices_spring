//
// This file was generated by the Eclipse Implementation of JAXB, v2.3.7 
// See https://eclipse-ee4j.github.io/jaxb-ri 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2023.01.29 at 04:26:19 PM ICT 
//


package com.ipmugo.articleservice.schema.openarchives.oai.oai_dc;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.openarchives.oai._2_0.oai_dc package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Dc_QNAME = new QName("http://www.openarchives.org/OAI/2.0/oai_dc/", "dc");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.openarchives.oai._2_0.oai_dc
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link OaiDcType }
     * 
     */
    public OaiDcType createOaiDcType() {
        return new OaiDcType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OaiDcType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link OaiDcType }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.openarchives.org/OAI/2.0/oai_dc/", name = "dc")
    public JAXBElement<OaiDcType> createDc(OaiDcType value) {
        return new JAXBElement<OaiDcType>(_Dc_QNAME, OaiDcType.class, null, value);
    }

}
