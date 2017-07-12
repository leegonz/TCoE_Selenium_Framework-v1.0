<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="xml" version="1.0" encoding="iso-8859-1" indent="yes"/>

<!--Test Scenario Metrics-->
	<xsl:variable name="scenarios_failed">
		<xsl:value-of select="count(//test-scenario[count(.//report[@status='Failed'])>0])" />
	</xsl:variable>
	<xsl:variable name="scenarios_warning">
		<xsl:value-of select="count(//test-scenario[count(.//report[@status='Failed'])=0 and count(.//report[@status='Warning'])>0])" />
	</xsl:variable>
	<xsl:variable name="scenarios_passed">
		<xsl:value-of select="count(//test-scenario[count(.//report[@status='Failed' or @status='Warning'])=0 and count(.//report[@status='Passed' or @status='Done'])>0 and (count(.//report[@status='Passed' or @status='Done'])=count(.//report)-count(.//report[@status='Info']))])" />
	</xsl:variable>
	<xsl:variable name="scenarios_incomplete">
		<xsl:value-of select="count(//test-scenario[count(.//report[@status='Failed' or @status='Warning'])=0 and count(.//report[@status='Passed' or @status='Done'])>0 and not(count(.//report[@status='Passed' or @status='Done'])=count(.//report)-count(.//report[@status='Info']))])" />
	</xsl:variable>
	<xsl:variable name="scenarios_norun">
		<xsl:value-of select="count(//test-scenario[count(.//report[@status='Passed' or @status='Done' or @status='Failed' or @status='Warning'])=0])" />
	</xsl:variable>
	<xsl:variable name="scenarios_total">
		<xsl:value-of select="count(//test-scenario)" />
	</xsl:variable>

<!--Test Case Metrics-->
	<xsl:variable name="cases_failed">
		<xsl:value-of select="count(//test-case[count(.//report[@status='Failed'])>0])" />
	</xsl:variable>

	<xsl:variable name="cases_warning">
		<xsl:value-of select="count(//test-case[count(.//report[@status='Failed'])=0 and count(.//report[@status='Warning'])>0])" />
	</xsl:variable>

	<xsl:variable name="cases_passed">
		<xsl:value-of select="count(//test-case[count(.//report[@status='Failed' or @status='Warning'])=0 and count(.//report[@status='Passed' or @status='Done'])>0 and (count(.//report[@status='Passed' or @status='Done'])=count(.//report)-count(.//report[@status='Info']))])" />
	</xsl:variable>

	<xsl:variable name="cases_incomplete">
		<xsl:value-of select="count(//test-case[count(.//report[@status='Failed' or @status='Warning'])=0 and count(.//report[@status='Passed' or @status='Done'])>0 and not(count(.//report[@status='Passed' or @status='Done'])=count(.//report)-count(.//report[@status='Info']))])" />
	</xsl:variable>

	<xsl:variable name="cases_norun">
		<xsl:value-of select="count(//test-case[count(.//report[@status='Passed' or @status='Done' or @status='Failed' or @status='Warning'])=0])" />
	</xsl:variable>

	<xsl:variable name="cases_total">
		<xsl:value-of select="count(//test-case)" />
	</xsl:variable>

<!--Test Step Metrics-->
	<xsl:variable name="steps_failed">
	<xsl:value-of select="count(//test-step[count(.//report[@status='Failed'])>0])" />
	</xsl:variable>

	<xsl:variable name="steps_warning">
	<xsl:value-of select="count(//test-step[count(.//report[@status='Failed'])=0 and count(.//report[@status='Warning'])>0])" />
	</xsl:variable>

	<xsl:variable name="steps_passed">
	<xsl:value-of select="count(//test-step[count(.//report[@status='Failed' or @status='Warning'])=0 and count(.//report[@status='Passed' or @status='Done'])>0 and (count(.//report[@status='Passed' or @status='Done'])=count(.//report)-count(.//report[@status='Info']))])" />
	</xsl:variable>

	<xsl:variable name="steps_incomplete">
	<xsl:value-of select="count(//test-step[count(.//report[@status='Failed' or @status='Warning'])=0 and count(.//report[@status='Passed' or @status='Done'])>0 and not(count(.//report[@status='Passed' or @status='Done'])=count(.//report)-count(.//report[@status='Info']))])" />
	</xsl:variable>

	<xsl:variable name="steps_norun">
	<xsl:value-of select="count(//test-step[count(.//report[@status='Passed' or @status='Done' or @status='Failed' or @status='Warning'])=0])" />
	</xsl:variable>

	<xsl:variable name="steps_total">
	<xsl:value-of select="count(//test-step)" />
	</xsl:variable>

