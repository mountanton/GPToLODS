<%-- 
    Document   : main
    Created on : 28-May-2014, 15:32:04
    Author     : micha_000
--%>


<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN"
    "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<!DOCTYPE HTML>
<!--
        Released for free under the Creative Commons Attribution 3.0 license (templated.co/license)
-->
<html>
    <head>
        <title>GPToLODS</title>
        <meta charset="utf-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1" />
        <link rel="stylesheet" href="assets/css/main.css" />
        <link rel="stylesheet" href="https://www.w3schools.com/lib/w3.css">
        <link rel="stylesheet" href="https://www.w3schools.com/lib/w3-theme-black.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
        <style>
            .footer-links,	.footer-links:focus, .footer-links:hover {
                text-decoration: none!important;
                color: white!important;
            }
        </style>
    </head>
    <body>

        <!-- Header -->
        <header id="header">
            <div class="logo"><a href="index.jsp">LODsyndesisIE <span>SAR group ISL-FORTH</span></a></div>
            <a href="#menu">Menu</a>
        </header>


        <nav id="menu">
            <ul class="links">
                <li><a href="index.jsp">Home</a></li>
                <li><a href="demo">LODsyndesisIE Demo</a></li>
                <li><a href="rest-api">REST API</a></li>
                <li><a href="https://www.youtube.com/watch?v=i52hY57dRms">Tutorial Video</a></li>
                <li><a href="http://islcatalog.ics.forth.gr/el/dataset/lodsyndesisie-collection">Evaluation Collection</a></li>
                <li><a href="https://demos.isl.ics.forth.gr/lodsyndesis/">Go to LODsyndesis</a></li>
            </ul>
        </nav>

        <section id="One" class="wrapper style3">
            <div class="inner">
                <header class="align-center">
                    <p style="font-size:1.3rem">LODsyndesis Services</p>
                    <h2>Entity Extraction and Enrichment Service</h2>
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
                                    <p>Information Extraction by exploiting LODsyndesis</p>
                                    <h2>LODsyndesisIE Demo Service</h2>
                                </header>
                                <p align="justify">
                                    <a href=demo>LODsyndesisIE</a> is an information extraction service that exploits widely used Entity Recognition tools (WAT, Stanford CoreNLP and DBpedia Spotlight)
                                    for recognizing the entities of a given text, and enriches the recognized entities by using 400 RDF datasets through <a href=https://demos.isl.ics.forth.gr/lodsyndesis/>LODsyndesis</a>. 
                                    <br><br>The user can browse and export the provenance of each entity, all its equivalent URIs, all its triples, and to find the K most
                                    relevant datasets for that entity. Moreover, the user can export the enriched text in HTML+RDFa format and to verify related facts
                                    between the entities of the given text.</p>     

                                <footer class="align-center">
                                    <a href="demo" class="button special">Try this service</a>
                                </footer>
                            </div>
                        </div>
                    </div>

                    <div>
                        <div class="box">
                            <div class="content">
                                <header class="align-center">
                                    <p>Information Extraction by using LODsyndesis</p>
                                    <h2>REST API and JAVA Client</h2>
                                </header>
                                <p align="justify">
                                    A <a href=rest-api#RESTAPI>REST API</a> which offers through GET requests the output of LODsyndesisIE
                                    in several formats (e.g., RDF, CSV) and by using different configuration, e.g.,
                                    by using any combination
                                    of the supported Entity Recognition tools (WAT, Stanford CoreNLP and DBpedia Spotlight).

                                    <br><br><br>
                                    We also offer a <a href=rest-api#JAVACL>REST JAVA Client</a>, for making it feasible to exploit LODsyndesisIE programatically, e.g.,
                                    for integrating the offered services into external services.</p>
                                <br>
                                <footer class="align-center">
                                    <a href="rest-api" class="button special">See more information</a>
                                </footer>
                            </div>
                        </div>
                    </div>



                    <div>
                        <div class="box">
                            <div class="content">
                                <header class="align-center">
                                    <p>LODsyndesisIE</p>
                                    <h2>Tutorial Video</h2>
                                </header>
                                <p align="center">
                                    <iframe width="410" height="290" src="https://www.youtube.com/embed/i52hY57dRms" frameborder="0" allow="autoplay; encrypted-media" allowfullscreen></iframe>
                            </div>
                        </div>
                    </div>

                    <div>
                        <div class="box">
                            <div class="content">
                                <header class="align-center">
                                    <p>LODsyndesisIE</p>
                                    <h2>Evaluation Collection And Results</h2>
                                </header>
                                <p align="justify">
                                    In this <a href="http://islcatalog.ics.forth.gr/el/dataset/lodsyndesisie-collection">link</a>, 
                                    one can download an evaluation collection of 10 texts, and effectiveness and efficiency results for the mentioned collection.
                                    <br><br><br>

                                    These texts contains on average 83.2 words and 15.8 entities, and the target is to recognize the entities of its text and to enrich
                                    their content through LODsyndesis.
                                    <br><br><br>

                                </p> 
                                <footer class="align-center">
                                    <a href="http://islcatalog.ics.forth.gr/el/dataset/lodsyndesisie-collection" class="button special">Browse the collection and the results</a>
                                </footer>
                            </div>
                        </div>
                    </div> 



                </div>


            </div>


        </section>

        
       
        <!-- Footer -->

        <div class="copyright" style="height:40px;font-size:12px; background-color: black;color:white;text-align:center;padding-top:10px">
            <a href="http://www.ics.forth.gr/isl/sar/privacy/TermsOfUse-ISL_EN.pdf" class="footer-links" target="_blank">Terms of Use</a>
            |
            <a href="http://www.ics.forth.gr/isl/sar/privacy/PrivacyPolicy-ISL_EN.pdf" style="padding-left:0px!important;" class="footer-links" target="_blank">Privacy Policy</a>
            | &copy; Copyright 2021 FOUNDATION FOR RESEARCH & TECHNOLOGY - HELLAS, All rights reserved.

        </div>
        <div style="display: block;
             margin-bottom: 8px;margin-top:8px;margin-left: auto;
             margin-right: auto;position:relative;
             text-align:center;"> 
            <img src="images/hfri.PNG" style="margin-right: 20px;" height="70" > 
            <img src="images/gsrt_logo_eng.png"  height="70"> </div>


        <footer id="footer" style="background-color:#5B5B5B;padding-top:1rem">
            <img style="float:right;margin-right:20px" src="images/islLogo_En_Main_web_700x237px.png" height="35" >
            <img  style="float:right;margin-right:25px;" src="images/ics-diskin-en-transparent-white.png" height="35" >
            <br><br> <p style="float:right;margin-right:20px;color:#D1D1D1;font-size: 12pt;font-family:Myriad Pro"> Designed and developed by <a href="https://www.ics.forth.gr/isl"
                                                                                                                                                 style="color:white">ISL Team</a></p>
        </footer>


        <script src="assets/js/jquery.min.js"></script>
        <script src="assets/js/jquery.scrollex.min.js"></script>
        <script src="assets/js/skel.min.js"></script>
        <script src="assets/js/util.js"></script>
        <script src="assets/js/main.js"></script>

    </body>
</html>