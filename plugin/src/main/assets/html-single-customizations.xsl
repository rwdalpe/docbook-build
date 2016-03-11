<?xml version='1.0'?>
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
	xmlns:t="http://docbook.org/xslt/ns/template" version="2.0"
	xmlns:m="http://docbook.org/xslt/ns/mode" xmlns="http://www.w3.org/1999/xhtml"
	xmlns:db="http://docbook.org/ns/docbook" xmlns:tmpl="http://docbook.org/xslt/titlepage-templates"
	xmlns:f="http://docbook.org/xslt/ns/extension" xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:xlink='http://www.w3.org/1999/xlink'
	xmlns:mp="http://docbook.org/xslt/ns/mode/private"
	xmlns:h="http://www.w3.org/1999/xhtml"
	xmlns:usrfn="http://docbook.org/xslt/ns/user-extension"

	exclude-result-prefixes="t xsl m db tmpl f xs usrfn mp xlink h">

	<xsl:import href="docbook-xslt2-2.0.22-rwdalpe/xslt/base/html/final-pass.xsl" />
	<xsl:import href="docbook-xslt2-rwdalpe-extension_rpg/html/all.xsl" />

	<xsl:param name="use.extensions" select="'1'" />
	<xsl:param name="html.scripts" select="''" />
	<xsl:param name="html.stylesheets" select="'slicknav.css'" />
	<xsl:param name="draft.watermark.image" select="'draft.png'" />
	<xsl:param name="generate.meta.generator" select="'0'" />
	<xsl:param name="docbook.css" select="''" />
	<xsl:param name="section.autolabel.max.depth" select="0" />
	<xsl:param name="generate.index" select="0"/>
	<xsl:param name="glossterm.auto.link" select="1" />
	<xsl:param name="glossary.sort" select="1" />
	<xsl:param name="syntax-highlighter" select="'0'" />
	<xsl:param name="scripts.builtin.ignore" select="'1'" />
	<xsl:param name="styles.builtin.ignore" select="'1'" />
	<xsl:param name="autolabel.elements">
		<db:appendix format="A" />
		<db:figure />
		<db:example />
		<db:table />
		<db:equation />
		<db:part format="I" />
		<db:reference format="I" />
		<db:preface />
		<db:qandadiv />
		<db:refsection />
	</xsl:param>
	<xsl:param name="NOW" select="current-dateTime()" />
	<xsl:param name="book-name" select="''"/>
	<xsl:param name="generate.toc" as="element()*">
		<tocparam path="book" toc="1" title="1"/>
	</xsl:param>

	<xsl:template match="*" mode="m:head-content">
		<meta xmlns="http://www.w3.org/1999/xhtml" name="viewport" content="initial-scale=1" />
	</xsl:template>

	<xsl:template match="*" mode="m:javascript-body">
		<script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js" type="text/javascript"></script>
		<script src="jquery.slicknav.js" type="text/javascript"></script>
		<script src="menu.js" type="text/javascript"></script>
		<script src="rotate-images.js" type="text/javascript"></script>
		<script src="readability.js" type="text/javascript"></script>
	</xsl:template>

	<xsl:template name="pi.dbtimestamp">
		<xsl:if test="$NOW "></xsl:if>
		<xsl:value-of
			select="format-dateTime(xs:dateTime($NOW), '[Y0001]-[M01]-[D01] [H01]:[m01] [ZN]')" />
	</xsl:template>

	<xsl:template match="processing-instruction('dbtimestamp')">
		<xsl:call-template name="pi.dbtimestamp" />
	</xsl:template>

	<xsl:template match="processing-instruction()" mode="m:titlepage-mode">
		<xsl:apply-templates select="." />
	</xsl:template>

	<xsl:template match="db:pubdate" mode="m:titlepage-recto-mode">
		<div class="pubdate-container">
			<p>
				Last modified on
				<span class="pubdate">
					<xsl:apply-templates select="." mode="m:titlepage-mode" />
				</span>
			</p>
		</div>
	</xsl:template>

	<!-- BEGIN TEMPORARY WORKAROUND FOR ISSUE https://github.com/rwdalpe/two-graves/issues/6 -->
	<xsl:template match="db:informalfigure">
		<xsl:if test="./db:mediaobject">
			<xsl:apply-templates mode="m:mediafixer"/>
		</xsl:if>
	</xsl:template>

	<xsl:template match="db:informalfigure/db:mediaobject" mode="m:mediafixer">
		<xsl:variable name="id" select="../@xml:id"/>
		<xsl:variable name="role" select="../@role" />
		<xsl:variable name="thisobj" as="element()+">
			<xsl:copy>
				<xsl:attribute name="xml:id" select="$id" />
				<xsl:attribute name="role" select="$role" />
				<xsl:apply-templates select="@*|node()" mode="m:identity" />
			</xsl:copy>
		</xsl:variable>
		<xsl:apply-templates select="$thisobj" />
	</xsl:template>

	<xsl:template match="db:textobject" mode="m:identity"/>

	<xsl:template match="@*|node()" mode="m:identity">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" mode="m:identity" />
		</xsl:copy>
	</xsl:template>
	<!--  END TEMPORARY WORKAROUND FOR ISSUE https://github.com/rwdalpe/two-graves/issues/6 -->

	<xsl:template match="db:mediaobject[@role='rotated-image' and @xlink:href]">
		<a href="{@xlink:href}">
			<xsl:apply-imports/>
		</a>
	</xsl:template>

	<xsl:template name="t:html-title-content">
		<xsl:param name="node" select="."/>
		<title>
			<xsl:value-of select="f:title($node)"/>: <xsl:apply-templates select="$node" mode="m:subtitle-content"/>
		</title>
	</xsl:template>

	<xsl:template match="db:printhistory" mode="m:titlepage-mode">
		<div>
			<xsl:sequence select="f:html-attributes(.)" />
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="db:index">
		<xsl:copy/>
	</xsl:template>
	
	<xsl:template match="db:index" mode="mp:toc"/>
	
	<xsl:function name="usrfn:is-acceptable-mediaobject" as="xs:integer">
		<xsl:param name="object" as="element()*" />

		<xsl:choose>
			<xsl:when test="$object/@role = 'hires'">0</xsl:when>
			<xsl:otherwise>1</xsl:otherwise>
		</xsl:choose>
	</xsl:function>

	<xsl:template match="db:cover[@role]" mode="m:titlepage-mode">
		<div class="{@role}">
			<xsl:apply-templates select="node()" />
		</div>
	</xsl:template>

	<xsl:template match="db:cover[@role]" mode="m:titlepage-template">
		<xsl:param name="context" as="element()" required="yes" />
		<xsl:param name="content" as="element()*" required="yes" />
		<xsl:param name="mode" as="xs:string" required="yes" />

		<xsl:apply-templates
			select="$content[node-name(.) = node-name(current()) and ./@role = current()/@role]"
			mode="m:titlepage-content">
			<xsl:with-param name="context" select="$context" />
			<xsl:with-param name="template" select="." />
			<xsl:with-param name="mode" select="$mode" />
		</xsl:apply-templates>
	</xsl:template>
	
	<xsl:template match="db:title-group" mode="m:titlepage-template">
		<xsl:param name="context" as="element()" required="yes"/>
		<xsl:param name="content" as="element()*" required="yes"/>
		<xsl:param name="mode"    as="xs:string" required="yes"/>
		<xsl:apply-templates select="." mode="m:titlepage-mode">
			<xsl:with-param name="context" select="$context"/>
		</xsl:apply-templates>
	</xsl:template>
	
	<xsl:template match="db:title-group" mode="m:titlepage-mode">
		<xsl:param name="context" as="element()?" select="()"/>
		<xsl:choose>
			<xsl:when test="$context[db:info/db:title and db:info/db:subtitle]">
				<h1>
					<span class="title">
						<xsl:apply-templates select="$context" mode="m:object-title-markup">
							<xsl:with-param name="allow-anchors" select="true()"/>
						</xsl:apply-templates>
					</span>
					<xsl:text> </xsl:text>
					<span class="subtitle">
						<xsl:apply-templates select="$context" mode="m:object-subtitle-markup"/>
					</span>
				</h1>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates select="$context/db:title | $context/db:info/db:title | $context/db:info/db:subtitle" mode="m:titlepage-mode"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template match="db:chapter|db:appendix" mode="m:insert-title-markup">
		<xsl:param name="purpose"/>
		<xsl:param name="xrefstyle"/>
		<xsl:param name="title"/>
	
		<xsl:copy-of select="$title"/>
	</xsl:template>
	
	<xsl:template match="db:book/db:info/db:title[not(following-sibling::db:subtitle)]/text() | db:book/db:info/db:subtitle[preceding-sibling::db:title]/text()">
		<xsl:copy-of select="."/><xsl:if test="$draft.mode = 'yes'"><xsl:text> [DRAFT]</xsl:text></xsl:if>
	</xsl:template>
	
	<xsl:template match="db:set|db:book|db:part|db:reference">
		<article>
			<xsl:sequence select="f:html-attributes(.,f:node-id(.))" />
			<xsl:call-template name="t:titlepage" />
	
			<xsl:if test="not(db:toc)">
				<!-- only generate a toc automatically if there's no explicit toc -->
				<xsl:apply-templates select="." mode="m:toc" />
			</xsl:if>
	
			<xsl:apply-templates />
			<xsl:apply-templates select="..//db:cover[@role = 'back-cover']" mode="m:titlepage-mode" />
		</article>
	</xsl:template>
	
	<xsl:template match="db:note|db:important|db:warning|db:caution|db:tip">
		<xsl:choose>
			<xsl:when test="$admonition.graphics">
				<xsl:apply-templates select="." mode="m:graphical-admonition"/>
			</xsl:when>
			<xsl:otherwise>
				<div>
					<xsl:sequence select="f:html-attributes(., @xml:id, local-name(.), (./@role,'admonition'))"/>
					<xsl:call-template name="t:titlepage"/>
		<xsl:apply-templates/>
				</div>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template match="db:book" mode="m:get-titlepage-templates" as="element(tmpl:templates)">
		<tmpl:templates>
			<tmpl:recto>
				<db:cover role="front-cover"/>
				<header tmpl:class="titlepage">
					<db:title-group />
					<xsl:if test="/db:book/db:info/db:volumenum">
						<div class="volume-info">Book <db:volumenum /> of <db:seriesvolnums /></div> 
					</xsl:if>
					<db:corpauthor />
					<db:authorgroup />
					<db:author />
					<db:editor />
					<db:othercredit />
					<div class="bottom-matter">
						<db:releaseinfo />
						<db:copyright />
						<db:legalnotice />
						<db:pubdate />
						<db:printhistory />
						<db:revision />
						<db:revhistory />
					</div>
					<db:abstract />
				</header>
				<hr tmpl:keep="true" />
			</tmpl:recto>
		</tmpl:templates>
	</xsl:template>
	
	<xsl:template match="db:warning" mode="m:get-titlepage-templates" as="element(tmpl:templates)">
		<tmpl:templates>
			<tmpl:titlepage>
				<header>
					<db:title/>
				</header>
			</tmpl:titlepage>
		</tmpl:templates>
	</xsl:template>
	
	<xsl:template match="db:tip" mode="m:get-titlepage-templates" as="element(tmpl:templates)">
		<tmpl:templates>
			<tmpl:titlepage>
				<header>
					<db:title/>
				</header>
			</tmpl:titlepage>
		</tmpl:templates>
	</xsl:template>
	
	<xsl:template name="t:user-localization-data">
		<l:l10n xmlns:l="http://docbook.sourceforge.net/xmlns/l10n/1.0" language="en" english-language-name="English">
			<l:gentext key="creatureskills" text="Skills"/>
			<l:gentext key="CreatureSkills" text="Skills"/>
			<l:gentext key="creaturefeats" text="Feats"/>
			<l:gentext key="CreatureFeats" text="Feats"/>
			<l:gentext key="cmd" text="CMD"/>
			<l:gentext key="CMD" text="CMD"/>
			<l:gentext key="cmb" text="CMB"/>
			<l:gentext key="CMB" text="CMB"/>
			<l:gentext key="bab" text="Base Atk"/>
			<l:gentext key="BAB" text="Base Atk"/>
			<l:gentext key="statistics" text="Statistics"/>
			<l:gentext key="Statistics" text="Statistics"/>
			<l:gentext key="spellsprepped" text="Spells Prepared"/>
			<l:gentext key="SpellsPrepped" text="Spells Prepared"/>
			<l:gentext key="concentration" text="concentration"/>
			<l:gentext key="Concentration" text="concentration"/>
			<l:gentext key="casterlevel" text="CL"/>
			<l:gentext key="CasterLevel" text="CL"/>
			<l:gentext key="slas" text="Spell-Like Abilities"/>
			<l:gentext key="Slas" text="Spell-Like Abilities"/>
			<l:gentext key="specialattacks" text="Special Attacks"/>
			<l:gentext key="SpecialAttacks" text="Special Attacks"/>
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
			<l:context name="xref">
				<l:template name="section" text="%t"/>
			</l:context>
		</l:l10n>
	</xsl:template>
	
	
</xsl:stylesheet>


