<?xml version="1.0" encoding="UTF-8"?>
<!--
  Copyright (C) 2014 Robert Winslow Dalpe

  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU Affero General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
  GNU Affero General Public License for more details.

  You should have received a copy of the GNU Affero General Public License
  along with this program. If not, see <http://www.gnu.org/licenses/>.
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:db="http://docbook.org/ns/docbook" xmlns:t="http://nwalsh.com/docbook/xsl/template/1.0"
  xmlns:date="http://exslt.org/dates-and-times" version="1.0"
  xmlns:xlink='http://www.w3.org/1999/xlink' xmlns="http://www.w3.org/1999/XSL/Transform"
  exclude-result-prefixes="xsl db t date xlink">

  <xsl:output method="text" omit-xml-declaration="yes" />

  <xsl:template match="db:mediaobject[@xlink:href]">
    <xsl:value-of select="@xlink:href" />
<xsl:text>
</xsl:text>
    <xsl:apply-templates select="@*|node()" />
  </xsl:template>

  <xsl:template
    match="db:imagedata[not(../../@xlink:href) and @fileref]">
    <xsl:value-of select="@fileref" />
<xsl:text>
</xsl:text>
    <xsl:apply-templates select="@*|node()" />
  </xsl:template>

  <xsl:template match="@*|node()">
    <xsl:apply-templates select="@*|node()" />
  </xsl:template>

</xsl:stylesheet>