<?xml version="1.0" encoding="UTF-8"?>
<!--
Copyright (C) 2014 Robert Winslow Dalpe

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:db="http://docbook.org/ns/docbook" xmlns:t="http://nwalsh.com/docbook/xsl/template/1.0"
  xmlns:date="http://exslt.org/dates-and-times" version="1.0"
  xmlns:xlink='http://www.w3.org/1999/xlink'
  xmlns:m="http://docbook.org/xslt/ns/mode"
  xmlns:saxon="http://icl.com/saxon"
  xmlns:exslt="http://exslt.org/common"
  xmlns="http://www.w3.org/1999/xhtml" exclude-result-prefixes="xsl db t date xlink m saxon exslt">

  <xsl:import href="docbook-xsl-ns-1.78.1/html/docbook.xsl" />
  
  <xsl:param name="local.l10n.xml" select="document('')"/> 
  <l:i18n xmlns:l="http://docbook.sourceforge.net/xmlns/l10n/1.0"> 
    <l:l10n language="en"> 
      <l:context name="xref"> 
        <l:template name="book" text="%t: %s"/>
        <l:template name="section" text="%t"/>
      </l:context>    
      <l:context name="xref-number-and-title"> 
        <l:template name="book" text="%t: %s"/> 
        <l:template name="section" text="%t"/>
      </l:context>    
    </l:l10n>
  </l:i18n>
</xsl:stylesheet>
