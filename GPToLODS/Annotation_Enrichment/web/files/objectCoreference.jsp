<%-- 
    Document   : main
    Created on : 28-May-2014, 15:32:04
    Author     : micha_000
--%>


<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN"
    "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>LODsyndesis</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="description" content="" />
        <meta name="Keywords" content="warehouse, linked data, semantic, sparql" />
		<%		boolean folders=false;
	
		String pag2="";
		String URI="",msg="";
		
				if (request.getAttribute("page2")!=null) {
					pag2=request.getAttribute("page2").toString();
				}
				if (request.getAttribute("URI")!=null) {
					URI="http://"+request.getAttribute("URI").toString().replace("$", "/");
				}
				else
					URI="http://dbpedia.org/resource/Aristotle";
				if (request.getAttribute("msg")!=null) {
					msg=request.getAttribute("msg").toString();
				}
				
				
%>



		<script type="text/javascript">
                    
                    
			function sendQuery() {
				var input = document.getElementById("query").value;
				var nameEncoded = encodeURIComponent(input);
				var vl = queryForm.queryType.value;
				var query;
				var value = queryForm.queryType.value;
				if (value==="DatDom")
					query="select distinct ?Dataset ?Domain where {<"+ input+">  dcterms:provenance ?Dataset .?Dataset dcterms:subject ?Domain}";
				else if (value==="EquivalentURIs")
					query="select distinct ?equivalentURI where { <"+ input+">  <http://purl.org/dc/terms/identifier> ?SID.?equivalentURI <http://purl.org/dc/terms/identifier> ?SID}";
				else if(value==="triples")
                                        query="select distinct ?ID where { <"+ input+"> <http://www.ics.forth.gr/isl/identifier> ?SID. ?SID ?p ?ID}";
                                    else if(value==="nt")
                                        query="select distinct ?ID where { <"+ input+"> <http://www.ics.forth.gr/isl/identifier> ?SID. ?SID ?p ?ID}";
                                else
					query="select distinct ?Dataset ?Domain where {<"+ input+"> dcterms:identifier ?SID . ?equivalentURI dcterms:identifier ?SID .?equivalentURI dcterms:provenance ?Dataset .?Dataset dcterms:subject ?Domain}";
                              //  query=query.replace(/\//g, '|');
                               // query=query.replace(/>/g,"biggerThan");
                               // query=query.replace(/</g,"lessThan");
                                var uri=input.replace("http://","");
                                        uri=uri.replace(/\//g, '$');
                                         uri=uri.replace("#", "@");
                               // uri=uri.replace("http://","");
                               
                                document.location.href = "RunQuery?URI="+ uri+"&queryType="+vl;    
			}
			
			function init(){
				
			<% String qt="";
				if (request.getAttribute("qt")!=null) {
					qt=request.getAttribute("qt").toString();
				} %>
				var value="<%=qt%>";
				if (value==="DatDom")
					document.getElementById("DatDom").checked=true;
				else if (value==="equivoruri")
					document.getElementById("equivoruri").checked=true;
			}
                        function myFunction(k) {
    var popup =document.getElementById("myPopup"+k);
    popup.classList.toggle("show");
}
		</script>
        <link rel="stylesheet" type="text/css" href="css/main.css" />
        <style>
/* Popup container - can be anything you want */
.popup {
    position: relative;
    display: inline-block;
    cursor: pointer;
    -webkit-user-select: none;
    -moz-user-select: none;
    -ms-user-select: none;
    user-select: none;
}

/* The actual popup */
.popup .popuptext {
    visibility: hidden;
    width: 500px;
    background-color: white;
    color: #fff;
    text-align: center;
    border-radius: 6px;
    padding: 8px 0;
    position: absolute;
    z-index: 1;
    bottom: 125%;
    left: 50%;
    margin-left: -80px;
}

/* Popup arrow */
.popup .popuptext::after {
    content: "";
    position: absolute;
    top: 100%;
    left: 50%;
    margin-left: -5px;
    border-width: 5px;
    border-style: solid;
    border-color: #555 transparent transparent transparent;
}

/* Toggle this class - hide and show the popup */
.popup .show {
    visibility: visible;
    -webkit-animation: fadeIn 1s;
    animation: fadeIn 1s;
}

/* Add animation (fade in the popup) */
@-webkit-keyframes fadeIn {
    from {opacity: 0;} 
    to {opacity: 1;}
}

@keyframes fadeIn {
    from {opacity: 0;}
    to {opacity:1 ;}
}
</style>
    </head>
    <body onload="init();">
	

        <div class="headerContainer">
            <div class="header">
               <div class="left1"> 
				<div class="logo">
                    <a title="LODsyndesis: Connectivity of LOD Datasets" href="./">
                        <span class="logotext"><b>LODsyndesis</b>: Connectivity of LOD Datasets</span>
                    </a>
					</div>
				<div class="menu">
						<font color='white'>&bull;</font>

                        <span class="menuitem">
                            <a href="index.jsp">Home</a>
                        </span>
						<font color='white'>&bull;</font>
						<span class="menuitem">
                            <a href="Config?type=objectCoreference">Global Entity Lookup And Facts Service</a>
                        </span>
                                                <font color='white'>&bull;</font>
						<span class="menuitem">
                            <a href="Config?type=factChecker">Fact Checking Service</a>
                        </span>
						<font color='white'>&bull;</font>
						<span class="menuitem">
                            <a href="Config?type=data_discovery">Data Discovery Service</a>
                        </span>
						  <font color='white'>&bull;</font>
                        <span class="menuitem">
                            <a href="#contact">Contact</a>
                        </span>
                        <font color='white'>&bull;</font>
                        <span class="menuitem">
                            <a href="#about">About</a>
                        </span>
                     </div>
                </div>
				</div>
                <div class="logo_right">
                    <img  src="files/logo.png" width="80"/>
                </div>
                <div class="menu"/>
            </div>
        </div>

        <div class="contentContainer">

            <div class="content">
                <div class="bgcontent">
					
					<div class="subContent" style="width:100%; margin-bottom:20px; margin-left: -5px">
                      <h2>Global Entity Lookup &amp; Facts Service (Type a URI)</h2>
			<input  size="100px" id="query"  value="<%=URI%>">
                            
                            	<script type="text/javascript">
                    
                    var input = document.getElementById("query");
input.addEventListener("keyup", function(event) {
    event.preventDefault();
    if (event.keyCode === 13) {
               document.getElementById("myBtn").click();
    }
});
                    </script>
			<form name="queryForm" >
				Find:<br>
			<input  type="radio" name="queryType" value="EquivalentURIs" id="EquivalentURIs" checked/>Equivalent URIs 	
			
			<br><input  type="radio" name="queryType" value="DatDom" id="DatDom" />Datasets and Domains containing this URI or Equivalent URI
			<br><input  type="radio" name="queryType" value="triples" id="triples" />Show all the Facts for this URI (or an equivalent one)<br>
                           <input  type="radio" name="queryType" value="nt" id="triples" />Export all the  for this URI (or an equivalent one) in N-Quads format
                            </form>
                       
			<br><button class="button" id="myBtn" onclick="sendQuery();">Search</button>
				</div>
			<div class="subContent">					
                       <%if (!pag2.equals("")){%>
					   <h3> <%=msg%></h3>
						<jsp:include page="<%=pag2%>" />
					<%}%>	
                    </div>
                   
							
                                     
                    <div class="subContent">
                        <div id="contact" class="subContentTitle">
                            Contact / Provide Feedback <a class="gotoptext" href="javascript:scrollTo(0,0)" title="go to the top of the page">top</a>
                        </div>
                        <div class="subContentText">
                            Please provide your feedback and any comments by sending an email at 
                            <a href="mailto:mountant@ics.forth.gr"><code>mountant@ics.forth.gr</code></a>.
                        </div>
                    </div>
                    
                    <div class="subContent">
                        <div id="about" class="subContentTitle">
                            About <a class="gotoptext" href="javascript:scrollTo(0,0)" title="go to the top of the page">top</a>
                        </div>
                        <div class="subContentText">
                            <p>
                                <span style="text-decoration: underline">Institution:</span><br />
                                <a href="http://ics.forth.gr/isl" title="Information System Laboratory (ISL)">Information System Laboratory (ISL)</a>,
                                <a href="http://ics.forth.gr" title="Institute of Computer Science (ICS)">Institute of Computer Science (ICS)</a>,
                                <br />
                                <a href="http://www.forth.gr" title="Foundation for Research and Technology - Hellas (FORTH)">Foundation for Research and Technology - Hellas (FORTH)</a>
                                <br />
                                <br />
                                
                                <span style="text-decoration: underline">Contact Persons:</span>
                                <br />
                                <code>
                                    Yannis Tzitzikas
                                    <br />
                                    Email: <a href="mailto:tzitzik@ics.forth.gr">tzitzik@ics.forth.gr</a>
                                    <br />
                                    Personal Web page: <a href="http://users.ics.forth.gr/~tzitzik/" title="Yannis Tzitzikas - Personal Home Page">http://users.ics.forth.gr/~tzitzik/</a>
                                    <br />
                                    Tel: <a href="tel:+302810391621">+30 2810 391 621</a>
                                    <br />
                                    Postal Address:
                                    <span style="font-family: Calibri">
                                    Institute of Computer Science (ICS),
                                    Foundation for Research and Technology - Hellas (FORTH),
                                    Science and Technology Park of Crete, Vassilika Vouton, 
                                    P.O.Box 1385, Heraklion, Crete, GR 7110, GREECE
                                    </span>
                                </code>
								<br />
								<br />
								<code>
                                    Michalis Mountantonakis
                                    <br />
                                    Email: <a href="mailto:tzitzik@ics.forth.gr">mountant@ics.forth.gr</a>
                                    <br />
                                    Personal Web page: <a href="http://users.ics.forth.gr/~mountant/" title="Michalis Mountantonakis - Personal Home Page">http://users.ics.forth.gr/~mountant/</a>
                                    <br />
                                    Postal Address:
                                    <span style="font-family: Calibri">
                                    Institute of Computer Science (ICS),
                                    Foundation for Research and Technology - Hellas (FORTH),
                                    Science and Technology Park of Crete, Vassilika Vouton, 
                                    P.O.Box 1385, Heraklion, Crete, GR 7110, GREECE
                                    </span>
                                </code>
                            </p>
                        </div>
                    </div>

                    <div class="subContent" style="width:100%; margin-bottom:80px; margin-left: -5px">
                        <div class="affiliationLogos">
                            <a href="http://www.ics.forth.gr/"  title="Institute of Computer Science - Foundation for Research and Technology - Hellas">
                                <img border="0" alt="Institute of Computer Science - Foundation for Research and Technology - Hellas" src="files/forth.jpg" height="150" />
                            </a>
                            <br />&nbsp;<br />&nbsp;
							<!--
							<br />&nbsp;<br />
                            <span style="font-family: calibri;font-size:16px;">This work was partially supported by:</span><br />
                            <a href="http://www.i-marine.eu/Pages/Home.aspx" target="_blank" title="iMarine: Data e-Infrastructure Initiative for Fisheries Management and Conservation of Marine Living Resources">
                                <img border="0" alt="iMarine: Data e-Infrastructure Initiative for Fisheries Management and Conservation of Marine Living Resources" src="files/images/iMarine_Logo.png" height="100"/>
                            </a>
							-->
                        </div>
                    </div>

                </div>

            </div>
        </div>
        
    </body>
</html>