<xsl:template match="root">
	<html>
	<head>
	<title>Premier/Phoenix Biller Data Creation report</title>
	<script src="http://code.jquery.com/jquery-1.11.0.min.js">//</script>
	<script>
	$( document ).ready(function() {

		function expandContent(element) {
			element.parent().nextAll(".box, .line").show();
			element.attr("class","collapse");
			element.unbind("click");
			element.text("[-]");
			element.click(function( event ) {
				collapseContent($(this));
			});
		};
		
		function collapseContent(element) {
			element.parent().nextAll(".box, .line").hide();
			element.attr("class","expand");
			element.unbind("click");
			element.text("[+]");
			element.click(function( event ) {
				expandContent($(this));
			});
		};
		
		$( "span.expand" ).click(function( event ) {
			expandContent($(this));
		});
		
		$( "span.collapse" ).click(function( event ) {
			collapseContent($(this));
		});
		
		$( "span.collapse-all" ).click(function( event ) {
			$( "span.collapse" ).click();
		});
		
		$( "span.expand-all" ).click(function( event ) {
			$( "span.expand" ).click();
		});
		
		$( "span.collapse" ).click();
		
		//$( "div.box[id='test-case']" ).find("span.expand:first").click();
		
	});
    </script>
	<style>
		h1 {
			font-size: 16pt;
			margin: 10px 0px 0px 0px;
		}
		div.box {
			font-family:"Calibri";
			font-size: 9pt;
			display: block;
			width: inherit;
			border: none;
			margin: 0px 5px 6px 5px;
			padding-bottom: 0px;
		}
		div.line {
			font-family:"Calibri";
			font-size: 9pt;
			display: block;
			width: inherit;
			border: solid #aaa 1px;
			margin: 4px 5px 4px 5px;
			padding: 0px 3px 0px 3px;
		}
		div.header1 {
			font-family:"Calibri";
			font-size: 10pt;
			color: white;
			background-color: #333;
			width: inherit;
			border: solid #aaa 1px;
			min-height: 19px;
			padding: 0px 3px 1px 3px;
			margin: 5px 0px 5px 0px;
		}
		div.header2 {
			font-family:"Calibri";
			font-size: 10pt;
			color: black;
			background-color: #cfcfcf;
			width: inherit;
			border: solid #bfbfbf 1px;
			min-height: 19px;
			padding: 0px 3px 1px 3px;
			margin: 5px 0px 5px 0px;
		}
		table.metrics {
			font-family:"Calibri";
			font-size: 9pt;
			border: solid gray 1px;
		}
		table.metrics td{
			text-align:center;
			border: solid #aaa 1px;
		}
		span.label {
			font-family:"Calibri";
			color: black;
			font-weight: bold;
		}
		span.passed {
			color: green;
		}
		span.failed {
			color: red;
		}
		span.warning {
			color: orange;
		}
		span.norun, span.incomplete {
			color: grey;
		}
		span.info {
			color: #3377ff;
		}
		span.done {
			color: green;
		}
		span.status {
			color: grey;
		}
		span.expand, span.collapse {
			font-family:"Courier";
			font-size: 10pt;
			color: grey;
			font-weight: bold;
		}
		a:link {
			color: #3377ff;
		}
		
	</style>
	</head>
	<body>
	<!-- <center><table class="metrics">
		<tr>
			<th width="105px">Level</th>
			<th width="75px"><span class="passed">Passed</span></th>
			<th width="75px"><span class="failed">Failed</span></th>
			<th width="75px"><span class="warning">Warning</span></th>
			<th width="75px"><span class="incomplete">Incomplete</span></th>
			<th width="75px"><span class="norun">No Run</span></th>
			<th width="75px">Total</th>
		</tr>
		<tr>
			<td>Test Scenario</td>
			<td><xsl:value-of select="$scenarios_passed" /></td>
			<td><xsl:value-of select="$scenarios_failed" /></td>
			<td><xsl:value-of select="$scenarios_warning" /></td>
			<td><xsl:value-of select="$scenarios_incomplete" /></td>
			<td><xsl:value-of select="$scenarios_norun" /></td>
			<td><xsl:value-of select="$scenarios_total" /></td>
		</tr>
		<tr>
			<td>Test Case</td>
			<td><xsl:value-of select="$cases_passed" /></td>
			<td><xsl:value-of select="$cases_failed" /></td>
			<td><xsl:value-of select="$cases_warning" /></td>
			<td><xsl:value-of select="$cases_incomplete" /></td>
			<td><xsl:value-of select="$cases_norun" /></td>
			<td><xsl:value-of select="$cases_total" /></td>
		</tr>
		<xsl:choose><xsl:when test=".//test-step">
		<tr>
			<td>Test Steps</td>
			<td><xsl:value-of select="$steps_passed" /></td>
			<td><xsl:value-of select="$steps_failed" /></td>
			<td><xsl:value-of select="$steps_warning" /></td>
			<td><xsl:value-of select="$steps_incomplete" /></td>
			<td><xsl:value-of select="$steps_norun" /></td>
			<td><xsl:value-of select="$steps_total" /></td>
		</tr>
		</xsl:when></xsl:choose>
	</table></center>
 -->
	<xsl:apply-templates select="test-scenario"/>
	
	</body>
	</html>
