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
	xmlns:rpg="http://docbook.org/ns/docbook"
	xmlns="http://www.w3.org/1999/xhtml" exclude-result-prefixes="xsl db t date xlink m saxon exslt rpg">

	<xsl:import href="docbook-xsl-ns-1.78.1/epub3/chunk.xsl" />
	<xsl:import href="epub-titlepage.xsl" />
	<xsl:import href="docbook-xslt1-rwdalpe-extension_rpg/epub3/all.xsl" />

	<xsl:param name="chapter.autolabel" select="0" />
	<xsl:param name="chunk.section.depth" select="3" />
	<xsl:param name="chunk.first.sections" select="1" />
	<xsl:param name="draft.watermark.image" select="'draft.png'" />
	<xsl:param name="glossterm.auto.link" select="1" />
	<xsl:param name="glossary.sort" select="1" />
	<xsl:param name="generate.index" select="0" />
	<xsl:param name="NOW" select="date:date-time()" />
	<xsl:param name="book-name" select="''"/>
	<xsl:param name="local.l10n.xml" select="document('')"/> 
	<l:i18n xmlns:l="http://docbook.sourceforge.net/xmlns/l10n/1.0"> 
		<l:l10n language="en"> 
		<l:gentext key="reach" text="Reach"/>
			<l:gentext key="Reach" text="Reach"/>
			<l:gentext key="space" text="Space"/>
			<l:gentext key="Space" text="Space"/>
			<l:gentext key="rangedattacks" text="Ranged"/>
			<l:gentext key="RangedAttacks" text="Ranged"/>
			<l:gentext key="meleeattacks" text="Melee"/>
			<l:gentext key="MeleeAttacks" text="Melee"/>
			<l:gentext key="creaturespeeds" text="Speed"/>
			<l:gentext key="CreatureSpeeds" text="Speed"/>
			<l:gentext key="offenses" text="Offense"/>
			<l:gentext key="Offenses" text="Offense"/>
			<l:gentext key="weaknesses" text="Weaknesses"/>
			<l:gentext key="Weaknesses" text="Weaknesses"/>
			<l:gentext key="sr" text="SR"/>
			<l:gentext key="SR" text="SR"/>
			<l:gentext key="resistance" text="Resist"/>
			<l:gentext key="Resistance" text="Resist"/>
			<l:gentext key="immunity" text="Immune"/>
			<l:gentext key="Immunity" text="Immune"/>
			<l:gentext key="dr" text="DR"/>
			<l:gentext key="DR" text="DR"/>
			<l:gentext key="defensiveabilities" text="Defensive Abilities"/>
			<l:gentext key="DefensiveAbilities" text="Defensive Abilities"/>
			<l:gentext key="fasthealing" text="fast healing"/>
			<l:gentext key="FastHealing" text="fast healing"/>
			<l:gentext key="regeneration" text="regeneration"/>
			<l:gentext key="Regeneration" text="regeneration"/>
			<l:gentext key="hd" text="HD"/>
			<l:gentext key="HD" text="HD"/>
			<l:gentext key="hp" text="hp"/>
			<l:gentext key="HP" text="hp"/>
			<l:gentext key="flatfoot" text="flat-footed"/>
			<l:gentext key="FlatFoot" text="flat-footed"/>
			<l:gentext key="touch" text="touch"/>
			<l:gentext key="Touch" text="touch"/>
			<l:gentext key="ac" text="AC"/>
			<l:gentext key="AC" text="AC"/>
			<l:gentext key="defenses" text="Defense"/>
			<l:gentext key="Defenses" text="Defense"/>
			<l:gentext key="dc" text="DC"/>
			<l:gentext key="DC" text="DC"/>
			<l:gentext key="aura" text="Aura"/>
			<l:gentext key="Aura" text="Aura"/>
			<l:gentext key="senses" text="Senses"/>
			<l:gentext key="Senses" text="Senses"/>
			<l:gentext key="initiative" text="Init"/>
			<l:gentext key="Initiative" text="Init"/>
			<l:gentext key="xpreward" text="XP"/>
			<l:gentext key="XPReward" text="XP"/>
			<l:gentext key="challengerating" text="CR"/>
			<l:gentext key="ChallengeRating" text="CR"/>
			<l:gentext key="marketplace-major" text="Major Items"/>
			<l:gentext key="marketplace-medium" text="Medium Items"/>
			<l:gentext key="marketplace-minor" text="Minor Items"/>
			<l:gentext key="marketplace-casting" text="Spellcasting"/>
			<l:gentext key="marketplace-limit" text="Purchase Limit"/>
			<l:gentext key="marketplace-base" text="Base Value"/>
			<l:gentext key="marketplace" text="Marketplace"/>
			<l:gentext key="Marketplace" text="Marketplace"/>
			<l:gentext key="populations" text="Population"/>
			<l:gentext key="Populations" text="Population"/>
			<l:gentext key="demographics" text="Demographics"/>
			<l:gentext key="Demographics" text="Demographics"/>
			<l:gentext key="government" text="Government"/>
			<l:gentext key="Government" text="Government"/>
			<l:gentext key="settlementdanger" text="Danger"/>
			<l:gentext key="SettlementDanger" text="Danger"/>
			<l:gentext key="settlementdisadvantages" text="Disadvantages"/>
			<l:gentext key="SettlementDisadvantages" text="Disadvantages"/>
			<l:gentext key="settlementqualities" text="Qualtities"/>
			<l:gentext key="SettlementQualities" text="Qualtities"/>
			<l:context name="title"> 
				<l:template name="book" text="%t"/>
			</l:context>
			<l:context name="xref">
				<l:template name="section" text="%t"/>
			</l:context>
		</l:l10n>
	</l:i18n>

	<!-- BEGIN TEMPORARY WORKAROUND FOR ISSUE https://github.com/rwdalpe/two-graves/issues/6 -->
	<xsl:template match="db:informalfigure">
		<xsl:if test="./db:mediaobject">
			<xsl:apply-templates mode="m:mediafixer"/>
		</xsl:if>
	</xsl:template>

	<xsl:template match="db:informalfigure/db:mediaobject" mode="m:mediafixer">
		<xsl:variable name="id" select="../@xml:id"/>
		<xsl:variable name="role" select="../@role" />
		<xsl:variable name="thisobj">
			<xsl:copy>
				<xsl:apply-templates select="@*|node()" mode="m:identity" />
				<xsl:if test="$id != ''"><xsl:attribute name="xml:id" ><xsl:value-of select="$id"/></xsl:attribute></xsl:if>
				<xsl:if test="$role != ''"><xsl:attribute name="role" ><xsl:value-of select="$role"/></xsl:attribute></xsl:if>
				<xsl:if test="@xlink:href"><xsl:attribute name="xlink:href"><xsl:value-of select="saxon:tokenize(./@xlink:href, '/')[last()]"/></xsl:attribute></xsl:if>
			</xsl:copy>
		</xsl:variable>
		<xsl:message><xsl:copy-of select="$thisobj"/></xsl:message>
		<xsl:apply-templates select="exslt:node-set($thisobj)" />
	</xsl:template>

	<xsl:template match="db:imagedata" mode="m:identity">
		<xsl:copy>
			<xsl:apply-templates select="@*|node" mode="m:identity"/>
			<xsl:attribute name="fileref">
				<xsl:choose>
					<xsl:when test="../../@xlink:href">
						<xsl:value-of select="saxon:tokenize(../../@xlink:href, '/')[last()]"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="saxon:tokenize(@fileref, '/')[last()]"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
		</xsl:copy>
	</xsl:template>

	<xsl:template match="db:textobject" mode="m:identity"/>

	<xsl:template match="@*|node()" mode="m:identity">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" mode="m:identity" />
		</xsl:copy>
	</xsl:template>
	<!--  END TEMPORARY WORKAROUND FOR ISSUE https://github.com/rwdalpe/two-graves/issues/6 -->
	
	<xsl:template match="db:pubdate" mode="book.titlepage.recto.mode">
		<p>
			Last modified on
		</p>
		<xsl:apply-templates select="." mode="titlepage.mode" />
	</xsl:template>
	
	<xsl:template match="db:book/db:info/db:title[not(following-sibling::db:subtitle)]/text() | db:book/db:info/db:subtitle[preceding-sibling::db:title]/text()">
		<xsl:copy-of select="."/><xsl:if test="$draft.mode = 'yes'"><xsl:text> [DRAFT]</xsl:text></xsl:if>
	</xsl:template>  
	<xsl:template match="db:book/db:info/db:title[not(following-sibling::db:subtitle)]/text() | db:book/db:info/db:subtitle[preceding-sibling::db:title]/text()" mode="titlepage.mode">
		<xsl:copy-of select="."/><xsl:if test="$draft.mode = 'yes'"><xsl:text> [DRAFT]</xsl:text></xsl:if>
	</xsl:template>  
	<xsl:template match="db:book/db:info/db:title[following-sibling::db:subtitle]" mode="title.markup">
		<xsl:apply-templates mode="no.anchor.mode"/>: <xsl:apply-templates select="../db:subtitle" mode="subtitle.markup"/>
	</xsl:template>

	<xsl:template name="pi.dbtimestamp">
		<xsl:call-template name="datetime.format">
			<xsl:with-param name="date" select="$NOW" />
			<xsl:with-param name="format" select="'Y-m-d H:M:S'" />
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template match="db:index">
		<xsl:copy/>
	</xsl:template>

	<xsl:template match="db:index" mode="toc"/>  
	<xsl:template match="db:index" mode="ncx"/>
	<xsl:template match="db:index" mode="package.manifest"/>
</xsl:stylesheet>
