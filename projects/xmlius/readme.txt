ETR XML PoCs
[2014-04-07]

Subprojects

1. etr_jaxb (maven artifact id 'etr-jaxb-gen') 
Based on etr.xsd schema JAXB POJOs get generated and unit tests show how to use those generated classes.
This subproject isone of prototypes for future POX(Plain Old XML) and SOAP web services(where JAXB can be used internally).

2. etr_xsl (maven artifact id 'etr-xsl') 
XSLT and XSL-FO to PDF transformations

3. etr_parsing (maven artifact id 'etr-xml-parsing)' 
Different kinds of parser are used to read and write ETR XMLs. For now DOM, SAX, StAX are implemented.