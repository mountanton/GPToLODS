# GPToLODs

GPT•LODS: Using RDF Knowledge Graphs for Annotating, Enriching and Validating ChatGPT Responses

https://demos.isl.ics.forth.gr/GPToLODS 

# Annotation and Enrichment Service

The Annotation and Enrichment Service is an information extraction service that exploits widely used Entity Recognition tools (WAT, Stanford CoreNLP and DBpedia Spotlight) for recognizing the entities of a given ChatGPT, and enriches the recognized entities by using 400 RDF datasets through LODsyndesis. The user can browse and export the provenance of each entity, all its equivalent URIs, all its triples, and to find the K most relevant datasets for that entity, by connecting to LODsyndesis Knowledge Graph.

# Fact Checking and Triples Generation Service

Fact Checking and Triples Generation Service is a service where a user sends a query to ChatGPT, for a question, entity or a given text, and this service asks ChatGPT to provide the answer in RDF N-triples format, by using DBpedia ontology. After receiving the response of ChatGPT (in RDF format), the user can either export the triples or validate the facts by using one or more RDF datasets (such as DBpedia), SPARQL queries, word embeddings and sentence similarity methods.
