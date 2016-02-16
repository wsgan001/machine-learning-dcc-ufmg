# JIDM and in Itamar's Dissertation #
[Download code and datasets](http://machine-learning-dcc-ufmg.googlecode.com/files/datasets-itamar-20-11-2013.tar.bz2)

[Download datasets](http://machine-learning-dcc-ufmg.googlecode.com/files/datasets-itamar-20-11-2013.tar.bz2)

Codes and datasets used in the [Itamar's dissertation](https://docs.google.com/viewer?a=v&pid=sites&srcid=ZGVmYXVsdGRvbWFpbnxpdGFtYXJoYXRhfGd4OjM1NTg5YTBlNGY4Y2Q1ZWY) and in the paper
[Hata, I.; Veloso, A. & Ziviani, N. (2013). Learning Accurate and Interpretable Classifiers Using Optimal Multi-Criteria Rules. Information and Data Management, 4(3):204--219.](http://seer.lcc.ufmg.br/index.php/jidm/article/view/239).
We provided the codes and the datasets here to facilitate the reproducibility of our work. The datasets have already been discretized and are in LAC's format.

### Abstract ###
The Occam’s Razor principle has become the basis for many Machine Learning algorithms, under the
interpretation that the classifier should not be more complex than necessary. Recently, this principle has shown to be
well suited to associative classifiers, where the number of rules composing the classifier can be substantially reduced
by using condensed representations such as maximal or closed rules. While it is shown that such a decrease in the
complexity of the classifier (usually) does not compromise its accuracy, the number of remaining rules is still larger than
necessary and making it hard for experts to interpret the corresponding classifier. In this paper we propose a much more
aggressive filtering strategy, which decreases the number of rules within the classifier dramatically without hurting its
accuracy. Our strategy consists in evaluating each rule under different statistical criteria, and filtering only those rules
that show a positive balance between all the criteria considered. Specifically, each candidate rule is associated with a
point in an n-dimensional scattergram, where each coordinate corresponds to a statistical criterion. Points that are not
dominated by any other point in the scattergram compose the Pareto frontier, and correspond to rules that are optimal
in the sense that there is no rule that is better off when all the criteria are taken into account. Finally, rules lying in the
Pareto frontier are filtered and compose the classifier. Our Pareto-Optimal filtering strategy may receive as input either
the entire set of rules or even a condensed representation (i.e., closed rules). A systematic set of experiments involving
benchmark data as well as recent data from actual application scenarios, followed by an extensive set of significance
tests, reveal that the proposed strategy decreases the number of rules by up to two orders of magnitude and produces
classifiers that are extremely readable (i.e., allow interpretability of the classification results) without hurting accuracy.


### Other Softwares ###

In addition to the softwares presented in this page, there are others that we
implemented:  [LAC-cpp](http://code.google.com/p/machine-learning-dcc-ufmg/wiki/LACLazyAssociativeAlgorithmCpp) , MultiIntervalDiscretization and ParetoFrontier.