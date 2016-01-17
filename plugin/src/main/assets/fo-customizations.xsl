<?xml version='1.0'?>
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
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:db="http://docbook.org/ns/docbook" xmlns:rpg="http://docbook.org/ns/docbook" xmlns:xlink='http://www.w3.org/1999/xlink' version="1.0">

	<xsl:import href="docbook-xsl-ns-1.78.1/fo/docbook.xsl" />
	<xsl:import href="docbook-xslt1-rwdalpe-extension_rpg/fo/all.xsl" />

	<xsl:param name="ulink.show" select="0" />
	<xsl:param name="glossterm.auto.link" select="1" />
	<xsl:param name="glossary.sort" select="1" />
	<xsl:param name="draft.watermark.image" select="'draft.png'" />
	<xsl:param name="body.start.indent" select="'0pt'" />
	<xsl:param name="hyphenate" select="'true'" />
	<xsl:param name="email.mailto.enabled" select="0" />
	<xsl:param name="email.delimiters.enabled" select="0" />
	<xsl:param name="use.svg" select="1" />
	<xsl:param name="fop1.extensions" select="1" />
	<xsl:param name="toc.indent.width" select="16" />
		<xsl:param name="generate.toc">
		book toc,title
	</xsl:param>
	<xsl:param name="local.l10n.xml" select="document('')" />
	<l:i18n xmlns:l="http://docbook.sourceforge.net/xmlns/l10n/1.0">
		<l:l10n language="en">
			<l:gentext key="reach" text="Reach" />
			<l:gentext key="Reach" text="Reach" />
			<l:gentext key="space" text="Space" />
			<l:gentext key="Space" text="Space" />
			<l:gentext key="rangedattacks" text="Ranged" />
			<l:gentext key="RangedAttacks" text="Ranged" />
			<l:gentext key="meleeattacks" text="Melee" />
			<l:gentext key="MeleeAttacks" text="Melee" />
			<l:gentext key="creaturespeeds" text="Speed" />
			<l:gentext key="CreatureSpeeds" text="Speed" />
			<l:gentext key="offenses" text="Offense" />
			<l:gentext key="Offenses" text="Offense" />
			<l:gentext key="weaknesses" text="Weaknesses" />
			<l:gentext key="Weaknesses" text="Weaknesses" />
			<l:gentext key="sr" text="SR" />
			<l:gentext key="SR" text="SR" />
			<l:gentext key="resistance" text="Resist" />
			<l:gentext key="Resistance" text="Resist" />
			<l:gentext key="immunity" text="Immune" />
			<l:gentext key="Immunity" text="Immune" />
			<l:gentext key="dr" text="DR" />
			<l:gentext key="DR" text="DR" />
			<l:gentext key="defensiveabilities" text="Defensive Abilities" />
			<l:gentext key="DefensiveAbilities" text="Defensive Abilities" />
			<l:gentext key="fasthealing" text="fast healing" />
			<l:gentext key="FastHealing" text="fast healing" />
			<l:gentext key="regeneration" text="regeneration" />
			<l:gentext key="Regeneration" text="regeneration" />
			<l:gentext key="hd" text="HD" />
			<l:gentext key="HD" text="HD" />
			<l:gentext key="hp" text="hp" />
			<l:gentext key="HP" text="hp" />
			<l:gentext key="flatfoot" text="flat-footed" />
			<l:gentext key="FlatFoot" text="flat-footed" />
			<l:gentext key="touch" text="touch" />
			<l:gentext key="Touch" text="touch" />
			<l:gentext key="ac" text="AC" />
			<l:gentext key="AC" text="AC" />
			<l:gentext key="defenses" text="Defense" />
			<l:gentext key="Defenses" text="Defense" />
			<l:gentext key="dc" text="DC" />
			<l:gentext key="DC" text="DC" />
			<l:gentext key="aura" text="Aura" />
			<l:gentext key="Aura" text="Aura" />
			<l:gentext key="senses" text="Senses" />
			<l:gentext key="Senses" text="Senses" />
			<l:gentext key="initiative" text="Init" />
			<l:gentext key="Initiative" text="Init" />
			<l:gentext key="xpreward" text="XP" />
			<l:gentext key="XPReward" text="XP" />
			<l:gentext key="challengerating" text="CR" />
			<l:gentext key="ChallengeRating" text="CR" />
			<l:gentext key="marketplace-major" text="Major Items" />
			<l:gentext key="marketplace-medium" text="Medium Items" />
			<l:gentext key="marketplace-minor" text="Minor Items" />
			<l:gentext key="marketplace-casting" text="Spellcasting" />
			<l:gentext key="marketplace-limit" text="Purchase Limit" />
			<l:gentext key="marketplace-base" text="Base Value" />
			<l:gentext key="marketplace" text="Marketplace" />
			<l:gentext key="Marketplace" text="Marketplace" />
			<l:gentext key="populations" text="Population" />
			<l:gentext key="Populations" text="Population" />
			<l:gentext key="demographics" text="Demographics" />
			<l:gentext key="Demographics" text="Demographics" />
			<l:gentext key="government" text="Government" />
			<l:gentext key="Government" text="Government" />
			<l:gentext key="settlementdanger" text="Danger" />
			<l:gentext key="SettlementDanger" text="Danger" />
			<l:gentext key="settlementdisadvantages" text="Disadvantages" />
			<l:gentext key="SettlementDisadvantages" text="Disadvantages" />
			<l:gentext key="settlementqualities" text="Qualtities" />
			<l:gentext key="SettlementQualities" text="Qualtities" />
			<l:context name="title">
				<l:template name="book" text="%t" />
			</l:context>
			<l:context name="xref">
				<l:template name="section" text="%t" />
			</l:context>
		</l:l10n>
	</l:i18n>

	<xsl:attribute-set name="xref.properties">
		<xsl:attribute name="color">blue</xsl:attribute>
		<xsl:attribute name="text-decoration">underline</xsl:attribute>
	</xsl:attribute-set>

	<xsl:template match="*" mode="simple.xlink.properties">
		<xsl:attribute name="color">blue</xsl:attribute>
		<xsl:attribute name="text-decoration">underline</xsl:attribute>
	</xsl:template>

	<xsl:template match="db:book/db:info/db:title[not(following-sibling::db:subtitle)]/text() | db:book/db:info/db:subtitle[preceding-sibling::db:title]/text()" mode="titlepage.mode">
		<xsl:copy-of select="." />
		<xsl:if test="$draft.mode = 'yes'">
			<xsl:text> [DRAFT]</xsl:text>
		</xsl:if>
	</xsl:template>

	<xsl:template match="db:mediaobject" />
</xsl:stylesheet>
