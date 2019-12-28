import pandas

table = pandas.read_csv("knowledge_graph_3d_choords.csv", index_col=0)
things = ['pca', 'tsne', 'k2', 'kn']

for kind in things:
    print ("writing table for %s " % kind)
    table['cl'] = table['cl_%s' % kind]
    table.groupby(by='cl').mean().reset_index().to_csv('%s_clusters_mean_points.csv' % kind, sep='\t', header=True,
                                                                  index=False)
