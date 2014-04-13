<?xml version="1.0" encoding="UTF-8"?>

<!-- NOTE: Well, the most browsers don't support 2.0 yet..
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xpath-default-namespace="http://com/meriosol/etr/schema">
-->
<xsl:stylesheet version="1.0" xmlns:e="http://com/meriosol/etr/schema" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="html" indent="yes" encoding="utf-8"/>
    <xsl:template match="/">
        <html>
            <head>
                <title>ETR - Events grid sample</title>
                <style>
                    body {
                    margin-left: 10;
                    margin-right: 10;
                    font:normal 80% arial,helvetica,sanserif;
                    background-color:#FFFFFF;
                    color:#000000;
                    }
                    .even_row td {
                    background: #eafff3;
                    }
                    .odd_row td {
                    background: #fffddd;
                    }
                    th, td {
                    text-align: left;
                    vertical-align: top;
                    }
                    th {
                    font-weight:bold;
                    background: #f9dbe8;
                    color: black;
                    }

                    table, th, td {
                    font-size:100%;
                    border: none
                    }
                </style>
            </head>

            <body>
                <h2>Events</h2>

                <table border="1">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Severity</th>
                            <th>Title</th>
                            <th>Category</th>
                            <th>Source</th>
                            <th>Process</th>
                            <th>Created</th>
                        </tr>
                    </thead>
                    <tbody>
                        <xsl:for-each select="e:events/e:event">
                            <xsl:call-template name="event_row"/>
                            <!-- - NOTE: More straight way, but let's play with call-template here..
                            <xsl:apply-templates />
                            -->
                        </xsl:for-each>
                    </tbody>
                </table>
                <h3>Summary</h3>
                <ul>
                    <li>Info messages found:
                        <span style="color:green">
                            <xsl:value-of select="number(count(e:events/e:event[@severity='INFO']))"/>
                        </span>
                    </li>
                    <li>Error messages found:
                        <span style="color:red">
                            <xsl:value-of select="number(count(e:events/e:event[@severity='ERROR']))"/>
                        </span>
                    </li>
                </ul>
            </body>
        </html>
    </xsl:template>

    <xsl:template name="event_row">
        <tr>
            <xsl:call-template name="table_row_attrib_class"/>
            <td>
                <xsl:value-of select="@id"/>
            </td>
            <td>
                <xsl:value-of select="@severity"/>
            </td>
            <td>
                <xsl:value-of select="e:title"/>
            </td>
            <td>
                <xsl:value-of select="e:event-category/@code"/>
                <xsl:call-template name="show_message_in_brackets">
                    <xsl:with-param name="message" select="e:event-category/e:name"/>
                </xsl:call-template>
            </td>
            <td>
                <xsl:value-of select="e:source"/>
            </td>
            <td>
                <xsl:value-of select="e:process-id"/>
            </td>
            <td>
                <xsl:value-of select="@created"/>
            </td>
        </tr>
    </xsl:template>

    <xsl:template name="table_row_attrib_class">
        <xsl:attribute name="class">
            <xsl:if test="position() mod 2 = 1">odd_row</xsl:if>
            <xsl:if test="position() mod 2 = 0">even_row</xsl:if>
        </xsl:attribute>
    </xsl:template>

    <xsl:template name="show_message_in_brackets">
        <xsl:param name="message"/>
        <!--<xsl:if test="normalize-space($message) != ''">-->
        <xsl:if test="string-length(normalize-space($message)) > 0">
            (<xsl:value-of select="$message"/>)
        </xsl:if>
    </xsl:template>

</xsl:stylesheet>