Cruising through the space of knowledge with HAL
=

You can fly through different embedding spaces.  Embeddings are multidimensional arrays, that model information about the occurrence of something in context of another thing.

One thing is to cruise through knowledge graph embeddings created with [ampligraph](https://github.com/Accenture/AmpliGraph/tree/master/ampligraph), dimension-reduced to 3d. The graph is taken from the wordnet 3.1. dictionary, containing semantic information about similarity, antonymy, hyperonymy and hyponymy and other.

Another is projecting only probabilistic knowledge about semantics to 3d. For example such created with [gensim](https://radimrehurek.com/gensim/)


Prerequisits: Get embeddigns and Database
==

* Install [Neo4j](https://neo4j.com/download-neo4j-now-ms/?utm_source=bing&utm_medium=ppc&utm_campaign=*DE%20-%20Search%20-%20Branded&utm_adgroup=*DE%20-%20Search%20-%20Branded%20-%20Neo4j%20-%20General&utm_term=neo4j&msclkid=5ce9716f488213e0df3b2cd60f6986f4)

* Adjust configuration in `neo4j.conf.`

  * enable reading external files
  
    Comment/uncomment these lines in `neo4j.conf` (you find a sample of this file in this directory)


``` 
dbms.security.allow_csv_import_from_file_urls=true
dbms.directories.import=import

```

   Increase its heap size:
   
   
```
dbms.memory.heap.max_size=3G
```

  * disable authorization for neo4j, because the authentication of the java-driver is more difficult than thought
  
```
dbms.security.auth_enabled=false
```

* Get or produce the 3d knowledge embeddings multidimensional representation of "semantics". With [https://github.com/c0ntradicti0n/allennlp_vs_ampligraph](https://github.com/c0ntradicti0n/allennlp_vs_ampligraph)

   CSV-file is here [https://github.com/c0ntradicti0n/allennlp_vs_ampligraph/blob/master/knowledge_graph_3d_choords.csv](https://github.com/c0ntradicti0n/allennlp_vs_ampligraph/blob/master/knowledge_graph_3d_choords.csv)

   In Neo4j Browser console:
   
   Make an index on the name of the names of Synsets in wordnet:
   
   
```
CREATE INDEX ON :Node(name)
```

   Load the Synstes as words - adapt the path
      
```
LOAD CSV WITH HEADERS FROM 'file:////home/stefan/eclipse-workspace/hal/data/knowledge_graph_3d_choords.csv' AS line
CREATE (:Node { 
	name: line.name, id:line.id, 
	x_pca:toFloat(line.x_pca), y_pca:toFloat(line.y_pca), z_pca:toFloat(line.z_pca), 
	x_tsne:toFloat(line.x_tsne), y_tsne:toFloat(line.y_tsne), z_tsne:toFloat(line.z_tsne), 
	x_k2:toFloat(line.x_k2), y_k2:toFloat(line.y_k2), z_k2:toFloat(line.z_k2), cl_pca:toInteger(line.cl_pca), cl_tsne:toInteger(line.cl_tsne),	cl_k2:toInteger(line.cl_k2), cl_kn:toInteger(line.cl_kn)})
```

   Load relationships (wordnet synonyms, anotnyms, hyponyms) - adapt the path
      
```
LOAD CSV WITH HEADERS FROM 'file:///home/.../eclipse-workspace/hal/knowledge_graph_rels.csv' AS line
MATCH (n1:Node {name:line.n1}), (n2:Node {name:line.n2})
WHERE id(n1)>id(n2)
MERGE (n1)-[:R{kind:line.rel}]->(n2)
```

   In need to delete all neo4j content:

```
MATCH (n) DETACH DELETE n;
```


* Get Java 8, Eclipse IDE and JMonkey 3

   Import this repository in Eclipse, following this tutorial: [https://wiki.jmonkeyengine.org/jme3/setting_up_jme3_in_eclipse.html](https://wiki.jmonkeyengine.org/jme3/setting_up_jme3_in_eclipse.html)

Run and Cruise
==

Press play cruise 

* Use ´awsd´ or arrow key to move, use your mouse for your angle.

* Change the kind of embeddings between 

   * [https://en.wikipedia.org/wiki/Principal_component_analysis](https://en.wikipedia.org/wiki/Principal_component_analysis)

   * [https://en.wikipedia.org/wiki/T-distributed_stochastic_neighbor_embedding](https://en.wikipedia.org/wiki/T-distributed_stochastic_neighbor_embedding]

   * [just 3d graph embeddings, trained with ´k=3´](https://github.com/Accenture/AmpliGraph/)

* Change the embeddings

   * GloVe and other gensim-embeddings follow
