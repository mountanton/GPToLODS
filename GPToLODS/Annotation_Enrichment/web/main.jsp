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
    </head>
    <body class="subpage">

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
        </nav>

        <section id="One" class="wrapper style3">


            <div class="inner">

                <header class="align-center">

                    <p style="font-size:1.3rem">LODsyndesis Services over ChatGPT <img src="images/gptlods_logo2.png" width="120" /></p>
                    <h3>GPT&#x2022;LODS: Using RDF Knowledge Graphs for Annotating, Enriching and Validating ChatGPT Responses</h3>

                </header>

            </div>
        </section>

        <!-- One -->
        <section id="one" class="wrapper style2">


            <div class="inner" id="services">


                <div class="grid-style">

                    <div>
                        <div class="box">

                            <div class="content">
                                <header class="align-center">
                                    <p>Annotation and Enrichment of ChatGPT Responses</p>
                                    <h2>Annotation and Enrichment Service</h2>
                                </header>
                                <p align="justify"><span class="image right"><a href="Annot_Enrichment"><img src="images/gptlods_logo_an.png" alt=""></a></span>
                                    <a href="Annot_Enrichment">The Annotation and Enrichment Service</a> is an information extraction service that exploits widely used Entity Recognition tools (WAT, Stanford CoreNLP and DBpedia Spotlight)
                                    for recognizing the entities of a given ChatGPT, and enriches the recognized entities by using 400 RDF datasets through <a href=https://demos.isl.ics.forth.gr/lodsyndesis/>LODsyndesis</a>. 
                                    <br><br>The user can browse and export the provenance of each entity, all its equivalent URIs, all its triples, and to find the K most
                                    relevant datasets for that entity, by connecting to LODsyndesis Knowledge Graph.</p>     

                                <footer class="align-center">
                                    <a href="Annot_Enrichment" class="button special">Try this service</a>
                                </footer>
                            </div>
                        </div>
                    </div>

                    <div>
                        <div class="box">
                            <div class="content">
                                <header class="align-center">
                                    <p>ChatGPT to RDF Triples</p>
                                    <h2>Fact Checking and Triples Generation Service</h2>
                                </header>
                                <p align="justify"><span class="image right"><a href="FactChecking"><img src="images/gptlods_logo_fc.png" alt="" /></a></span>
                                    <a href="FactChecking">Fact Checking and Triples Generation Service</a> is a service where a user sends a query to ChatGPT, for a question, entity or a given text,
                                    and this service asks ChatGPT to provide the answer in RDF N-triples format, by using DBpedia ontology. <br><br>
                                    After receiving the response of ChatGPT (in RDF format), the user can either export the triples or validate
                                    the facts by using one or more RDF datasets (such as DBpedia), SPARQL queries, word embeddings and sentence similarity methods.
                                </p>     

                                <footer class="align-center">
                                    <a href="FactChecking" class="button special">Try this service</a>
                                </footer>

                            </div>
                        </div>
                    </div>



                    <div>
                        <div class="box">
                            <div class="content">
                                <header class="align-center">
                                    <p>Annotation and Enrichment</p>
                                    <h2>Tutorial Video and Use Cases of Annotation and Enrichment Service</h2>
                                </header>
                                <p align="center">
                                    <iframe width="450" height="350" src="https://www.youtube.com/embed/H30bSv9NfUw" frameborder="0" allow="autoplay; encrypted-media" allowfullscreen></iframe>
                            </div>
                        </div>
                    </div>

                    <div>
                        <div class="box">
                            <div class="content">
                                <header class="align-center">
                                    <p>Fact Checking and Triples Generation</p>
                                    <h2>Tutorial Video and Use Cases of Fact Checking Service</h2>
                                </header>
                                <p align="center">
                                    <iframe width="450" height="350" src="https://www.youtube.com/embed/5DW1d37aPMc" frameborder="0" allow="autoplay; encrypted-media" allowfullscreen></iframe>
                            </div>
                        </div>
                    </div> 



                </div>


            </div>

            <section id="pub" class="wrapper style3">
                <div class="inner" >
                    <header class="align-center">
                        <p></p>
                        <h2>Publications & Slides</h2>
                    </header>
                    <h3>2023</h3>

                    <p style="font-size: 20px;">M. Mountantonakis and Y. Tzitzikas </br>
                        <a href="https://arxiv.org/pdf/2304.05774">
                            Using Multiple RDF Knowledge Graphs for Enriching ChatGPT Responses</a>,</br>
                        Accepted as Demo in ECML/PKDD 2023
                    </p>
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