<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pom="http://maven.apache.org/POM/4.0.0">
<xsl:output encoding="UTF-8"/>

    <xsl:template match="node()|@*">
        <xsl:copy>
            <xsl:apply-templates select="node()|@*"/>
        </xsl:copy>
    </xsl:template>

    <!-- replace the version -->
    <xsl:template match="/pom:project/pom:version/text()">
        <xsl:value-of select="$version" />
    </xsl:template>

    <!-- replace the version -->
    <!--
    <xsl:template match="/pom:project/pom:dependencies/pom:dependency[artifactId="sevntu-checks"]/pom:version/text()">
        <xsl:value-of select="$version" />
    </xsl:template>
    -->
</xsl:stylesheet>
