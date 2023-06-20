<%-- 
    Document   : main
    Created on : 28-May-2014, 15:32:04
    Author     : micha_000
--%>


<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN"
    "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html>
    <head>
        <title>GPT&#x2022;LODS</title>
        <meta charset="utf-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1" />
        <link rel="stylesheet" href="assets/css/main.css" />
        <link rel="stylesheet" href="https://www.w3schools.com/lib/w3.css">
        <link rel="stylesheet" href="https://www.w3schools.com/lib/w3-theme-black.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
        <meta name="Keywords" content="warehouse, linked data, semantic, sparql" />
        <%		boolean folders = false;

            String pag2 = "", question = "";
            String URI = "", msg = "", cl = "", text = "", intro = "";
            String responseOnlygpt="";
            if (request.getAttribute("page2") != null) {
                pag2 = request.getAttribute("page2").toString();
            }
            if (request.getAttribute("question") != null) {
                question = request.getAttribute("question").toString();
            }
            if (request.getAttribute("responseOnlyGPT") != null) {
                responseOnlygpt = request.getAttribute("responseOnlyGPT").toString();
            }
            if (request.getAttribute("URI") != null) {
                URI = "http://" + request.getAttribute("URI").toString().replace("$", "/");
            } else {
                URI = "A Set of Entities";
            }
            if (request.getAttribute("cl") != null) {
                cl = "http://" + request.getAttribute("cl").toString().replace("$", "/");
            } else {
                cl = "http://dbpedia.org/class/yago/WikicatAncientGreekPhilosophers";
            }
            if (request.getAttribute("msg") != null) {
                msg = request.getAttribute("msg").toString();
            }
            if (request.getAttribute("intro") != null) {
                intro = request.getAttribute("intro").toString();
            }


        %>



        <script type="text/javascript">

            function myFunction(k) {
                var popup = document.getElementById("myPopup" + k);

                popup.classList.toggle("show");
            }

            function showData(value) {
                if (value.length >= 4 && !value.startsWith("http://")) {
                    var options = '';
                    $.ajax({
                        url: "Config?type=autoComplete&word=" + value,
                        type: "POST",
                        async: false,
                        success: function (data) {
                            var k = data.split("\n");

                            for (i = 0; i < k.length - 1; i++) {
                                var x = k[i].split("\t");
                                options += '<option value="' + x[1] + '" >' + x[0] + '</option>';
                            }
                        }
                    });
                    document.getElementById('huge_list').innerHTML = options;
                }
            }


            function sendQuery() {
                var input = document.getElementById("textS").value;
                //var tool = queryForm.toolType.value;
                var tool = "";
                if (document.getElementById("tool1").checked === true && document.getElementById("tool2").checked === true && document.getElementById("tool3").checked === true) {
                    tool = "ALL";
                } else if (document.getElementById("tool1").checked === true && document.getElementById("tool2").checked === true && document.getElementById("tool3").checked === false) {
                    tool = "DBSCNLP";
                } else if (document.getElementById("tool1").checked === true && document.getElementById("tool2").checked === false && document.getElementById("tool3").checked === true) {
                    tool = "DBWAT";
                } else if (document.getElementById("tool1").checked === false && document.getElementById("tool2").checked === true && document.getElementById("tool3").checked === true) {
                    tool = "SCNLPWAT";
                } else if (document.getElementById("tool1").checked === true && document.getElementById("tool2").checked === false && document.getElementById("tool3").checked === false) {
                    tool = "DBpedia Spotlight";
                } else if (document.getElementById("tool1").checked === false && document.getElementById("tool2").checked === true && document.getElementById("tool3").checked === false) {
                    tool = "Stanford CoreNLP";
                } else if (document.getElementById("tool1").checked === false && document.getElementById("tool2").checked === false && document.getElementById("tool3").checked === true) {
                    tool = "WAT";
                }
                var checkGPT = document.getElementById("gpt1").checked;
                var gptmodel = "";
                if (checkGPT === true)
                    gptmodel = document.getElementById("gpt1").value;
                else {
                    gptmodel = document.getElementById("gpt2").value;
                }
                if (tool !== "")
                    document.location.href = "InformationExtraction?text=" + input + "&toolType=" + tool + "&gptModel=" + gptmodel;// + "&mt="+v2+"&qt=" + value + "&lt=" + limit;
                else
                    alert("Select at least one Entity Recognition tool");
                //document.location.href = "RunQuery?URI="+ uri+"&queryType="+vl;    
            }

            function sendQuery2() {
                document.location.href = "Annot_Enrichment";
            }



            function rdfa() {
                var value = "<%=text%>";
                document.location.href = "InformationExtraction?RDFa=true";
            }

            function compareFacts() {
                document.location.href = "ResponseToFacts";
            }

            function rdfFacts() {

                document.location.href = "InformationExtraction?RDFFacts=true";
            }

            function relFacts() {
                // var value="<%=text%>";

                document.location.href = "InformationExtraction?relFacts=true";
            }

             function relFactsNew() {
                var value="<%=responseOnlygpt%>";
                window.open("http://83.212.101.188:8081/FactChecking_ChatGPT/?text="+value, "_blank");
                //document.location.href = "InformationExtraction?relFacts=true";
            }

            function relFactsURI() {
                var e = document.getElementById("URIs");
                var ent = e.options[e.selectedIndex].value;
                document.location.href = "InformationExtraction?relFacts=true&entity=" + ent;
            }

            function init() {
                var page2 = document.getElementById("message");




            <%  String qt = "", lt = "", uri = "", type = "", mt = "", uris = "", urisType = "", outputRDF = "", keyEntity = "", keyURI = "";
                String entities = "";
                if (request.getAttribute("urisType") != null) {
                    urisType = request.getAttribute("urisType").toString();
                }

                if (request.getAttribute("URIs") != null) {
                    uris = request.getAttribute("URIs").toString();
                }

                if (request.getAttribute("text") != null) {
                    text = request.getAttribute("text").toString();
                }
                if (request.getAttribute("outputRDFfactsHTML") != null) {
                    outputRDF = request.getAttribute("outputRDFfactsHTML").toString();
                }
                if (request.getAttribute("keyEntity") != null) {
                    keyEntity = request.getAttribute("keyEntity").toString();
                }
                if (request.getAttribute("keyURI") != null) {
                    keyURI = request.getAttribute("keyURI").toString();
                }
                if (request.getAttribute("entities") != null) {
                    entities = request.getAttribute("entities").toString();
                }
            %>


                var value = "<%=qt%>";
                var lmt = "<%=lt%>";
                var intro = "<%=intro%>";
                var outputRDF = "<%=outputRDF%>";
                var entities = "<%=entities%>";
                // if(intro!==""){
                window.location.hash = '#main';
                // }
                if (outputRDF !== "") {
                    var select = document.getElementById('URIs');
                    var spl = entities.split("\t");
                    var i;
                    for (i = 0; i < spl.length - 1; i++) {
                        var opt = document.createElement('option');
                        var spl2 = spl[i].split("$");
                        opt.value = spl2[1];
                        opt.innerHTML = spl2[0];
                        select.appendChild(opt);
                    }
                }
                var uris = "<%=uris.replace("\n", "<br>")%>";
                var type = "<%=type%>";
                var keyentity = "<%=keyEntity%>";
                if (page2.style.display === "none") {
                    page2.style.display = "block";
                } else if (keyentity === "") {
                    page2.style.display = "none";
                }


                var urisType = "<%=urisType%>";


            }

            function show_hide() {
                var page2 = document.getElementById("message");
                if (page2.style.display === "none") {
                    page2.style.display = "block";
                } else {
                    page2.style.display = "none";
                }

            }

            function checkMoreFacts() {
                var keyURI = "<%=keyURI%>";
                document.location.href = "https://demos.isl.ics.forth.gr/lodsyndesis/RunQueryFC?URI=dbpedia.org$resource$" + keyURI + "&queryType=fc&words=type&export=false#message";
            }


            function setText(k) {
                if (k === 1)
                    document.getElementById("textS").innerHTML = "The Godfather is an American crime film directed by Francis Ford Coppola and produced by Albert S. Ruddy, based on Mario Puzo's best-selling novel of the same name. The film features an ensemble cast including Marlon Brando, Al Pacino, James Caan, Richard Castellano, Robert Duvall, Sterling Hayden, John Marley, Richard Conte, and Diane Keaton. The story, spanning from 1945 to 1955, chronicles the Corleone crime family under patriarch Vito Corleone (Brando), focusing on the transformation of one of his sons, Michael Corleone (Pacino), from reluctant family outsider to ruthless mafia boss.";
                else if (k === 2)
                    document.getElementById("textS").innerHTML = "The 1896 Summer Olympics, officially known as the Games of The I Olympiad, was the first international Olympic Games held in modern history. Organised by The International Olympic Committee, which had been created by Pierre De Coubertin, it was held in Athens, Greece, from 6 to 15 April 1896. The inaugural Games of the modern Olympics were attended by as many as 280 athletes, all male, from 12 countries. The athletes in the 1896 Summer Olympics competed in 43 events covering Track and Field, Cycling, Swimming, Gymnastics, Weightlifting, Fencing and Tennis.";
                else if (k === 3)
                    document.getElementById("textS").innerHTML = "The Scorpions are a German rock band formed in 1965 in Hanover by Rudolf Schenker. Since the band's inception, its musical style has ranged from hard rock to heavy metal. The lineup from 1978 to 1992 was the most successful incarnation of the group, and included Klaus Meine (vocals), Rudolf Schenker (rhythm guitar), Matthias Jabs (lead guitar), Francis Buchholz (bass), and Herman Rarebell (drums). Throughout the 1980s the group received positive reviews and critical acclaim from music critics, and experienced commercial success with the albums Animal Magnetism (1980), Blackout (1982), Love at First Sting (1984), the live recording World Wide Live (1985), Savage Amusement (1988) and Best of Rockers 'n' Ballads (1989), which is their best-selling compilation album.";
                else if (k === 4)
                    document.getElementById("textS").innerHTML = "Nikos Kazantzakis was born in Heraklion, Crete. Widely considered a giant of modern Greek literature, he was nominated for the Nobel Prize in Literature in nine different years. Kazantzakis' novels included Zorba the Greek (published 1946 as Life and Times of Alexis Zorbas), Christ Recrucified (1948), Captain Michalis (1950, translated Freedom and Death), and The Last Temptation of Christ (1955). His fame spread in the English-speaking world due to cinematic adaptations of Zorba the Greek (1964) and The Last Temptation of Christ (1988). He translated also a number of notable works into Modern Greek, such as the Divine Comedy, Thus Spoke Zarathustra and the Iliad.  Late in 1957, even though suffering from leukemia, he set out on one last trip to China and Japan. Falling ill on his return flight, he was transferred to Freiburg, Germany, where he died.";
                else if (k === 5)
                    document.getElementById("textS").innerHTML = "The Lord of the Rings Film Series is a  series of three epic fantasy adventure films directed by Peter Jackson, based on the novel written by J. R. R. Tolkien. The films are subtitled The Fellowship of the Ring (2001), The Two Towers (2002) and The Return Of The King (2003). Produced and distributed by New Line Cinema with the co-production of WingNut Films, it is an international venture between New Zealand and the United States. The films feature an ensemble cast including Elijah Wood, Ian McKellen, Liv Tyler, Viggo Mortensen, Sean Astin, Cate Blanchett, John Rhys-Davies, Christopher Lee, Billy Boyd, Orlando Bloom, Hugo Weaving, Andy Serkis and Sean Bean.";

            }


        </script>
        <style>


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
                width: 220px;
                background-color: #fff;
                color: #000;
                text-align: center;
                font-size:20px;
                border-style: solid;
                border-radius: 8px;
                border-width: 2px;
                border-color: #000;
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
    <body class="subpage" onload="init();">

        <!-- Header -->
        <header id="header">
            <div class="logo"><a href="index.jsp">GPT&#x2022;LODS<span> SAR group ISL-FORTH</span></a></div>
            <a href="#menu">Menu</a>
        </header>


        <nav id="menu">
            <ul class="links">
                <li><a href="index.jsp">Home Page</a></li>
                <li><a href="Annot_Enrichment">Annotation and Enrichment</a></li> <!--annotation_enrichment_service-->
                <li><a href="https://youtu.be/H30bSv9NfUw">Tutorial Video - Annotation and Enrichment</a></li>
                <li><a href="FactChecking">Fact Checking and Triples Generation</a></li>
                <li><a href="https://youtu.be/5DW1d37aPMc">Tutorial Video - Fact Checking and Triples Generation</a></li>
                <!--    <li><a href="http://islcatalog.ics.forth.gr/el/dataset/lodsyndesisie-collection">Evaluation Collection</a></li> -->
                <li><a href="https://demos.isl.ics.forth.gr/lodsyndesis/">Go to LODsyndesis</a></li>
            </ul>

        </ul>
    </nav>


    <!-- One -->
    <section id="One" class="wrapper style3">
        <div class="inner">
            <header class="align-center">
                <p style="font-size:1.3rem">LODsyndesis Services over ChatGPT <img src="images/gptlods_logo2.png" width="120" /></p>
                <h3>GPT&#x2022;LODS: Annotation and Enrichment Service</h3>
            </header>
        </div>
    </section>

    <!-- Two -->
    <section id="two" class="wrapper style2">
        <div class="inner">
            <div id="main" class="box">
                <div class="content">
                    <header class="align-center">GPT&#x2022;LODS</p>
                        <%if (pag2.equals("")) {%>    
                        <h2>Ask ChatGPT and Get an Annotated Response</h2>
                        <%} else {%>  
                        <div id="intro"><h2><%=intro%></h2></div>
                                <%}%>
                    </header>
                    <%if (pag2.equals("")) {%>
                    <form name="queryForm" >

                        <textarea horizontal-align="justify" rows="2" cols="3" name="text" placeholder="" style="width:70%; display: block;
                                  margin-left: auto;
                                  margin-right: auto; " id="textS">Which is the birth place of Aristotle?</textarea>

                        <!-- <div align="center">Text Samples: <u id="demo" onclick="setText(5)">The Lord of the Rings</u> &nbsp;&nbsp;&nbsp;
                             <u id="demo3" onclick="setText(4)">Nikos Kazantzakis</u>&nbsp;&nbsp;&nbsp;
                             <u id="demo5" onclick="setText(1)">The Godfather</u>&nbsp;&nbsp;&nbsp;

                             <u id="demo4" onclick="setText(3)">The Scorpions (band)</u>&nbsp;&nbsp;&nbsp;
                             <u id="demo2" onclick="setText(2)">1896 Olympic Games</u> </div> -->



                        <br><div align="center">   

                            <b>Select Tool(s) for Entity Recognition:</b>
                            <input type="checkbox" id="tool3" name="tool3" value="WAT" checked>
                            <label for="tool3">WAT</label>
                            <input type="checkbox" id="tool1" name="tool1" value="DBpedia Spotlight" checked>
                            <label for="tool1">DBpedia Spotlight</label>
                            <input type="checkbox" id="tool2" name="tool2" value="Stanford CoreNLP">
                            <label for="tool2">Stanford CoreNLP</label> <br>
                            <b>Select ChatGPT Model:</b> 
                            <input type="radio" id="gpt2" name="gptModel" value="turbo" checked>
                            <label for="gpt2">gpt-3.5-turbo-0301</label>
                            <input type="radio" id="gpt1" name="gptModel" value="davinci">
                            <label for="gpt1">text-davinci-003</label>

                        </div>
                    </form>
                    <div align="center">
                        <button style="text-align: center;" class="button special" id="myBtn" onclick="sendQuery();">Get the Annotated Response</button> 
                    </div>

                    <%}%>
                    <br>
                    <%if (!pag2.equals("")) {%>
                    <br>
                    <p style="text-align:center;">
                        <!--<h3>User Prompt</h3></p>
                        <div id="antext"> -->
                    <div id="antext" style=" border-style: solid;
                         border-radius: 6px;
                         border-width: 2px;
                         border-color: #000;">
                        <%=text%>
                        <!--<p  class="solid" align="justify"></p>-->
                    </div>
                    <br>
                    <%if (!outputRDF.equals("")) {%>
                    <p style="text-align:right;">
                        <button style="text-align: center;" class="button special" id="myBtn2" onclick="rdfa();">HTML+RDFa</button> &nbsp;&nbsp;
                        <button style="text-align: center;" class="button special" id="myBtn3" onclick="sendQuery2();">Ask Again</button>
                    </p>

                    <p style="text-align:center;">
                    <div align="center"> Key Entity <select style="text-align:center;max-width:300px;" id="URIs"></select>  <br>
                        <button style="text-align: center;" class="button special" id="myBtn7" onclick="relFactsURI();">Find and Check Related Facts</button> </div>
                    </p>
                    <%} else {%>
                    <p style="text-align:right;">
                        <button style="text-align: center;" class="button special" id="myBtn7" onclick="show_hide();">Entities Information</button> &nbsp;&nbsp;
                        <button style="text-align: center;" class="button special" id="myBtn7" onclick="relFacts();">Facts between Entities</button> &nbsp;&nbsp;
                        <button style="text-align: center;" class="button special" id="myBtn8" onclick="relFactsNew();">Fact Checking (with relations)</button> &nbsp;&nbsp;
                        <button style="text-align: center;" class="button special" id="myBtn2" onclick="rdfa();">HTML+RDFa</button> &nbsp;&nbsp;
                        <button style="text-align: center;" class="button special" id="myBtn3" onclick="sendQuery2();">Ask Again</button>
                    </p>
                    <%}%>

                    <%}%>
                    <br> 
                    <div id="message">
                        <div class="subContent">

                            <%if (!pag2.equals("")) {%>

                            <header class="align-center">

                                <h2> <%=msg%></h2>
                            </header>

                            <%}%>	


                            <%if (!pag2.equals("")) {%>
                            <jsp:include page="<%=pag2%>" />
                            <%}%>	
                            <%if (!outputRDF.equals("")) {%>
                            <p style="text-align:right;">
                                <button style="text-align: center;" class="button special" id="myBtn25" onclick="rdfFacts();">Export as RDF</button> 
                                &nbsp;&nbsp;
                                <button style="text-align: center;" class="button special" id="myBtn5" onclick="checkMoreFacts();">Check more facts for <%=keyEntity%></button>

                            </p>
                            <%}%>



                        </div>
                    </div>
                    </section>

                    <!-- Footer -->
                    <!-- Footer -->

                    <div class="copyright" style="height:40px;font-size:12px; background-color: black;color:white;text-align:center;padding-top:10px">
                        <a href="http://www.ics.forth.gr/isl/sar/privacy/TermsOfUse-ISL_EN.pdf" class="footer-links" target="_blank">Terms of Use</a>
                        |
                        <a href="http://www.ics.forth.gr/isl/sar/privacy/PrivacyPolicy-ISL_EN.pdf" style="padding-left:0px!important;" class="footer-links" target="_blank">Privacy Policy</a>
                        | &copy; Copyright 2023 FOUNDATION FOR RESEARCH & TECHNOLOGY - HELLAS, All rights reserved.

                    </div>


                    <footer id="footer" style="background-color:#5B5B5B;padding-top:1rem">
                        <img style="float:right;margin-right:20px" src="images/islLogo_En_Main_web_700x237px.png" height="35" >
                        <img  style="float:right;margin-right:25px;" src="images/ics-diskin-en-transparent-white.png" height="35" >
                        <br><br> <p style="float:right;margin-right:20px;color:#D1D1D1;font-size: 12pt;font-family:Myriad Pro"> Designed and developed by <a href="https://www.ics.forth.gr/isl"
                                                                                                                                                             style="color:white">ISL Team</a></p>
                    </footer>
                    <!-- Scripts -->
                    <script src="assets/js/jquery.min.js"></script>
                    <script src="assets/js/jquery.scrollex.min.js"></script>
                    <script src="assets/js/skel.min.js"></script>
                    <script src="assets/js/util.js"></script>
                    <script src="assets/js/main.js"></script>

                    </body>
                    </html>