</xsl:template>

<!--Template for Test Scenarios--> 
<xsl:template match="test-scenario">	
	<div class="box" id="test-summary">
		<div class="header2"><span class="collapse" href="#">[-]</span>Summary<span class="status"> | <xsl:choose>
		  <xsl:when test=".//report[@status='Failed']">
			<span class="failed">Failed</span>
		  </xsl:when>
		  <xsl:when test=".//report[@status='Warning']">
			<span class="warning">Warning</span>
		  </xsl:when>
		  <xsl:when test=".//report[@status='Passed']">
			<span class="passed">Passed</span>
		  </xsl:when>
		  <xsl:otherwise>
			<span class="norun">No Run</span>
		  </xsl:otherwise>
		</xsl:choose></span>
		</div>
		<xsl:apply-templates select="summary"/>
		<xsl:apply-templates select="details"/>
	</div>
	<xsl:apply-templates select="test-case"/>
</xsl:template>

<!--Template for Summary/Details--> 
<xsl:template match="summary|details">
	<xsl:for-each select="info">
		<div class="line"><span class="label"><xsl:value-of select="@name" /></span> : <xsl:value-of select="@desc" /></div>
	</xsl:for-each>
</xsl:template>

<!--Template for Test Cases--> 
<xsl:template match="test-case">
	<div class="box" id="test-case">
		<div class="header1"><span class="collapse" href="#">[-]</span>Log: : <xsl:value-of select="@name"/><span class="status"> | <xsl:choose>
		  <xsl:when test=".//report[@status='Failed']">
			<span class="failed">Failed</span>
		  </xsl:when>
		  <xsl:when test=".//report[@status='Warning']">
			<span class="warning">Warning</span>
		  </xsl:when>
		  <xsl:when test=".//report[@status='Passed']">
			<span class="passed">Passed</span>
		  </xsl:when>
		  <xsl:otherwise>
			<span class="norun">No Run</span>
		  </xsl:otherwise>
		</xsl:choose></span>
		</div>
		<xsl:apply-templates select="summary"/>
		<xsl:apply-templates select="test-step"/>
		<xsl:apply-templates select="report|attachment"/>
	</div>
</xsl:template>

<!--Template for Test Steps--> 
<xsl:template match="test-step">
	<div class="box" id="test-step">
		<div class="header2"><span class="collapse" href="#">[-]</span> Step: <xsl:value-of select="@name"/><span class="status"> | <xsl:choose>
		  <xsl:when test=".//report[@status='Failed']">
			<span class="failed">Failed</span>
		  </xsl:when>
		  <xsl:when test=".//report[@status='Warning']">
			<span class="warning">Warning</span>
		  </xsl:when>
		  <xsl:when test=".//report[@status='Passed']">
			<span class="passed">Passed</span>
		  </xsl:when>
		  <xsl:otherwise>
			<span class="norun">No Run</span>
		  </xsl:otherwise>
		</xsl:choose></span>
		</div>
		<xsl:apply-templates select="report|attachment"/>
	</div>
</xsl:template>

<!--Template for Test Reports--> 
<xsl:template match="report">
	<div class="line">
		<span width="100px"><xsl:value-of select="@timestamp"/></span> | <b><span>
		<xsl:choose>
		  <xsl:when test="@status='Passed'">
			<xsl:attribute name="class">passed</xsl:attribute>
		  </xsl:when>
		  <xsl:when test="@status='Failed'">
			<xsl:attribute name="class">failed</xsl:attribute>
		  </xsl:when>
		  <xsl:when test="@status='Warning'">
			<xsl:attribute name="class">warning</xsl:attribute>
		  </xsl:when>
		  <xsl:when test="@status='Done'">
			<xsl:attribute name="class">done</xsl:attribute>
		  </xsl:when>
		  <xsl:when test="@status='Info'">
			<xsl:attribute name="class">info</xsl:attribute>
		  </xsl:when>
		</xsl:choose>
		<xsl:value-of select="@status"/>
		</span></b> | <xsl:value-of select="@desc"/>
	</div>
</xsl:template>

<!--Template for Test Attachements--> 
<xsl:template match="attachment">
	<div class="line"><span class="label">Attachment</span> : <a>
	  <xsl:attribute name="href">
		<xsl:value-of select="@file"/>
	  </xsl:attribute>
	  <xsl:value-of select="@name"/>
	</a></div>
</xsl:template>

</xsl:stylesheet>