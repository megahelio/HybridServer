<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:template match="/">
    <html>
      <head>
        <title>Hybrid Server Configuration</title>
      </head>
      <body>
        <h1>Hybrid Server Configuration</h1>
        <table border="1">
          <tr>
            <th>Element</th>
            <th>Value</th>
          </tr>
          <xsl:apply-templates select="configuration/*"/>
        </table>
      </body>
    </html>
  </xsl:template>
  
  <xsl:template match="connections">
    <tr>
      <th colspan="2">Connections</th>
    </tr>
    <xsl:apply-templates select="*"/>
  </xsl:template>
  
  <xsl:template match="database">
    <tr>
      <th colspan="2">Database</th>
    </tr>
    <xsl:apply-templates select="*"/>
  </xsl:template>
  
  <xsl:template match="servers">
    <tr>
      <th colspan="2">Servers</th>
    </tr>
    <xsl:apply-templates select="server"/>
  </xsl:template>
  
  <xsl:template match="server">
    <tr>
      <th colspan="2">Server</th>
    </tr>
    <xsl:apply-templates select="*"/>
  </xsl:template>
  
  <xsl:template match="*">
    <tr>
      <td><xsl:value-of select="name()"/></td>
      <td><xsl:value-of select="."/></td>
    </tr>
  </xsl:template>
</xsl:stylesheet>
