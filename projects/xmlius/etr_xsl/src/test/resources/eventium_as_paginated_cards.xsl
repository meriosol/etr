<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format"
                version="1.0" xmlns:e="http://com/meriosol/etr/schema">
    <xsl:template match="/">
        <fo:root>
            <fo:layout-master-set>
                <fo:simple-page-master master-name="A4_Eventium" page-width="297mm"
                                       page-height="210mm" margin-top="1cm" margin-bottom="1cm"
                                       margin-left="1cm" margin-right="1cm">
                    <fo:region-body margin="1cm"/>
                    <fo:region-before extent="0.5cm"/>
                    <fo:region-after extent="0.5cm"/>
                    <fo:region-start extent="0.5cm"/>
                    <fo:region-end extent="0.5cm"/>
                </fo:simple-page-master>
            </fo:layout-master-set>

            <fo:page-sequence master-reference="A4_Eventium">
                <!-- Header -->
                <fo:static-content flow-name="xsl-region-before">
                    <fo:block text-align="start" font-size="8pt" font-family="sans-serif" font-style="oblique"
                              line-height="8pt">
                        ETR - Event Cards
                    </fo:block>
                </fo:static-content>
                <!-- Footer -->
                <fo:static-content flow-name="xsl-region-after">
                    <fo:block text-align="end" font-size="10pt" font-family="sans-serif" line-height="12pt">
                        p.
                        <fo:page-number/>
                    </fo:block>
                </fo:static-content>
                <fo:flow flow-name="xsl-region-body">
                    <xsl:apply-templates/>
                </fo:flow>
            </fo:page-sequence>
        </fo:root>
    </xsl:template>
    <xsl:template match="e:events/e:event">
        <fo:block break-after="page">
            <fo:block text-indent="5mm" font-family="sans-serif" font-size="16pt">
                Event info:
            </fo:block>

            <fo:block font-size="14pt" font-family="sans-serif" color="orange"
                      space-before="5mm" space-after="5mm">
                ID:
                <xsl:value-of select="@id"/>
            </fo:block>

            <fo:block>
                Severity:
                <xsl:value-of select="@severity"/>
            </fo:block>

            <fo:block>
                Title:
                <xsl:value-of select="e:title"/>
            </fo:block>

            <fo:block>
                Category:
                <xsl:value-of select="e:event-category/@code"/>
                <xsl:call-template name="show_message_in_brackets">
                    <xsl:with-param name="message" select="e:event-category/e:name"/>
                </xsl:call-template>
            </fo:block>

            <fo:block>
                Source:
                <xsl:value-of select="e:source"/>
            </fo:block>

            <fo:block>
                Process:
                <xsl:value-of select="e:process-id"/>
            </fo:block>

            <fo:block>
                Created:
                <xsl:value-of select="@created"/>
            </fo:block>
        </fo:block>
    </xsl:template>

    <xsl:template name="show_message_in_brackets">
        <xsl:param name="message"/>
        <!--<xsl:if test="normalize-space($message) != ''">-->
        <xsl:if test="string-length(normalize-space($message)) > 0">
            (<xsl:value-of select="$message"/>)
        </xsl:if>
    </xsl:template>

</xsl:stylesheet>
