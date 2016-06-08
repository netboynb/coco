package com.ms.coco.serialization;

import org.restexpress.serialization.xml.XstreamXmlProcessor;

import com.ms.coco.echo.controller.DelayResponse;

public class XmlSerializationProcessor
extends XstreamXmlProcessor
{
	public XmlSerializationProcessor()
    {
	    super();
		alias("delay_response", DelayResponse.class);
    }
}
